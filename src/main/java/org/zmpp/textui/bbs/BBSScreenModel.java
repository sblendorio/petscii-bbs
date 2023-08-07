package org.zmpp.textui.bbs;

import eu.sblendorio.bbs.core.BbsThread;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.davidmoten.text.utils.WordWrap;
import org.zmpp.encoding.ZsciiEncoding;
import org.zmpp.io.OutputStream;
import org.zmpp.vm.Machine;
import org.zmpp.vm.ScreenModel;
import org.zmpp.vm.StatusLine;
import org.zmpp.vm.TextCursor;

import eu.sblendorio.bbs.core.PetsciiKeys;

public class BBSScreenModel implements ScreenModel, OutputStream, StatusLine {

    private BbsThread bbsThread;
    private Machine machine;
    private StringBuffer buffer;

    String adventureName = "";
    int score = 0;
    int steps = 0;
    int hours = 0;
    int minutes = 0;

    static int BUFFER_LENGTH = 8192;

    private int nlines = 0;

    private boolean isSelected = false;

    public BBSScreenModel(BbsThread bbsThread, Machine machine) {
        this.bbsThread = bbsThread;
        this.machine = machine;
        buffer = new StringBuffer(BUFFER_LENGTH);
    }

    @Override
    public void reset() {
        bbsThread.log("reset not yet implemented");
    }

    @Override
    public void splitWindow(int linesUpperWindow) {
        bbsThread.log("splitWindow not yet implemented");
    }

    @Override
    public void setWindow(int window) {
        bbsThread.log("setWindow not yet implemented");
    }

    @Override
    public void setTextStyle(int style) {
        bbsThread.log("setTextStyle not yet implemented");
    }

    @Override
    public void setBufferMode(boolean flag) {
        bbsThread.log("setBufferMode not yet implemented");
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
        bbsThread.write(PetsciiKeys.HOME);
        bbsThread.flush();
    }

    @Override
    public void eraseWindow(int window) {
        bbsThread.log("eraseWindow not yet implemented");
    }

    @Override
    public void setTextCursor(int line, int column, int window) {
        bbsThread.log("setTextCursor not yet implemented");
    }

    @Override
    public TextCursor getTextCursor() {
        bbsThread.log("getTextCursor not yet implemented");
        return null;
    }

    @Override
    public void setPaging(boolean flag) {
        bbsThread.log("setPaging not yet implemented");
    }

    @Override
    public int setFont(int fontnumber) {
        bbsThread.log("setFont not yet implemented");
        return 0;
    }

    @Override
    public void setBackgroundColor(int colornumber, int window) {
        bbsThread.log("setBackgroundColor not yet implemented");
    }

    @Override
    public void setForegroundColor(int colornumber, int window) {
        bbsThread.log("setForegroundColor not yet implemented");
    }

    @Override
    public void redraw() {
    }

    @Override
    public void displayCursor(boolean flag) {
    }

    @Override
    public OutputStream getOutputStream() {
        return this;
    }

    @Override
    public void waitInitialized() {
    }

    @Override
    public void resetPagers() {
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

    @Override
    public void print(short zsciiChar, boolean isInput) {
        if (isInput) {
            if (zsciiChar == ZsciiEncoding.NEWLINE || zsciiChar == ZsciiEncoding.NEWLINE_10) {
                if (bbsThread.getLocalEcho()) bbsThread.newline();
                bbsThread.flush();
            } else if (zsciiChar != ZsciiEncoding.INSTDEL && zsciiChar != ZsciiEncoding.DELETE && zsciiChar != -1) {
                char c = machine.getGameData().getZsciiEncoding().getUnicodeChar(zsciiChar);
                if (bbsThread.getLocalEcho())  {
                    bbsThread.print("" + c);
                    bbsThread.afterReadLineChar();
                }
                bbsThread.flush();
            }
        } else {
            if (zsciiChar == ZsciiEncoding.NEWLINE || zsciiChar == ZsciiEncoding.NEWLINE_10) {
                wordWrap(buffer.toString()).forEach(s -> {
                    nlines++;
                    bbsThread.println(s);
                    bbsThread.checkBelowLine();
                    checkForScreenPaging();
                });
                bbsThread.flush();
                buffer = new StringBuffer(BUFFER_LENGTH);
            } else if (zsciiChar == '>') {
                buffer.append(">");
                List<String> lines = wordWrap(buffer.toString());
                for (int i=0; i<lines.size(); ++i) {
                    nlines++;
                    if (i != lines.size()-1) {
                        bbsThread.println(lines.get(i));
                        checkForScreenPaging();
                    } else {
                        bbsThread.print(lines.get(i));
                        bbsThread.afterReadLineChar();
                    }
                }
                bbsThread.flush();
                buffer = new StringBuffer(BUFFER_LENGTH);
                nlines = 0;
            } else {
                char c = machine.getGameData().getZsciiEncoding().getUnicodeChar(zsciiChar);
                buffer.append(c);
            }
        }
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

    @Override
    public void deletePrevious(short zchar) {
        this.bbsThread.write(bbsThread.backspace());
    }

    @Override
    public void close() {
    }

    @Override
    public void flush() {
        bbsThread.flush();
    }

    @Override
    public void select(boolean flag) {
        isSelected = flag;
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public short backspace() {
        return (short) (bbsThread.backspaceKey());
    }

    @Override
    public boolean getLocalEcho() {
        return bbsThread.getLocalEcho();
    }
}
