package eu.sblendorio.bbs.tenants.ascii;

import java.nio.charset.StandardCharsets;

public class IndieCampusAscii extends GoogleBloggerProxyAscii {

    public IndieCampusAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.blogUrl = "https://iononsoleggere.blogspot.com/";
        this.labels = "BBS";
        this.showTimestamp = false;
        this.pageSize = 10;
        this.screenLines = 18;
    }

    private static final byte[] LOGO_BYTES = "Indie Campus".getBytes(StandardCharsets.ISO_8859_1);
}
