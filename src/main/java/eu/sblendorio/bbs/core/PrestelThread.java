package eu.sblendorio.bbs.core;

import java.io.IOException;
import java.net.Socket;

import static eu.sblendorio.bbs.core.MinitelControls.*;
import static eu.sblendorio.bbs.core.Utils.bytes;

public abstract class PrestelThread extends BbsThread {

    private byte currentSize = TEXTSIZE_NORMAL;

    public PrestelThread() {
        keepAliveChar = 17;
        setLocalEcho(true);
    }

    @Override
    public BbsInputOutput buildIO(Socket socket) throws IOException {
        return new PrestelInputOutput(socket);
    }

    @Override
    public String getTerminalType() {
        return "prestel";
    }

    @Override
    public void cls() {
        write(12);
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
