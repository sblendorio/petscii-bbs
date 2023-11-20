package eu.sblendorio.bbs.core;

import java.io.IOException;
import java.net.Socket;

import static eu.sblendorio.bbs.core.PrestelControls.*;

@Hidden
public abstract class PrestelThread extends BbsThread {

    protected boolean autoConceal = false;

    public PrestelThread() {
        keepAliveChar = 17;
        setLocalEcho(true);
    }

    @Override
    public BbsInputOutput buildIO(Socket socket) throws IOException {
        return new PrestelInputOutput(socket, autoConceal);
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

    public void gotoXY(int x, int y) {
        write(HOME);
        for (int i=0; i<y; ++i) write(CURSOR_DOWN);
        for (int i=0; i<x; ++i) write(CURSOR_RIGHT);
    }

}
