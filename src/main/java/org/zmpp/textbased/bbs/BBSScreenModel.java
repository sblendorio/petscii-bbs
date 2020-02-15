package org.zmpp.textbased.bbs;


import org.zmpp.vm.StatusLine;
import org.zmpp.encoding.ZsciiEncoding;
import org.zmpp.io.OutputStream;
import org.zmpp.vm.Machine;
import org.zmpp.vm.ScreenModel;
import org.zmpp.vm.TextCursor;

import eu.sblendorio.bbs.core.Colors;
import eu.sblendorio.bbs.core.Keys;

import eu.sblendorio.bbs.core.PetsciiThread;

public class BBSScreenModel implements ScreenModel, OutputStream, StatusLine {

    PetsciiThread petsciiThread;
    Machine machine;
    BBSInputStream cliInputStream;

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
        petsciiThread.write(Colors.LIGHT_BLUE);
    }

    @Override
    public void reset() {
        throw new java.lang.UnsupportedOperationException("reset not yet implemented");

    }

    @Override
    public void splitWindow(int linesUpperWindow) {
        throw new java.lang.UnsupportedOperationException("splitWindow not yet implemented");

    }

    @Override
    public void setWindow(int window) {
        throw new java.lang.UnsupportedOperationException("setWindow not yet implemented");

    }

    @Override
    public void setTextStyle(int style) {
        throw new java.lang.UnsupportedOperationException("setTextStyle not yet implemented");

    }

    @Override
    public void setBufferMode(boolean flag) {
        throw new java.lang.UnsupportedOperationException("setBufferMode not yet implemented");

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
        throw new java.lang.UnsupportedOperationException("eraseWindow not yet implemented");

    }

    @Override
    public void setTextCursor(int line, int column, int window) {
        throw new java.lang.UnsupportedOperationException("setTextCursor not yet implemented");

    }

    @Override
    public TextCursor getTextCursor() {
        throw new java.lang.UnsupportedOperationException("getTextCursor not yet implemented");

    }

    @Override
    public void setPaging(boolean flag) {
        throw new java.lang.UnsupportedOperationException("setPaging not yet implemented");

    }

    @Override
    public int setFont(int fontnumber) {
        throw new java.lang.UnsupportedOperationException("setFont not yet implemented");

    }

    @Override
    public void setBackgroundColor(int colornumber, int window) {
        throw new java.lang.UnsupportedOperationException("setBackgroundColor not yet implemented");

    }

    @Override
    public void setForegroundColor(int colornumber, int window) {
        throw new java.lang.UnsupportedOperationException("setForegroundColor not yet implemented");

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
        System.out.println("redraw");
    }

    @Override
    public void displayCursor(boolean flag) {
        //throw new java.lang.UnsupportedOperationException("displayCursor not yet implemented");

    }

    @Override
    public OutputStream getOutputStream() {
        return this;
    }

    @Override
    public void waitInitialized() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resetPagers() {
        // TODO Auto-generated method stub

    }

    @Override
    public void print(short zsciiChar, boolean isInput) {
        if (zsciiChar == ZsciiEncoding.NEWLINE) {
            this.inputCharCount = 0;
            this.petsciiThread.newline();
          } else if (zsciiChar == 20) {
            this.inputCharCount--;
            this.petsciiThread.write(20); // TODO FIXME SBLEND
            this.petsciiThread.flush();
          } else {
            this.inputCharCount++;
            char c = machine.getGameData().getZsciiEncoding().getUnicodeChar(zsciiChar);
            System.out.print(""+c);
            this.petsciiThread.print(""+c);
          }

    }

    @Override
    public void deletePrevious(short zchar) {
        petsciiThread.print(""+machine.getGameData().getZsciiEncoding().getUnicodeChar(zchar));

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
