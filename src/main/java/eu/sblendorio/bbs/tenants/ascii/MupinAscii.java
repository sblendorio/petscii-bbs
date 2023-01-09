package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.Hidden;

import java.nio.charset.StandardCharsets;

@Hidden
public class MupinAscii extends WordpressProxyAscii {

    public MupinAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.mupin.it";
        this.showAuthor = true;
        this.pageSize = 5;
    }

    private static final byte[] LOGO_BYTES = "Mupin.it".getBytes(StandardCharsets.ISO_8859_1);
}
