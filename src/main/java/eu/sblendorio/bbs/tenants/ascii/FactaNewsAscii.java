package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.Hidden;
import java.nio.charset.StandardCharsets;

@Hidden
public class FactaNewsAscii extends WordpressProxyAscii {

    public FactaNewsAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://facta.news";
        this.showAuthor = false;
        this.pageSize = 5;
    }

    private static final byte[] LOGO_BYTES = "Facta.news".getBytes(StandardCharsets.ISO_8859_1);

}
