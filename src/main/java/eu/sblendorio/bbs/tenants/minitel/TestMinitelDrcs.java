package eu.sblendorio.bbs.tenants.minitel;

import eu.sblendorio.bbs.core.MinitelThread;

public class TestMinitelDrcs extends MinitelThread {
    @Override
    public void doLoop() throws Exception {
        cls();
        write(readBinaryFile("minitel/undefined.vdt"));
    }
}
