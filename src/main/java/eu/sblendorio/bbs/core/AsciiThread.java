package eu.sblendorio.bbs.core;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import static java.nio.charset.StandardCharsets.ISO_8859_1;

public abstract class AsciiThread extends BbsThread {

    protected int screenColumns = 40;
    protected int screenRows = 24;

    public AsciiThread() {
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

}
