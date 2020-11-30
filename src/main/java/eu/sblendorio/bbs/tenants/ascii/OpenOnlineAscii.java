package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.tenants.petscii.WordpressProxy;
import java.nio.charset.StandardCharsets;

@Hidden
public class OpenOnlineAscii extends WordpressProxyAscii {

    public OpenOnlineAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.open.online";
        this.showAuthor = true;
        this.pageSize = 8;
    }

    private static final byte[] LOGO_BYTES = "Open Online".getBytes(StandardCharsets.ISO_8859_1);

}
