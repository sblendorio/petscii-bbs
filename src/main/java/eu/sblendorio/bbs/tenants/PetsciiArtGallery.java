package eu.sblendorio.bbs.tenants;

import eu.sblendorio.bbs.core.PetsciiThread;

import static eu.sblendorio.bbs.core.Keys.LOWERCASE;
import static eu.sblendorio.bbs.core.Keys.UPPERCASE;
import static eu.sblendorio.bbs.core.Keys.CASE_LOCK;
import static eu.sblendorio.bbs.core.Keys.CLR;

public class PetsciiArtGallery extends PetsciiThread {

    public static String[] pictures = {
            "walkingdead",
            "nightdriver",
            "magicportal",
            "baby-crying"
    };

    @Override
    public void doLoop() throws Exception {
        write(CLR, UPPERCASE, CASE_LOCK);
        for (String picture: pictures) {
            cls();
            writeRawFile(picture);
            resetInput();
            readKey();
        }
        write(CLR, LOWERCASE);
    }
}
