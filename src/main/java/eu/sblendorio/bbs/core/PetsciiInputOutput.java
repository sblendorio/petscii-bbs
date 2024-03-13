package eu.sblendorio.bbs.core;

import java.io.IOException;
import java.net.Socket;

public class PetsciiInputOutput extends BbsInputOutput {
    public PetsciiInputOutput(Socket socket) throws IOException{
        super(socket);
    }

    public static byte[] NEW_LINE_BYTES = new byte[] { 13 };

    @Override
    public byte[] newlineBytes() {
        return NEW_LINE_BYTES;
    }

    @Override
    public int readKey() throws IOException {
        final int result = super.readKey();
        final int res =  (result >= 193 && result <= 218) ? result - 96 : result;
        return res;
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
    public byte[] backspace() {
        return new byte[] { PetsciiKeys.DEL };
    }

    @Override
    public int backspaceKey() {
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
    public void writeBackspace() {
        write(PetsciiKeys.LEFT, PetsciiKeys.SPACE_CHAR, PetsciiKeys.LEFT);
    }

    @Override
    public int convertToAscii(int ch) {
        if (ch >= 'a' && ch <= 'z')
            return Character.toUpperCase(ch);
        else if (ch >= 'A' && ch <= 'Z')
            return Character.toLowerCase(ch);
        else if (ch == 164)
            return '_';
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
