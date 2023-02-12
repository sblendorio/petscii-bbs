package eu.sblendorio.bbs.core;

import java.io.IOException;
import java.net.Socket;

public class MinitelInputOutput extends BbsInputOutput {

    public MinitelInputOutput(Socket socket) throws IOException {
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

}
