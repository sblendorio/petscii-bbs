package eu.sblendorio.bbs.core;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static eu.sblendorio.bbs.core.PrestelControls.*;
import static org.apache.commons.lang3.StringUtils.length;

public class PrestelInputOutput extends BbsInputOutput {

    protected boolean autoconceal;

    public PrestelInputOutput(Socket socket, boolean autoconceal) throws IOException {
        super(socket);
        this.autoconceal = autoconceal;
    }

    public PrestelInputOutput(Socket socket) throws IOException {
        this(socket, false);
    }

    @Override
    public byte[] newlineBytes() {
        return new byte[] { 13, 10 };
    }

    @Override
    public byte[] backspace() {
        return new byte[] {CURSOR_LEFT, ' ', CURSOR_LEFT};
    }

    @Override
    public int backspaceKey() {
        return BACKSPACE_KEY;
    }

    @Override
    public int returnAlias() {
        return 95;
    }

    @Override
    public boolean isNewline(int ch) {
        return ch == AsciiKeys.CR || ch == AsciiKeys.LF;
    }

    @Override
    public boolean isBackspace(int ch) {
        return ch == AsciiKeys.BACKSPACE || ch == AsciiKeys.DELETE;
    }

    @Override
    public void writeDoublequotes() {
        write(34);
    }

    @Override
    public int convertToAscii(int ch) {
        return ch;
    }

    @Override
    public boolean quoteMode() {
        return false;
    }

    @Override
    public void writeBackspace() {
        write(CURSOR_LEFT, ' ', CURSOR_LEFT);
    }

    @Override
    public void afterReadLineChar() {
        if (!autoconceal) return;
        write(CONCEAL);
        write(CURSOR_LEFT);
    }

    @Override
    public void checkBelowLine() {
        if (!autoconceal) return;
        newline();
        write(CURSOR_UP);
    }


    @Override
    public void print(String msg) {
        if (msg == null) return;
        for (char c: msg.toCharArray()) {
            try {
                if (isDiacritic(c)) {
                    printDiacritic(c);
                } else if (Character.isLetter(c)) {
                    out.write(Character.toString(c).getBytes(StandardCharsets.UTF_8));
                } else {
                    out.write(c);
                }
            } catch (IOException e) {}
        }
    }

    //@Override
    public void println(String msg) {
        print(msg);
        if (!autoconceal) {
            newline();
            return;
        }
        if (length(msg) < 40) {
            write(CONCEAL);
            write(CURSOR_LEFT);
            if (length(msg) < 39)
                newline();
        }
    }

    public void newline() {
        write(newlineBytes());
        if (!autoconceal) return;
        write(CONCEAL);
        write(CURSOR_LEFT);
    }

    private void printDiacritic(char ch) {
        write(ch);
    }

    private boolean isDiacritic(char ch) {
        return "àáâäèéêëìíîïòóôöùúûüçÀÁÂÄÈÉÊËÌÍÎÏÒÓÔÖÙÚÛÜÇ".indexOf(ch) != -1;
    }
}
