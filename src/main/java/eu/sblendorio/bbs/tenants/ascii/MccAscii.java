package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.Hidden;

import java.nio.charset.StandardCharsets;

@Hidden
public class MccAscii extends WordpressProxyAscii {

    public MccAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.camisanicalzolari.it";
        this.showAuthor = false;
        this.pageSize = 9;
        this.httpUserAgent =
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/85.0.4183.102 Safari/537.36";
    }

    private static final byte[] LOGO_BYTES = "Marco Camisani Calzolari".getBytes(StandardCharsets.ISO_8859_1);

}
