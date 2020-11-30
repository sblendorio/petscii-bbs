package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.Hidden;
import java.nio.charset.StandardCharsets;

@Hidden
public class SportalAscii extends WordpressProxyAscii {

    public SportalAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "http://www.sportal.it";
        this.pageSize = 10;
    }

    private static final byte[] LOGO_BYTES = "Sportal".getBytes(StandardCharsets.ISO_8859_1);

}
