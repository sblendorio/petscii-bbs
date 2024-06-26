package org.zmpp.textui;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.zmpp.ExecutionControl;
import org.zmpp.base.StoryFileHeader;
import org.zmpp.vm.InvalidStoryException;
import org.zmpp.vm.Machine;
import org.zmpp.vm.MachineFactory;
import org.zmpp.vm.MachineRunState;
import org.zmpp.windowing.AnnotatedCharacter;
import org.zmpp.windowing.AnnotatedText;
import org.zmpp.windowing.BufferedScreenModel;
import org.zmpp.windowing.ScreenModel;
import org.zmpp.windowing.ScreenModelListener;
import org.zmpp.windowing.BufferedScreenModel.StatusLineListener;

/**
 * Mixes activites found in ZmppFrame, ScreenModelView and ScreenModelSplitView (swing client implementation)
 */
public class CliScreenModel implements ScreenModelListener,StatusLineListener {

    private String topRoomDescription ="";

    /*
    * Screen Model manages a virtual window
    */
    private final BufferedScreenModel screenModel = new BufferedScreenModel();

    /*
     * The actual Zork adventure file
     */
    private final InputStream storyFile = CliScreenModel.class.getResourceAsStream("/zmpp/bureaucracy-r160.z4");
    //private final InputStream storyFile = BBSScreenModel.class.getResourceAsStream("/games/zork1.z3");
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


    public static void main(String[] args) throws Exception {

        BufferedReader bsr = new BufferedReader(new InputStreamReader(System.in));
        CliScreenModel model = new CliScreenModel();
        resizeScreen(model.getExecutionControl().getMachine(), 25, 80);
        model.run();
        while(model.getCurrentRunState() != MachineRunState.STOPPED){
            if(model.getCurrentRunState().isWaitingForInput()){
                System.out.println("<------ PRIMA. readchar="+model.getCurrentRunState().isReadChar()+", readline="+model.getCurrentRunState().isReadLine()+", none="+model.getCurrentRunState().isWaitingForInput() +"---------->");
                if (model.getCurrentRunState().isReadChar()) {
                    System.out.println("* "+ model.topRoomDescription);
                }
                String inputline = bsr.readLine();
                System.out.println("<------ DOPO ---------->");
                if (model.getCurrentRunState().isReadLine()) {
                    model.setCurrentRunState(model.getExecutionControl().resumeWithInput(inputline));
                } else if (model.getCurrentRunState().isReadChar()) {
                    inputline.chars().forEach(c -> model.getExecutionControl().resumeWithInput(String.valueOf((char) c)));
                    model.setCurrentRunState(model.getExecutionControl().resumeWithInput("\n"));
                }
            }
        }


    }
    //usato solo per pulire la console
    public static void clearScreen() {  
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    }  


    public CliScreenModel() throws IOException,InvalidStoryException{
        
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
        CliScreenModel.clearScreen();
        List<AnnotatedText> text = ((BufferedScreenModel) screenModel).getLowerBuffer();
        //questo testo va stampato, se si vuole, in alto a sx e contiene la descrizione, carattere per carattere, del luogo in cui ci si trova (es "A ovest della casa")
        //System.out.println(topRoomDescription);
        for (AnnotatedText segment : text) {
            // example TextAnnotation, fixed: false bold: false italic: false reverse: false bg: 1 fg: 1
            String  annotation = segment.getAnnotation().toString();
           System.out.print(segment.getText().replace('\r','\n'));
        }
    }

    public static void resizeScreen(Machine machine, int numRows, int numCharsPerRow) {
        if (machine.getVersion() >= 4) {
            machine.writeUnsigned8(StoryFileHeader.SCREEN_HEIGHT, (char) numRows);
            machine.writeUnsigned8(StoryFileHeader.SCREEN_WIDTH,
                    (char) numCharsPerRow);
        }
        if (machine.getVersion() >= 5) {
            machine.getFileHeader().setFontHeight(1);
            machine.getFileHeader().setFontWidth(1);
            machine.writeUnsigned16(StoryFileHeader.SCREEN_HEIGHT_UNITS,
                    (char) numRows);
            machine.writeUnsigned16(StoryFileHeader.SCREEN_WIDTH_UNITS,
                    (char) numCharsPerRow);
        }
    }

    @Override
    public void topWindowUpdated(int cursorx, int cursory, AnnotatedCharacter c) {

        topRoomDescription += ""+c.getCharacter();

    }

    @Override
    public void screenSplit(int linesUpperWindow) {
        System.out.println("linesup:"+linesUpperWindow);
    }

    @Override
    public void windowErased(int window) {
        System.out.println("BUREAU. windowErased, window="+window);
    }

    @Override
    public void topWindowCursorMoving(int line, int column) {
        System.out.println("top window cursor moving :"+line+":"+column+", topRoomDescription="+this.topRoomDescription);
        this.topRoomDescription = "";
    }

    @Override
    public void statusLineUpdated(String objectDescription, String status) {
        System.out.println(objectDescription+" "+status);
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'statusLineUpdated'");
    }



    
}
