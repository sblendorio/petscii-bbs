package eu.sblendorio.bbs.tenants.minitel;

import eu.sblendorio.bbs.core.MinitelThread;
import eu.sblendorio.bbs.tenants.mixed.BasicIde;
import eu.sblendorio.bbs.tenants.mixed.PatreonData;

import java.util.Map;
import java.util.TreeMap;

public class BasicIdeMinitel extends MinitelThread {
    private Map<Long, String> program = new TreeMap<>();

    @Override
    public void doLoop() throws Exception {
        PatreonData patreonData = PatreonData.authenticateAscii(this);
        if (patreonData == null)
            return;

        cls();
        println("*** RETROCAMPUS BBS BASIC 1.0 ***");
        BasicIde.execute(this, program);
    }

}
