package eu.sblendorio.bbs.core.bbstype;

import eu.sblendorio.bbs.core.BbsInputOutput;
import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.PetsciiInputOutput;
import static eu.sblendorio.bbs.core.PetsciiKeys.DOWN;
import static eu.sblendorio.bbs.core.PetsciiKeys.HOME;
import static eu.sblendorio.bbs.core.PetsciiKeys.RIGHT;
import java.io.IOException;
import java.net.Socket;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.substring;

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

}
