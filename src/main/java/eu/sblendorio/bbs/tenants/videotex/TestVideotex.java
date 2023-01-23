package eu.sblendorio.bbs.tenants.videotex;

import eu.sblendorio.bbs.core.AsciiThread;

public class TestVideotex extends AsciiThread {
    @Override
    public void doLoop() throws Exception {
        write(12);
        println("Prova");
        write(12);
        println("Ciao");
    }
}
