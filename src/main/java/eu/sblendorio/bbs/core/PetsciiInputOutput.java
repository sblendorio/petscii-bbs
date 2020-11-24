package eu.sblendorio.bbs.core;

import java.io.IOException;
import java.net.Socket;

public class PetsciiInputOutput extends BbsInputOutput {
    public PetsciiInputOutput(Socket socket) throws IOException{
        super(socket);
    }

    @Override
    public void cls() {
        out.write(147);
    }

    @Override
    public void newline() {
        out.write(13);
    }

    @Override
    public int readKey() throws IOException {
        final int result = super.readKey();
        return (result >= 193 && result <= 218) ? result - 96 : result;
    }

    @Override
    public void print(String msg) {
        if (msg == null) return;

        for (char c: msg.toCharArray()) {
            if (!isPrintableChar(c) && c != '\r' && c != '\n')
                continue;
            else if (c == '_')
                c = (char) 228;
            else if (c >= 'a' && c <= 'z')
                c = Character.toUpperCase(c);
            else if (c >= 'A' && c <= 'Z')
                c = Character.toLowerCase(c);

            out.write(c);
        }
    }

    @Override
    public int backspace() {
        return PetsciiKeys.DEL;
    }

    @Override
    public boolean isBackspace(int ch) {
        return ch == PetsciiKeys.DEL || ch == PetsciiKeys.INS;
    }

    @Override
    public boolean isNewline(int ch) {
        return ch == PetsciiKeys.RETURN || ch == 141;
    }

    @Override
    public void writeDoublequotes() {
        write(34, 34, PetsciiKeys.DEL);
    }

    @Override
    public int convertToAscii(int ch) {
        if (ch >= 'a' && ch <= 'z')
            return Character.toUpperCase(ch);
        else if (ch >= 'A' && ch <= 'Z')
            return Character.toLowerCase(ch);
        else
            return ch;
    }

    @Override
    public boolean isPrintableChar(int c) {
        return (c >= 32 && c <= 127) || (c >= 160 && c <= 255);
    }

    @Override
    public boolean quoteMode() {
        return out.quoteMode();
    }
}
