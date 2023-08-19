package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.Hidden;

import java.nio.charset.StandardCharsets;

@Hidden
public class ChpdbAscii extends WordpressProxyAscii {

    public ChpdbAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://chpdb.it";
        this.pageSize = 5;
        this.showAuthor = true;
        this.httpUserAgent = CHROME_AGENT;
    }

    private static final byte[] LOGO_BYTES = "Chi ha paura del buio?".getBytes(StandardCharsets.ISO_8859_1);

}