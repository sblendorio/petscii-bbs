package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.Hidden;
import java.nio.charset.StandardCharsets;

@Hidden
public class ProTestItaliaAscii extends WordpressProxyAscii {

    public ProTestItaliaAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.pro-test.it";
        this.pageSize = 9;
    }

    private static final byte[] LOGO_BYTES = "Pro-Test Italia".getBytes(StandardCharsets.ISO_8859_1);

}
