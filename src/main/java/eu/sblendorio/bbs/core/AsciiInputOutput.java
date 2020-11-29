package eu.sblendorio.bbs.core;

import java.io.IOException;
import java.net.Socket;

public class AsciiInputOutput extends BbsInputOutput {
    public AsciiInputOutput(Socket socket) throws IOException{
        super(socket);
    }

    public static byte[] NEW_LINE_BYTES = new byte[] { 13, 10 };

    @Override
    public void cls() {
        for (int i=0; i<25; ++i) {
            newline();
        }
    }

    @Override
    public byte[] newlineBytes() {
        return NEW_LINE_BYTES;
    }

    @Override
    public int backspace() {
        return AsciiKeys.BACKSPACE;
    }

    @Override
    public boolean isBackspace(int ch) {
        return ch == AsciiKeys.BACKSPACE;
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
    public int getScreenColumns() {
        return 40;
    }

    @Override
    public int getScreenRows() {
        return 24;
    }


}
