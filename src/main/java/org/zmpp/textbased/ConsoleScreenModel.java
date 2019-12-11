package org.zmpp.textbased;

import org.zmpp.io.OutputStream;
import org.zmpp.vm.ScreenModel;
import org.zmpp.vm.TextCursor;

public class ConsoleScreenModel implements ScreenModel {
    @Override
    public void reset() {

    }

    @Override
    public void splitWindow(int linesUpperWindow) {

    }

    @Override
    public void setWindow(int window) {

    }

    @Override
    public void setTextStyle(int style) {

    }

    @Override
    public void setBufferMode(boolean flag) {

    }

    @Override
    public void eraseLine(int value) {

    }

    @Override
    public void eraseWindow(int window) {

    }

    @Override
    public void setTextCursor(int line, int column, int window) {

    }

    @Override
    public TextCursor getTextCursor() {
        return null;
    }

    @Override
    public void setPaging(boolean flag) {

    }

    @Override
    public int setFont(int fontnumber) {
        return 0;
    }

    @Override
    public void setBackgroundColor(int colornumber, int window) {

    }

    @Override
    public void setForegroundColor(int colornumber, int window) {

    }

    @Override
    public void redraw() {

    }

    @Override
    public void displayCursor(boolean flag) {

    }

    @Override
    public OutputStream getOutputStream() {
        return null;
    }

    @Override
    public void waitInitialized() {

    }

    @Override
    public void resetPagers() {

    }
}
