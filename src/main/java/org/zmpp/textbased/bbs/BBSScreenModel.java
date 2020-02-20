package org.zmpp.textbased.bbs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.text.WordUtils;
import org.zmpp.encoding.ZsciiEncoding;
import org.zmpp.io.OutputStream;
import org.zmpp.vm.Machine;
import org.zmpp.vm.ScreenModel;
import org.zmpp.vm.StatusLine;
import org.zmpp.vm.TextCursor;

import eu.sblendorio.bbs.core.Colors;
import eu.sblendorio.bbs.core.Keys;
import eu.sblendorio.bbs.core.PetsciiThread;

public class BBSScreenModel implements ScreenModel, OutputStream, StatusLine {

    PetsciiThread petsciiThread;
    Machine machine;
    BBSInputStream cliInputStream;
    StringBuffer buffer;

    String adventureName = "";
    int score = 0;
    int steps = 0;
    int hours = 0;
    int minutes = 0;


    static int BUFFER_LENGTH = 880; // = 40 columns x 22 rows (+1 for title + 2 for input)  

    private boolean isSelected = false;

    public BBSScreenModel(PetsciiThread petsciiThread, Machine machine) {
        this.petsciiThread = petsciiThread;
        this.machine = machine;
        buffer = new StringBuffer(BUFFER_LENGTH);
    }

    private void trimAndPrintBuffer(){

        String[] rows = buffer.toString().split("\n");
        String newContent = rows[rows.length-1];
        int maxRows = 21;

        int index = rows.length-2;
        String row = "";
        
        
        while(maxRows>0 && index >= 0){
            row = rows[index];
            int rowCount   = row.length() / 40;
            if(rowCount == 0 ){
                rowCount = 1;
            }
            if(rowCount<=maxRows){
                maxRows = maxRows - rowCount;
                newContent = row + "\n" + newContent;
            }else{
                newContent =  row.substring(maxRows*40) +  "\n" + newContent;
                maxRows = 0;
            }
            index--;
            
        }
        buffer.delete(0, buffer.length());
        buffer.append(newContent);

        cbmPrintString(buffer.toString());


        
    }

    private void cbmPrintString(String value){
        String[] rows = value.toString().split("\n");
        for(int i = 0; i< rows.length; i++){
            this.petsciiThread.print(rows[i]);
            this.petsciiThread.newline();
        }
    }

    private void printStatusBar(){
        String rightStatus = score+"/"+steps;
        int spaces = 40 - adventureName.length() - 1 - rightStatus.length();
        StringBuffer spacesString = new StringBuffer();
        for(int i = 0 ; i< spaces;i++){
            spacesString.append(" ");
        }
        String statusBar = String.format("%s:%s%s",adventureName,spacesString.toString(),rightStatus);
        petsciiThread.write(Colors.WHITE);
        petsciiThread.print(statusBar);
        petsciiThread.write(Colors.GREY1);
        petsciiThread.newline();
    }

    @Override
    public void reset() {
        petsciiThread.log("reset not yet implemented");
    }

    @Override
    public void splitWindow(int linesUpperWindow) {
        petsciiThread.log("splitWindow not yet implemented");
    }

    @Override
    public void setWindow(int window) {
        petsciiThread.log("setWindow not yet implemented");

    }

    @Override
    public void setTextStyle(int style) {
        petsciiThread.log("setTextStyle not yet implemented");

    }

    @Override
    public void setBufferMode(boolean flag) {
        petsciiThread.log("setBufferMode not yet implemented");

    }

    @Override
    public void updateStatusScore(String objectName, int score, int steps) {
        this.adventureName = objectName;
        this.score = score;
        this.steps = steps;
    }

    @Override
    public void updateStatusTime(String objectName, int hours, int minutes) {
        this.adventureName = objectName;
        this.hours = hours;
        this.minutes = minutes;
    }

    @Override
    public void eraseLine(int value) {
        petsciiThread.write(Keys.HOME);
        petsciiThread.flush();

    }

    @Override
    public void eraseWindow(int window) {
        petsciiThread.log("eraseWindow not yet implemented");

    }

    @Override
    public void setTextCursor(int line, int column, int window) {
        petsciiThread.log("setTextCursor not yet implemented");

    }

    @Override
    public TextCursor getTextCursor() {
        petsciiThread.log("getTextCursor not yet implemented");
        return null;
    }

    @Override
    public void setPaging(boolean flag) {
        petsciiThread.log("setPaging not yet implemented");

    }

    @Override
    public int setFont(int fontnumber) {
        petsciiThread.log("setFont not yet implemented");
        return 0;
    }

    @Override
    public void setBackgroundColor(int colornumber, int window) {
        petsciiThread.log("setBackgroundColor not yet implemented");
    }

    @Override
    public void setForegroundColor(int colornumber, int window) {
        petsciiThread.log("setForegroundColor not yet implemented");
    }

    @Override
    public void redraw() {
/* TODO FIX SBLEND
        petsciiThread.write(Keys.CLR); // clear screen
         petsciiThread.flush();                  // flush 
         printStatusBar();
         trimAndPrintBuffer();
         petsciiThread.flush();

 */
    }

    @Override
    public void displayCursor(boolean flag) {}

    @Override
    public OutputStream getOutputStream() {
        return this;
    }

    @Override
    public void waitInitialized() {}

    @Override
    public void resetPagers() {}

    protected List<String> wordWrap(String s) {
        String[] cleaned = s.split("\n");
        List<String> result = new ArrayList<>();
        for (String item: cleaned) {
            String[] wrappedLine = WordUtils
                    .wrap(item, 39, "\n", true)
                    .split("\n");
            result.addAll(Arrays.asList(wrappedLine));
        }
        return result;
    }

    @Override
    public void print(short zsciiChar, boolean isInput) {
        if (isInput) {
            if (zsciiChar == ZsciiEncoding.NEWLINE || zsciiChar == ZsciiEncoding.NEWLINE_10) {
                petsciiThread.newline();
                petsciiThread.flush();
            } else if (zsciiChar != ZsciiEncoding.INSTDEL && zsciiChar != -1) {
                char c = machine.getGameData().getZsciiEncoding().getUnicodeChar(zsciiChar);
                petsciiThread.print("" + c);
                petsciiThread.flush();
            }
        } else {
            if (zsciiChar == ZsciiEncoding.NEWLINE || zsciiChar == ZsciiEncoding.NEWLINE_10) {
                wordWrap(buffer.toString()).forEach(petsciiThread::println);
                petsciiThread.flush();
                buffer = new StringBuffer(BUFFER_LENGTH);
            } else if (zsciiChar == '>') {
                buffer.append(">");
                List<String> lines = wordWrap(buffer.toString());
                for (int i=0; i<lines.size(); ++i) {
                    petsciiThread.print(lines.get(i));
                    if (i != lines.size()-1) petsciiThread.newline();
                }
                petsciiThread.flush();
                buffer = new StringBuffer(BUFFER_LENGTH);
            } else {
                char c = machine.getGameData().getZsciiEncoding().getUnicodeChar(zsciiChar);
                buffer.append(c);
            }
        }
    }

    @Override
    public void deletePrevious(short zchar) {
        this.petsciiThread.write(ZsciiEncoding.INSTDEL);
    }

    @Override
    public void close() {

    }

    @Override
    public void flush() {
        petsciiThread.flush();
    }

    @Override
    public void select(boolean flag) {
        isSelected = flag;
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

}
