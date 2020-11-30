package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.Hidden;
import java.nio.charset.StandardCharsets;

@Hidden
public class IlPostAscii extends WordpressProxyAscii {

    public IlPostAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.ilpost.it";
        this.showAuthor = true;
        this.pageSize = 7;
    }

    private static final byte[] LOGO_BYTES = "Il Post".getBytes(StandardCharsets.ISO_8859_1);

}
