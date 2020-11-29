package eu.sblendorio.bbs.tenants.mixed;

import eu.sblendorio.bbs.core.PetsciiThread;

public class DemoPetscii extends PetsciiThread {
    @Override
    public void doLoop() throws Exception {
        newline();
        newline();
        println("Esempio di PetSCII Thread");
        newline();
        newline();
    }
}
