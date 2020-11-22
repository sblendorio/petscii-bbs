package eu.sblendorio.bbs.core;

import java.io.*;
import java.net.Socket;

import static eu.sblendorio.bbs.core.Utils.isControlChar;
import static eu.sblendorio.bbs.core.Utils.isPrintableChar;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.substring;

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
        final int result = in.read();
        if (result == -1) throw new BbsIOException("BbsIOException::readKey()");
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
    public boolean quoteMode() {
        return out.quoteMode();
    }

    // CICCIO
    private String readCommandLine() throws IOException {
        int ch;
        String value = EMPTY;
        do {
            ch = readKey();
            if (isBackspace(ch)) {
                if (value.length() > 0) {
                    write(backspace());
                    value = value.substring(0, value.length()-1);
                }
            } else if (ch == 34) {
                write(34, 34, backspace());
                value += "\"";
            } else if (ch == PetsciiKeys.RETURN || ch == 141) {
                write(PetsciiKeys.RETURN);
            } else if (Utils.isPrintableChar(ch)) {
                write(ch);
                if (ch >= 'a' && ch <= 'z')
                    ch = Character.toUpperCase(ch);
                else if (ch >= 'A' && ch <= 'Z')
                    ch = Character.toLowerCase(ch);
                value += (char) ch;
            }
        } while (ch != PetsciiKeys.RETURN && ch != 141);
        final String result = value;
        return result;
    }

}
