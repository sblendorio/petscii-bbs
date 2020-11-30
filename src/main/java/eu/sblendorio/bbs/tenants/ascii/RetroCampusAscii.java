package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.tenants.petscii.WordpressProxy;
import java.nio.charset.StandardCharsets;

@Hidden
public class RetroCampusAscii extends WordpressProxyAscii {

    public RetroCampusAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "http://www.retrocampus.com";
        this.pageSize = 9;
    }

    private static final byte[] LOGO_BYTES = "Retrocampus".getBytes(StandardCharsets.ISO_8859_1);

}
