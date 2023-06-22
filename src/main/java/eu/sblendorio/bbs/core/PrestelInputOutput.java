package eu.sblendorio.bbs.core;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class PrestelInputOutput extends BbsInputOutput {

    public PrestelInputOutput(Socket socket) throws IOException {
        super(socket);
    }

    @Override
    public byte[] newlineBytes() {
        return new byte[] { 13, 10 };
    }

    @Override
    public int backspace() {
        return AsciiKeys.BACKSPACE;
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
        write(AsciiKeys.BACKSPACE, ' ', AsciiKeys.BACKSPACE);
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

    private void printDiacritic(char ch) {
        write(ch);
    }

    private boolean isDiacritic(char ch) {
        return "àáâäèéêëìíîïòóôöùúûüçÀÁÂÄÈÉÊËÌÍÎÏÒÓÔÖÙÚÛÜÇ".indexOf(ch) != -1;
    }
}
