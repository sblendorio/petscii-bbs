package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.Hidden;

@Hidden
public class AlessandroAlbano extends WordpressProxy {

    public AlessandroAlbano() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.alessandroalbano.it";
        this.pageSize = 6;
        this.screenLines = 19;
        this.showAuthor = true;
    }

    private static final byte[] LOGO_BYTES = readBinaryFile("petscii/alessandroalbano.seq");

}
