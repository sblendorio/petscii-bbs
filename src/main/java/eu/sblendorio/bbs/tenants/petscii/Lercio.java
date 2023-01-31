package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.Hidden;

@Hidden
public class Lercio extends WordpressProxy {

    public Lercio() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.lercio.it";
        this.pageSize = 6;
        this.screenLines = 19;
        this.showAuthor = true;
    }

    private static final byte[] LOGO_BYTES = readBinaryFile("petscii/lercio.seq");

}
