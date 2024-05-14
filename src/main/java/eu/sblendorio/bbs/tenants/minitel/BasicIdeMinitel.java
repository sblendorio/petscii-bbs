package eu.sblendorio.bbs.tenants.minitel;

import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.MinitelThread;
import eu.sblendorio.bbs.tenants.mixed.BasicIde;
import eu.sblendorio.bbs.tenants.mixed.PatreonData;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.Map;
import java.util.TreeMap;

public class BasicIdeMinitel extends MinitelThread {
    private Map<Long, String> program = new TreeMap<>();
    private TriConsumer<BbsThread, Integer, Integer> locate = null;

    public BasicIdeMinitel(TriConsumer<BbsThread, Integer, Integer> locate) {
        this.locate = locate;
    }

    @Override
    public void doLoop() throws Exception {
        PatreonData patreonData = PatreonData.authenticateAscii(this);
        if (patreonData == null) return;

        cls();
        println("*** RETROCAMPUS BBS BASIC V1.0 ***");
        println("DERIVED FROM SWBASIC2 BY KONYISOFT");
        BasicIde.execute(this, program, locate);
    }

}
