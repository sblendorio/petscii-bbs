package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.tenants.mixed.BasicIde;
import eu.sblendorio.bbs.tenants.mixed.PatreonData;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.Map;
import java.util.TreeMap;

public class BasicIdeAscii extends AsciiThread {
    private Map<Long, String> program = new TreeMap<>();
    private TriConsumer<BbsThread, Integer, Integer> locate = null;

    public BasicIdeAscii(TriConsumer<BbsThread, Integer, Integer> locate) {
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
