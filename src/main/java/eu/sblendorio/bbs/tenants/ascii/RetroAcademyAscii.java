package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.Hidden;
import java.nio.charset.StandardCharsets;

@Hidden
public class RetroAcademyAscii extends WordpressProxyAscii {

    public RetroAcademyAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.retroacademy.it";
        this.pageSize = 10;
    }

    protected static final byte[] LOGO_BYTES = "Retroacademy".getBytes(StandardCharsets.ISO_8859_1);
}
