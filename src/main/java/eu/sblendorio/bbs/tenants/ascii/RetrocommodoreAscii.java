package eu.sblendorio.bbs.tenants.ascii;

import java.nio.charset.StandardCharsets;

public class RetrocommodoreAscii extends GoogleBloggerProxyAscii {

    public RetrocommodoreAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.blogUrl = "https://faberpixel.blogspot.com";
        this.pageSize = 10;
    }

    private static final byte[] LOGO_BYTES = "Il disinformatico".getBytes(StandardCharsets.ISO_8859_1);
}
