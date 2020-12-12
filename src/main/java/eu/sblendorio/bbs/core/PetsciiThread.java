package eu.sblendorio.bbs.core;

import static eu.sblendorio.bbs.core.PetsciiKeys.DOWN;
import static eu.sblendorio.bbs.core.PetsciiKeys.HOME;
import static eu.sblendorio.bbs.core.PetsciiKeys.RIGHT;
import java.io.IOException;
import java.net.Socket;

public abstract class PetsciiThread extends BbsThread {

    @Override
    public BbsInputOutput buildIO(Socket socket) throws IOException {
        return new PetsciiInputOutput(socket);
    }

    public void gotoXY(int x, int y) {
        write(HOME);
        for (int i=0; i<y; ++i) write(DOWN);
        for (int i=0; i<x; ++i) write(RIGHT);
    }


    @Override
    public int getScreenColumns() {
        return 40;
    }

    @Override
    public int getScreenRows() {
        return 25;
    }


    @Override
    public void cls() {
        write(147);
    }


}
