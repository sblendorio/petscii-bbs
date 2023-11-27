package eu.sblendorio.bbs.tenants.minitel;

import eu.sblendorio.bbs.core.MinitelControls;
import eu.sblendorio.bbs.core.MinitelThread;

public class TestMinitel2 extends MinitelThread {
    @Override
    public void doLoop() throws Exception {
        cls();
        println("Prova però, perché, com'è, ü, garçon");
        println("àáâäèéêëìíîïòóôöùúûüç");
        println("ÀÁÂÄÈÉÊËÌÍÎÏÒÓÔÖÙÚÛÜÇ");
        for (int i=32; i<=127; i++) write(i);
        write(MinitelControls.CURSOR_ON);
    }
}
