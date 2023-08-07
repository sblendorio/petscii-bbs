package eu.sblendorio.bbs.core;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class AsciiInputOutput extends BbsInputOutput {
    public AsciiInputOutput(Socket socket) throws IOException{
        super(socket);
    }

    public static byte[] NEW_LINE_BYTES = new byte[] { 13, 10 };

    @Override
    public byte[] newlineBytes() {
        return NEW_LINE_BYTES;
    }

    @Override
    public byte[] backspace() {
        return new byte[] { AsciiKeys.BACKSPACE };
    }

    @Override
    public int backspaceKey() {
        return AsciiKeys.BACKSPACE;
    }

    @Override
    public boolean isBackspace(int ch) {
        return ch == AsciiKeys.BACKSPACE || ch == AsciiKeys.DELETE;
    }

    @Override
    public boolean isNewline(int ch) {
        return ch == AsciiKeys.CR || ch == AsciiKeys.LF;
    }

    @Override
    public void writeDoublequotes() {
        write(34);
    }

    @Override
    public void print(String msg) {
        if (msg == null) return;
        for (char c: msg.toCharArray()) {
            if (Character.isLetter(c)) {
                try {
                    out.write(Character.toString(c).getBytes(StandardCharsets.UTF_8));
                } catch (IOException e) {}
            } else {
                out.write(c);
            }
        }
    }

    @Override
    public int convertToAscii(int ch) {
        return ch;
    }

    @Override
    public boolean isPrintableChar(int c) {
        return c >= 32;
    }

    @Override
    public boolean quoteMode() {
        return false;
    }

    @Override
    public void writeBackspace() {
        write(AsciiKeys.BACKSPACE, ' ', AsciiKeys.BACKSPACE);
    }

}
