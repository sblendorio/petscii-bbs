package eu.sblendorio.bbs.core;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static eu.sblendorio.bbs.core.HtmlUtils.utilHtmlClean;
import static eu.sblendorio.bbs.core.HtmlUtils.utilHtmlDiacriticsToAscii;
import static java.nio.charset.StandardCharsets.ISO_8859_1;

public abstract class AsciiThread extends BbsThread {

    public int screenColumns = 40;
    public int screenRows = 24;
    public byte[] clsBytes = new byte[] {
        13, 10,
        13, 10,
        13, 10,
        13, 10
    };

    public AsciiThread() {
        keepAliveChar = 0;
    }

    @Override
    public String getTerminalType() {
        return "ascii";
    }

    @Override
    public BbsInputOutput buildIO(Socket socket) throws IOException {
        return new AsciiInputOutput(socket);
    }

    @Override
    public int getScreenColumns() {
        return screenColumns;
    }

    @Override
    public int getScreenRows() {
        return screenRows;
    }

    @Override
    public void cls() {
        write(clsBytes);
    }

    @Override
    public String preprocessDiacritics(String s) {
        return utilHtmlDiacriticsToAscii(s);
    }

}
