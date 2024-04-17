package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.PetsciiThread;
import eu.sblendorio.bbs.games.BasicIde;

import java.util.Map;
import java.util.TreeMap;

public class BasicIdePetscii extends PetsciiThread {
    private Map<Long, String> program = new TreeMap<>();

    @Override
    public void doLoop() throws Exception {
        cls();
        println("*** RETROCAMPUS BBS BASIC 1.0 ***");
        BasicIde.execute(this, program);
    }

}
