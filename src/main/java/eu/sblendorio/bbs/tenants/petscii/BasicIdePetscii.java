package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.PetsciiColors;
import eu.sblendorio.bbs.core.PetsciiThread;
import eu.sblendorio.bbs.tenants.mixed.BasicIde;
import eu.sblendorio.bbs.tenants.mixed.PatreonData;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.Map;
import java.util.TreeMap;

public class BasicIdePetscii extends PetsciiThread {
    private Map<Long, String> program = new TreeMap<>();
    private TriConsumer<BbsThread, Integer, Integer> locate = null;

    public BasicIdePetscii(TriConsumer<BbsThread, Integer, Integer> locate) {
        this.locate = locate;
    }

    @Override
    public void doLoop() throws Exception {
        PatreonData patreonData = PatreonData.authenticatePetscii(this);
        if (patreonData == null) return;

        write(PetsciiColors.GREY3);
        cls();
        println("*** RETROCAMPUS BBS BASIC V1.0 ***");
        println("DERIVED FROM SWBASIC2 BY KONYISOFT");
        BasicIde.execute(this, program, locate);
    }

}
