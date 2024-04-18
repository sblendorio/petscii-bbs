package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.PetsciiColors;
import eu.sblendorio.bbs.core.PetsciiThread;
import eu.sblendorio.bbs.tenants.mixed.BasicIde;
import eu.sblendorio.bbs.tenants.mixed.PatreonData;

import java.util.Map;
import java.util.TreeMap;

public class BasicIdePetscii extends PetsciiThread {
    private Map<Long, String> program = new TreeMap<>();

    @Override
    public void doLoop() throws Exception {
        //PatreonData patreonData = PatreonData.authenticatePetscii(this);
        //if (patreonData == null)
        //    return;

        write(PetsciiColors.GREY3);
        cls();
        println("*** RETROCAMPUS BBS BASIC V1.0 ***");
        println("DERIVED FROM SWBASIC2 BY KONYISOFT");
        BasicIde.execute(this, program);
    }

}
