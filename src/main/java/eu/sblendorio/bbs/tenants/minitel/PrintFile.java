package eu.sblendorio.bbs.tenants.minitel;

import eu.sblendorio.bbs.core.MinitelThread;

public class PrintFile extends MinitelThread {
    @Override
    public void doLoop() throws Exception {
        write(readBinaryFile("minitel/wikipedia-title.vdt"));
    }
}
