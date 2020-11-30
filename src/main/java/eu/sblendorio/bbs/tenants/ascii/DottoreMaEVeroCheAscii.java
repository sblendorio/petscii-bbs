package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.Hidden;
import java.nio.charset.StandardCharsets;

@Hidden
public class DottoreMaEVeroCheAscii extends WordpressProxyAscii {

    public DottoreMaEVeroCheAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://dottoremaeveroche.it";
        this.pageSize = 10;
    }

    private static final byte[] LOGO_BYTES = "Dottore, ma e' vero che...".getBytes(StandardCharsets.ISO_8859_1);
}
