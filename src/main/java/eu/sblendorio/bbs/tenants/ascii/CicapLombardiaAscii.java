package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.Hidden;
import java.nio.charset.StandardCharsets;

@Hidden
public class CicapLombardiaAscii extends WordpressProxyAscii {

    public CicapLombardiaAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://gruppi.cicap.org/lombardia";
        this.pageSize = 11;
    }

    private static final byte[] LOGO_BYTES = "CICAP Lombardia".getBytes(StandardCharsets.ISO_8859_1);

}
