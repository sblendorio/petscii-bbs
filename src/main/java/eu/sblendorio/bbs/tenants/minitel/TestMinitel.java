package eu.sblendorio.bbs.tenants.minitel;

import eu.sblendorio.bbs.core.AsciiThread;

public class TestMinitel extends AsciiThread {
    @Override
    public void doLoop() throws Exception {
        write(12);
        println("Prova");
        write(12);
        println("Ciao");
    }
}
