package org.zmpp.textui;

import eu.sblendorio.bbs.core.BbsThread;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.davidmoten.text.utils.WordWrap;
import org.zmpp.ExecutionControl;
import org.zmpp.base.DefaultMemory;
import org.zmpp.iff.DefaultFormChunk;
import org.zmpp.iff.FormChunk;
import org.zmpp.iff.WritableFormChunk;
import org.zmpp.vm.InvalidStoryException;
import org.zmpp.vm.MachineFactory;
import org.zmpp.vm.MachineRunState;
import org.zmpp.vm.SaveGameDataStore;
import org.zmpp.windowing.*;
import org.zmpp.windowing.BufferedScreenModel.StatusLineListener;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static eu.sblendorio.bbs.core.HtmlUtils.inferDiacritics;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class BbsScreenModel implements ScreenModelListener, StatusLineListener, SaveGameDataStore {
    private static Logger logger = LogManager.getLogger("org.zmpp.screen");

    private String topRoomDescription ="";

    private final BufferedScreenModel screenModel = new BufferedScreenModel(); // Screen Model manages a virtual window

    // The actual Zork adventure file
    private final InputStream storyFile;

    private BbsThread bbsThread = null;

    private Runnable boldOn = null;
    private Runnable boldOff = null;
    private Runnable afterPaging = null;

    int nlines;

    private ExecutionControl executionControl; // Execution control setup and run the Zork VM, constrol user input

    private MachineRunState currentRunState;

    public MachineRunState getCurrentRunState() {
        return currentRunState;
    }

    public void setCurrentRunState(MachineRunState runState) {
        this.currentRunState = runState;
    }

    public ExecutionControl getExecutionControl() {
        return executionControl;
    }

    public void runTheGame() throws Exception {
        setCurrentRunState(this.executionControl.run()); // Starting the game
        while (getCurrentRunState() != MachineRunState.STOPPED) {
            if (getCurrentRunState().isWaitingForInput()) {
                bbsThread.flush();
                bbsThread.resetInput();
                String rawInputLine = bbsThread.readLine();
                String inputLine = ".".equals(rawInputLine) ? "q" : inferDiacritics(rawInputLine);
                setCurrentRunState(getExecutionControl().resumeWithInput(inputLine));
            }
        }
    }

    //usato solo per pulire la console
    public void clearScreen() {
        /*bbsThread.cls();
        bbsThread.flush();*/
        bbsThread.optionalCls();
    }
    public BbsScreenModel(byte[] storyFile, BbsThread bbsThread) throws IOException,InvalidStoryException {
        this(storyFile, bbsThread, 0, null, null, null);
    }

    public BbsScreenModel(byte[] storyFile, BbsThread bbsThread, int nlines) throws IOException,InvalidStoryException {
        this(storyFile, bbsThread, nlines, null, null, null);
    }

    public BbsScreenModel(byte[] storyFile, BbsThread bbsThread, int nlines, Runnable boldOn, Runnable boldOff) throws IOException,InvalidStoryException {
        this(storyFile, bbsThread, nlines, boldOn, boldOff, null);
    }
    public BbsScreenModel(byte[] storyFile, BbsThread bbsThread, int nlines, Runnable boldOn, Runnable boldOff, Runnable afterPaging) throws IOException,InvalidStoryException{
        this.bbsThread = bbsThread;
        this.storyFile = new ByteArrayInputStream(storyFile);
        this.nlines = nlines;
        this.boldOn = boldOn;
        this.boldOff = boldOff;
        this.afterPaging = afterPaging;
        // Zork VM configuration
        MachineFactory.MachineInitStruct initStruct = new MachineFactory.MachineInitStruct();
        this.screenModel.addScreenModelListener(this);

        initStruct.storyFile = this.storyFile;
        initStruct.screenModel = this.screenModel;
        initStruct.statusLine = this.screenModel;
        initStruct.saveGameDataStore = this;

        this.executionControl = new ExecutionControl(initStruct);

        // Wiring game with the virtual window
        this.screenModel.init(this.executionControl.getMachine(), this.executionControl.getZsciiEncoding());
    }

    // Implementation of ScreenModelListener
    @Override
    public void screenModelUpdated(ScreenModel screenModel) {
        clearScreen();
        List<AnnotatedText> text = ((BufferedScreenModel) screenModel).getLowerBuffer();
        //questo testo va stampato, se si vuole, in alto a sx e contiene la descrizione, carattere per carattere, del luogo in cui ci si trova (es "A ovest della casa")
        //logger.debug((topRoomDescription);
        for (AnnotatedText segment : text) {
            // example TextAnnotation, fixed: false bold: false italic: false reverse: false bg: 1 fg: 1
            String annotation = segment.getAnnotation().toString();
            showText(segment);
        }
    }

    private void showText(AnnotatedText segment) {
        if (segment == null || segment.getText() == null || segment.getText().isBlank())
            return;

        if (segment.getAnnotation().isBold()) {
            Optional.ofNullable(boldOn).ifPresent(Runnable::run);
            bbsThread.println();
            nlines++;
        } else {
            Optional.ofNullable(boldOff).ifPresent(Runnable::run);
        }

        String text = bbsThread.preprocessDiacritics(
            segment
            .getText()
            .replace("\r\n", "\n")
            .replace("\r", "\n")
        );
        List<String> lines = wordWrap(text);
        boolean firstNonEmpty = true;
        for (int i=0; i<lines.size(); i++) {
            String s = lines.get(i);
            if (firstNonEmpty && s.isBlank()) continue;
            firstNonEmpty = false;
            bbsThread.print(s);
            if (i < lines.size() - 1) {
                nlines++;
                bbsThread.println();
                bbsThread.checkBelowLine();
                checkForScreenPaging();
            } else if (s.trim().endsWith(">") || s.trim().endsWith("?")) {
                nlines = 0;
            } else {
                bbsThread.println();
                nlines++;
            }
        }

        bbsThread.flush();

    }

    @Override
    public void topWindowUpdated(int cursorx, int cursory, AnnotatedCharacter c) {
        topRoomDescription += ""+c.getCharacter();
    }

    @Override
    public void screenSplit(int linesUpperWindow) {
        logger.debug("linesup:"+linesUpperWindow);
    }

    @Override
    public void windowErased(int window) {
        // TODO Auto-generated method stub
        logger.debug("Unimplemented method 'windowErased'");
    }

    @Override
    public void topWindowCursorMoving(int line, int column) {
        logger.debug("top window cursor moving :"+line+":"+column);
        this.topRoomDescription = "";
    }

    @Override
    public void statusLineUpdated(String objectDescription, String status) {
        // TODO Auto-generated method stub
        logger.debug("Unimplemented method 'statusLineUpdated'. " + objectDescription+" "+status);
    }

    protected List<String> wordWrap(String s) {
        String[] cleaned = s.split("\n");
        List<String> result = new ArrayList<>();
        for (String item: cleaned) {
            String[] wrappedLine = WordWrap
                    .from(item)
                    .maxWidth(bbsThread.getScreenColumns() - 1)
                    .newLine("\n")
                    .breakWords(false)
                    .wrap()
                    .split("\n");
            result.addAll(Arrays.asList(wrappedLine));
        }
        return result;
    }

    private void checkForScreenPaging() {
        try {
            if (nlines >= bbsThread.getScreenRows() - 1) {
                bbsThread.print(bbsThread.getScreenColumns() >= 40
                        ? "--- ANY KEY FOR NEXT PAGE -------------"
                        : "--- MORE ------------");
                bbsThread.flush();
                bbsThread.resetInput();
                bbsThread.readKey();
                if (afterPaging == null) {
                    bbsThread.println();
                    bbsThread.println();
                    bbsThread.optionalCls();
                } else {
                    afterPaging.run();
                }
                nlines = 0;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Implementation of SaveGameDataStore
    @Override
    public boolean saveFormChunk(final WritableFormChunk formchunk) {
        RandomAccessFile raf = null;
        String currentdir = new File(System.getProperty("user.dir")).getAbsolutePath();
        try {
            bbsThread.newline();
            File saveFile;
            boolean sure = true;
            do {
                bbsThread.print("Filename: ");
                bbsThread.flush();
                bbsThread.resetInput();
                String filename = bbsThread.readLine();
                if (isBlank(filename)) {
                    bbsThread.println("Aborted.");
                    return false;
                }
                saveFile = new File(currentdir + File.separator + filename.toLowerCase() + ".ziff");
                if (saveFile.exists()) {
                    bbsThread.println("WARNING: File already exists.");
                    bbsThread.print("Keep going with this? (Y/N) ");
                    bbsThread.flush();
                    bbsThread.resetInput();
                    String line = bbsThread.readLine();
                    if (isBlank(line)) {
                        bbsThread.println("Aborted.");
                        return false;
                    }
                    final String response = defaultString(line).trim().toLowerCase();
                    sure = response.equals("y") || response.equals("yes");
                }
            } while (!sure);
            raf = new RandomAccessFile(saveFile, "rw");
            byte[] data = formchunk.getBytes();
            raf.write(data);
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (raf != null) try { raf.close(); } catch (Exception ex) { }
        }

        return false;
    }

    @Override
    public FormChunk retrieveFormChunk() {
        RandomAccessFile raf = null;
        String currentdir = new File(System.getProperty("user.dir")).getAbsolutePath();
        try {
            bbsThread.newline();
            bbsThread.print("Filename: ");
            bbsThread.flush();
            bbsThread.resetInput();
            String filename = bbsThread.readLine();
            if (isBlank(filename)) {
                bbsThread.println("Aborted.");
                return null;
            }
            File loadFile = new File(currentdir + File.separator + filename.toLowerCase() + ".ziff");
            if (!loadFile.exists()) {
                bbsThread.println("File not found. Aborted.");
                return null;
            }
            raf = new RandomAccessFile(loadFile, "r");
            byte[] data = new byte[(int) raf.length()];
            raf.readFully(data);
            return new DefaultFormChunk(new DefaultMemory(data));
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (raf != null) try { raf.close(); } catch (Exception ex) { }
        }

        return null;
    }

}
