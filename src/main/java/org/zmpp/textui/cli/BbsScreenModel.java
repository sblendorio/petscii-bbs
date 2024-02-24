package org.zmpp.textui.cli;


import eu.sblendorio.bbs.core.BbsThread;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.davidmoten.text.utils.WordWrap;
import org.zmpp.ExecutionControl;
import org.zmpp.vm.InvalidStoryException;
import org.zmpp.vm.MachineFactory;
import org.zmpp.vm.MachineRunState;
import org.zmpp.windowing.*;
import org.zmpp.windowing.BufferedScreenModel.StatusLineListener;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Mixes activites found in ZmppFrame, ScreenModelView and ScreenModelSplitView (swing client implementation)
 */
public class BbsScreenModel implements ScreenModelListener,StatusLineListener {
    private static Logger logger = LogManager.getLogger(BbsScreenModel.class);

    private String topRoomDescription ="";

    /*
    * Screen Model manages a virtual window
    */
    private final BufferedScreenModel screenModel = new BufferedScreenModel();

    /*
     * The actual Zork adventure file
     */
    private final InputStream storyFile;
    //private final InputStream storyFile = BbsScreenModel.class.getResourceAsStream("/zmpp/Zork-1-ITA-v7.z5");
    //private final InputStream storyFile = BBSScreenModel.class.getResourceAsStream("/games/zork1.z3");
    private final BbsThread bbsThread;
    int nlines;

    /*
         * Execution control setup and run the Zork VM, constrol user input
         */
    private ExecutionControl executionControl;



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
        run();
        while (getCurrentRunState() != MachineRunState.STOPPED) {
            if (getCurrentRunState().isWaitingForInput()) {
                bbsThread.flush();
                bbsThread.resetInput();
                String inputLine = bbsThread.readLine();
                setCurrentRunState(getExecutionControl().resumeWithInput(inputLine));
            }
        }
    }
    //usato solo per pulire la console
    public void clearScreen() {
        /*bbsThread.cls();
        bbsThread.flush();*/
    }
    public BbsScreenModel(byte[] storyFile, BbsThread bbsThread) throws IOException,InvalidStoryException {
        this(storyFile, bbsThread, 0);
    }

    public BbsScreenModel(byte[] storyFile, BbsThread bbsThread, int nlines) throws IOException,InvalidStoryException{
        this.bbsThread = bbsThread;
        this.storyFile = new ByteArrayInputStream(storyFile);
        this.nlines = nlines;
        /*
         * Zork VM configuration
         */
        MachineFactory.MachineInitStruct initStruct = new MachineFactory.MachineInitStruct();
        
        this.screenModel.addScreenModelListener(this);
        
        
        initStruct.storyFile = this.storyFile;
        initStruct.screenModel = this.screenModel;
        initStruct.statusLine = this.screenModel; 

        this.executionControl = new ExecutionControl(initStruct);

        
        /*
         * Wiring game with the virtual window
         */
        this.screenModel.init(this.executionControl.getMachine(), this.executionControl.getZsciiEncoding());



        
    }

    public void run(){

        /*
         * Starting the game
         */
        this.setCurrentRunState(this.executionControl.run());

    }

   
    //Implementation of screenModellistener

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
        if (segment == null || segment.getText() == null || segment.getText().isBlank()) return;

        String text = segment
                .getText()
                .replace("\r\n", "\n")
                .replace("\r", "\n");

        wordWrap(text).forEach(s -> {
            bbsThread.print(s);
            if (!s.endsWith(">")) {
                nlines++;
                bbsThread.println();
                bbsThread.checkBelowLine();
                checkForScreenPaging();
            }
        });
        /*
                List<String> rows = wordWrap(text);
for (int i=0; i<rows.size(); i++) {
            String row = rows.get(i);
            bbsThread.print(row);
            if (!row.trim().endsWith(">")) {
                bbsThread.println();
            }
        }*/

        bbsThread.flush();
        nlines = 0;

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
        throw new UnsupportedOperationException("Unimplemented method 'windowErased'");
    }

    @Override
    public void topWindowCursorMoving(int line, int column) {
        logger.debug("top window cursor moving :"+line+":"+column);
        this.topRoomDescription = "";
    }

    @Override
    public void statusLineUpdated(String objectDescription, String status) {
        logger.debug(objectDescription+" "+status);
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'statusLineUpdated'");
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
        if (nlines >= bbsThread.getScreenRows() - 1) {
            bbsThread.print("--- ANY KEY FOR NEXT PAGE -------------");
            try {
                bbsThread.flush();
                bbsThread.resetInput();
                bbsThread.readKey();
                bbsThread.println();
                bbsThread.println();
                bbsThread.optionalCls();
                nlines = 0;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
