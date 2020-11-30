package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.Hidden;
import java.nio.charset.StandardCharsets;

@Hidden
public class NextQuotidianoAscii extends WordpressProxyAscii {

    public NextQuotidianoAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.nextquotidiano.it";
        this.showAuthor = true;
        this.pageSize = 7;
    }

    private static final byte[] LOGO_BYTES = "Next Quotidiano".getBytes(StandardCharsets.ISO_8859_1);
}
