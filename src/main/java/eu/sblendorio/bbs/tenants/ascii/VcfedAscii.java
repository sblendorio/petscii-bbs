package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.Hidden;
import java.nio.charset.StandardCharsets;

@Hidden
public class VcfedAscii extends WordpressProxyAscii {

    public VcfedAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "http://www.vcfed.org";
    }

    private static final byte[] LOGO_BYTES = "Vintage Computer Federation".getBytes(StandardCharsets.ISO_8859_1);
}
