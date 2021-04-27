package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.Hidden;

@Hidden
public class Vitno extends WordpressProxy {

    public Vitno() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://vintageisthenewold.com";
        this.pageSize = 7;
        this.screenLines = 18;
        this.showAuthor = true;
    }

    private static final byte[] LOGO_BYTES = readBinaryFile("petscii/vitno.seq");

}
