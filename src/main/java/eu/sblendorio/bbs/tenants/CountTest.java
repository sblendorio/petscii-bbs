package eu.sblendorio.bbs.tenants;

import eu.sblendorio.bbs.core.PetsciiThread;

public class CountTest extends PetsciiThread {
    @Override
    public void doLoop() throws Exception {
        String s = "";
        for (int i=1; i<=276; ++i) s += " "+String.valueOf(i);
        print(s);
        flush();
        resetInput(); int ch = readKey();
    }
}
