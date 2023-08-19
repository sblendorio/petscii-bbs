package eu.sblendorio.bbs.tenants.ascii;

import java.nio.charset.StandardCharsets;

public class VitnoAscii extends WordpressProxyAscii {

    public VitnoAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://vintageisthenewold.com";
        this.pageSize = 9;
        this.httpUserAgent = CHROME_AGENT;

    }
    private static final byte[] LOGO_BYTES = "Vintage is the new old".getBytes(StandardCharsets.ISO_8859_1);

}
