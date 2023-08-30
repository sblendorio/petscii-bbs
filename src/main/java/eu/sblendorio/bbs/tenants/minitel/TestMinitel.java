package eu.sblendorio.bbs.tenants.minitel;

import eu.sblendorio.bbs.core.MinitelThread;

public class TestMinitel extends MinitelThread {
    @Override
    public void doLoop() throws Exception {
        cls();
        println("Prova però, perché, com'è, ü, garçon");
        println("àáâäèéêëìíîïòóôöùúûüç");
        println("ÀÁÂÄÈÉÊËÌÍÎÏÒÓÔÖÙÚÛÜÇ");
        // write(readBinaryFile("minitel/undefined.vdt"));
    }
}
