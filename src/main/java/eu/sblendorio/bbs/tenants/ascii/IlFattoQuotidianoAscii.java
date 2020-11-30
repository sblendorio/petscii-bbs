package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.Hidden;
import java.nio.charset.StandardCharsets;

@Hidden
public class IlFattoQuotidianoAscii extends WordpressProxyAscii {

    public IlFattoQuotidianoAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.ilfattoquotidiano.it";
        this.showAuthor = true;
        this.pageSize = 4;
    }

    private static final byte[] LOGO_BYTES = "Il Fatto Quotidiano".getBytes(StandardCharsets.ISO_8859_1);

}
