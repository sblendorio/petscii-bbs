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
        this.httpUserAgent = CHROME_AGENT;
    }

    private static final byte[] LOGO_BYTES = "Marco Camisani Calzolari".getBytes(StandardCharsets.ISO_8859_1);

}
