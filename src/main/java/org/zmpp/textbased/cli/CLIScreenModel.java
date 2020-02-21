package org.zmpp.textbased.cli;

import java.io.Console;

import org.zmpp.vm.StatusLine;
import org.zmpp.encoding.ZsciiEncoding;
import org.zmpp.io.OutputStream;
import org.zmpp.vm.Machine;
import org.zmpp.vm.ScreenModel;
import org.zmpp.vm.TextCursor;

public class CLIScreenModel implements ScreenModel, OutputStream, StatusLine {

    Console console;
    Machine machine;
    CLIInputStream cliInputStream;
    StringBuffer buffer;

    String adventureName = "";
    int score = 0;
    int steps = 0;
    int hours = 0;
    int minutes = 0;

    static int BUFFER_LENGTH = 880; // = 40 columns x 22 rows (+1 for title + 2 for input)

    private boolean isSelected = false;

    public CLIScreenModel(Console console, Machine machine) {
        this.console = console;
        this.machine = machine;
        buffer = new StringBuffer(BUFFER_LENGTH);
    }

    private void printOSAndBuffer(String s) {
        buffer.append(s);
        console.printf(s);
    }

    private void trimBuffer() {

        String[] rows = buffer.toString().split("\n");
        String newContent = rows[rows.length - 1];
        int maxRows = 21;

        int index = rows.length - 2;
        String row = "";

        while (maxRows > 0 && index > 0) {
            row = rows[index];
            int rowCount = row.length() / 40;
            if (rowCount == 0) {
                rowCount = 1;
            }
            if (rowCount <= maxRows) {
                maxRows = maxRows - rowCount;
                newContent = row + "\n" + newContent;
            } else {
                newContent = row.substring(maxRows * 40) + "\n" + newContent;
                maxRows = 0;
            }
            index--;

        }
        buffer.delete(0, buffer.length());
        buffer.append(newContent);

    }

    private void printStatusBar() {
        String rightStatus = score + "/" + steps;
        int spaces = 40 - adventureName.length() - 1 - rightStatus.length();
        StringBuffer spacesString = new StringBuffer();
        for (int i = 0; i < spaces; i++) {
            spacesString.append(" ");
        }
        console.printf("\033[47;30m");
        console.printf("%s:%s%s\n", adventureName, spacesString.toString(), rightStatus);
        console.printf("\033[40;37m");
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
        printOSAndBuffer("\r");
        console.flush();
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
        console.printf("\033[H\033[2J"); // clear screen
        console.flush();                  // flush
        trimBuffer();
        printStatusBar();
        console.printf(buffer.toString()); // redraw
        console.flush();
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
            printOSAndBuffer("\n");
        } else {
            printOSAndBuffer("" + machine.getGameData().getZsciiEncoding().getUnicodeChar(zsciiChar));
        }
    }

    @Override
    public void deletePrevious(short zchar) {
        console.printf("" + machine.getGameData().getZsciiEncoding().getUnicodeChar(zchar));
    }

    @Override
    public void close() {
    }

    @Override
    public void flush() {
        console.flush();
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
