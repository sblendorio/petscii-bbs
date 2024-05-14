package eu.sblendorio.bbs.tenants.minitel;

import eu.sblendorio.bbs.core.MinitelThread;

public class TestMinitel4 extends MinitelThread {
    @Override
    public void doLoop() throws Exception {
        cls();
        print("Riga numero 1");
        write(13,10);
        print("Riga numero 2");
    }
}
