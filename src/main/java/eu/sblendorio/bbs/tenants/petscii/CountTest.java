package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.bbstype.PetsciiThread;

public class CountTest extends PetsciiThread {
    @Override
    public void doLoop() throws Exception {
        String s = "";
        for (int i=1; i<=276; ++i) s += " "+ i;
        print(s);
        flush();
        resetInput(); readKey();
    }
}
