package eu.sblendorio.bbs.tenants.minitel;

import eu.sblendorio.bbs.core.MinitelThread;

public class TestMinitel extends MinitelThread {
    @Override
    public void doLoop() throws Exception {
        cls();
        println("Prova: à");
        println("Ciao");
    }
}
