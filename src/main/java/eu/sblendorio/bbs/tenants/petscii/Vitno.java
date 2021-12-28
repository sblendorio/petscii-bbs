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
        this.httpUserAgent =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/85.0.4183.102 Safari/537.36";
    }

    private static final byte[] LOGO_BYTES = readBinaryFile("petscii/vitno.seq");

}
