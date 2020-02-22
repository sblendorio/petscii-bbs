package org.zmpp.textbased.bbs;

import java.awt.Color;
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

    String adventureName = "";
    int score = 0;
    int steps = 0;
    int hours = 0;
    int minutes = 0;

    int inputCharCount = 0;

    
    private boolean isSelected = false;

    public BBSScreenModel(PetsciiThread petsciiThread, Machine machine) {
        this.petsciiThread = petsciiThread;
        this.machine = machine;
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
        petsciiThread.write(Colors.GREY3);
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
        petsciiThread.write(Keys.HOME);
        printStatusBar();
        petsciiThread.write(Keys.HOME);
        for(int i=0; i< 24;i++){
            petsciiThread.write(Keys.DOWN);
        }
        petsciiThread.print(">");
        for(int i=0; i<this.inputCharCount;i++){
            petsciiThread.write(Keys.RIGHT);
        }
        petsciiThread.flush();
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
        if (zsciiChar == ZsciiEncoding.NEWLINE || zsciiChar == ZsciiEncoding.NEWLINE_10) {
            petsciiThread.newline();
            petsciiThread.flush();
            this.inputCharCount=0;
        } else if (zsciiChar != ZsciiEncoding.INSTDEL && zsciiChar != -1) {
            char c = machine.getGameData().getZsciiEncoding().getUnicodeChar(zsciiChar);
            petsciiThread.print("" + c);
            petsciiThread.flush();
            this.inputCharCount++;
        }else{
            this.inputCharCount--;
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
