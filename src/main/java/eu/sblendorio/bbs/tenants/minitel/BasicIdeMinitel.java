package eu.sblendorio.bbs.tenants.minitel;

import eu.sblendorio.bbs.core.MinitelThread;
import eu.sblendorio.bbs.games.BasicIde;

import java.util.Map;
import java.util.TreeMap;

public class BasicIdeMinitel extends MinitelThread {
    private Map<Long, String> program = new TreeMap<>();

    @Override
    public void doLoop() throws Exception {
        cls();
        println("*** RETROCAMPUS BBS BASIC 1.0 ***");
        BasicIde.execute(this, program);
    }

}
