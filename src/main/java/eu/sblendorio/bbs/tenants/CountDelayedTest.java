package eu.sblendorio.bbs.tenants;

import eu.sblendorio.bbs.core.PetsciiThread;

public class CountDelayedTest extends PetsciiThread {
    @Override
    public void doLoop() throws Exception {
        String s = "";
        for (int i=1; i<=276; ++i) s += " "+ i;
        s.chars().forEach(ch -> {
            write(ch);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {}
        });
        flush();
        resetInput(); readKey();
    }
}
