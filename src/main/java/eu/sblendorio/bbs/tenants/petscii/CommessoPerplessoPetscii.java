package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.Hidden;

import static eu.sblendorio.bbs.core.PetsciiColors.GREY2;

@Hidden
public class CommessoPerplessoPetscii extends WordpressProxy {

    public CommessoPerplessoPetscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.commessoperplesso.it";
        this.pageSize = 15;
        this.screenLines = 19;
        this.showAuthor = false;
    }

    private static final byte[] LOGO_BYTES =  readBinaryFile("petscii/commesso.seq");

}
