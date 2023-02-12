package eu.sblendorio.bbs.tenants.minitel;

import eu.sblendorio.bbs.core.AsciiThread;

public class TestMinitel extends AsciiThread {
    @Override
    public void doLoop() throws Exception {
        cls();
        println("Prova: à");
        println("Ciao");
    }
}
