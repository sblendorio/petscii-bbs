package org.zmpp.textui;

import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.Utils;
import org.apache.commons.collections4.CollectionUtils;
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
import java.util.*;

import static eu.sblendorio.bbs.core.HtmlUtils.inferDiacritics;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class BbsScreenModel implements ScreenModelListener, StatusLineListener, SaveGameDataStore {

    private static final String SAVE_FILE_PATH = System.getProperty("user.home") + File.separator + "saved-text-adventures";

    private static Logger logger = LogManager.getLogger("org.zmpp.screen");

    private String topRoomDescription = "";

    private final BufferedScreenModel screenModel = new BufferedScreenModel(); // Screen Model manages a virtual window

    private final String nameOfTheGame;
    // The actual Zork adventure file
    private final InputStream storyFile;

    private BbsThread bbsThread = null;

    private boolean firstNewline = false;
    private Runnable boldOn = null;
    private Runnable boldOff = null;
    private Runnable afterPaging = null;
    private Map<String, Runnable> overrides = null;

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
                String inputLine = (".".equals(rawInputLine) ? "quit" : inferDiacritics(rawInputLine)).trim();
                if ("..".equalsIgnoreCase(inputLine)) {

                    break;
                }
                if (overrides != null && overrides.containsKey(inputLine.toLowerCase())) {
                    overrides.get(inputLine.toLowerCase()).run();
                } else {
                    setCurrentRunState(getExecutionControl().resumeWithInput(inputLine));
                }
            }
        }
        bbsThread.println();
        bbsThread.println("PRESS ANY KEY.");
        bbsThread.flush();
        bbsThread.resetInput();
        bbsThread.keyPressed(86_400_000);
    }

    //usato solo per pulire la console
    public void clearScreen() {
        /*bbsThread.cls();
        bbsThread.flush();*/
        bbsThread.optionalCls();
    }
    public BbsScreenModel(String nameOfTheGame, byte[] storyFile, BbsThread bbsThread) throws IOException,InvalidStoryException {
        this(nameOfTheGame, storyFile, bbsThread, 0, null, null, null);
    }

    public BbsScreenModel(String nameOfTheGame, byte[] storyFile, BbsThread bbsThread, int nlines) throws IOException,InvalidStoryException {
        this(nameOfTheGame, storyFile, bbsThread, nlines, null, null, null);
    }

    public BbsScreenModel(String nameOfTheGame, byte[] storyFile, BbsThread bbsThread, int nlines, Runnable boldOn, Runnable boldOff) throws IOException,InvalidStoryException {
        this(nameOfTheGame, storyFile, bbsThread, nlines, boldOn, boldOff, null);
    }

    public BbsScreenModel(String nameOfTheGame, byte[] storyFile, BbsThread bbsThread, int nlines, Runnable boldOn, Runnable boldOff, Runnable afterPaging) throws IOException,InvalidStoryException {
        this(nameOfTheGame, storyFile, bbsThread, nlines, boldOn, boldOff, afterPaging, null);
    }

    public BbsScreenModel(String nameOfTheGame, byte[] storyFile, BbsThread bbsThread, int nlines, Runnable boldOn, Runnable boldOff, Runnable afterPaging, Map<String, Runnable> overrides) throws IOException,InvalidStoryException{
        this.nameOfTheGame = nameOfTheGame;
        this.bbsThread = bbsThread;
        this.storyFile = new ByteArrayInputStream(storyFile);
        this.nlines = nlines;
        this.boldOn = boldOn;
        this.boldOff = boldOff;
        this.afterPaging = afterPaging;
        this.overrides = overrides;
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
            if (firstNewline) {
                nlines++;
                bbsThread.println();
                bbsThread.checkBelowLine();
                checkForScreenPaging();
            }
            Optional.ofNullable(boldOn).ifPresent(Runnable::run);
        } else {
            Optional.ofNullable(boldOff).ifPresent(Runnable::run);
        }
        firstNewline = true;

        String text = bbsThread.preprocessDiacritics(
            segment
            .getText()
            .replace("\r\n", "\n")
            .replace("\r", "\n")
            .replaceAll("\\[\\*\\* Programming error: tried to print \\(char\\) [0-9]+, which is not a valid ZSCII character code for output \\*\\*\\]", "")
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
                nlines++;
                bbsThread.println();
                bbsThread.checkBelowLine();
                checkForScreenPaging();
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
                    .includeExtraWordChars("0123456789()")
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
                Optional.ofNullable(boldOff).ifPresent(Runnable::run);
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
        Utils.mkdir(SAVE_FILE_PATH);

        RandomAccessFile raf = null;
        String currentdir = new File(SAVE_FILE_PATH).getAbsolutePath();
        try {
            bbsThread.newline();
            File saveFile;
            boolean sure = true;
            do {
                bbsThread.print("Filename: ");
                bbsThread.flush();
                bbsThread.resetInput();
                String filename = bbsThread.readLine();
                filename = filename.trim().replaceAll("[^a-zA-Z0-9-._ ]", "").toLowerCase();
                if (isBlank(filename) || filename.trim().equals("..") || filename.trim().equals(".")) {
                    bbsThread.println("Aborted.");
                    return false;
                }
                saveFile = new File(
                        currentdir + File.separator + nameOfTheGame + "-" + filename + ".ziff"
                );
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
        String currentdir = new File(SAVE_FILE_PATH).getAbsolutePath();
        try {
            bbsThread.newline();
            bbsThread.print("Filename: ");
            bbsThread.flush();
            bbsThread.resetInput();
            String filename = bbsThread.readLine();
            filename = filename.trim().replaceAll("[^a-zA-Z0-9-._ ]", "").toLowerCase();
            if (isBlank(filename) || filename.trim().equals("..") || filename.trim().equals(".")) {
                bbsThread.println("Aborted.");
                return null;
            }
            File loadFile = new File(
                    currentdir + File.separator + nameOfTheGame + "-" + filename + ".ziff"
            );
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
