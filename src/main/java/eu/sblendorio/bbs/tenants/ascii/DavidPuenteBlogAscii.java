package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.Hidden;
import java.nio.charset.StandardCharsets;

@Hidden
public class DavidPuenteBlogAscii extends WordpressProxyAscii {

    public DavidPuenteBlogAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.davidpuente.it";
    }

    private static final byte[] LOGO_BYTES = "David Puente Blog".getBytes(StandardCharsets.ISO_8859_1);

}
