package eu.sblendorio.bbs.tenants;

import eu.sblendorio.bbs.core.PetsciiThread;
import static eu.sblendorio.bbs.core.Keys.UPPERCASE;

public class PetsciiArtGallery extends PetsciiThread {

    @Override
    public void doLoop() throws Exception {
        cls();
        write(UPPERCASE);
        writeRawFile("walkingdead"); resetInput(); readKey();
        writeRawFile("nightdriver"); resetInput(); readKey();
        writeRawFile("magicportal"); resetInput(); readKey();
        writeRawFile("babycrying");  resetInput(); readKey();
    }
}
