package eu.sblendorio.bbs.tenants.ascii;

import java.nio.charset.StandardCharsets;

public class VitnoAscii extends WordpressProxyAscii {

    public VitnoAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://vintageisthenewold.com";
        this.pageSize = 9;
        this.httpUserAgent =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/85.0.4183.102 Safari/537.36";

    }
    private static final byte[] LOGO_BYTES = "Vintage is the new old".getBytes(StandardCharsets.ISO_8859_1);

}
