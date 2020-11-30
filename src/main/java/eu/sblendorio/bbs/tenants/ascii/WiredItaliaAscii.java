package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.Hidden;
import java.nio.charset.StandardCharsets;

@Hidden
public class WiredItaliaAscii extends WordpressProxyAscii {

    public WiredItaliaAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.wired.it";
    }

    private static final byte[] LOGO_BYTES = "Wired Italia".getBytes(StandardCharsets.ISO_8859_1);

}