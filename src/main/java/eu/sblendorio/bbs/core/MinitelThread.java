package eu.sblendorio.bbs.core;

import java.io.IOException;
import java.net.Socket;

public abstract class MinitelThread extends BbsThread {
    @Override
    public BbsInputOutput buildIO(Socket socket) throws IOException {
        return new MinitelInputOutput(socket);
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
