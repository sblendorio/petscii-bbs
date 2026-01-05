package eu.sblendorio.bbs.tenants.minitel;

import eu.sblendorio.bbs.core.MinitelThread;

public class TestMinitel5 extends MinitelThread {
    @Override
    public void doLoop() throws Exception {
        cls();
        println("Riga numero 1");
        int x;
        while ((x=keyPressed(1000)) == -1);
        println("Premuto tasto x=" + x);
    }
}
