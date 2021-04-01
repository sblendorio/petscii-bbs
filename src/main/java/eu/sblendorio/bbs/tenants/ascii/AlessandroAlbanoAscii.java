package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.Hidden;
import java.nio.charset.StandardCharsets;

@Hidden
public class AlessandroAlbanoAscii extends WordpressProxyAscii {

    public AlessandroAlbanoAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.alessandroalbano.it";
        this.showAuthor = true;
        this.pageSize = 6;
    }

    private static final byte[] LOGO_BYTES = "Alessandro Albano".getBytes(StandardCharsets.ISO_8859_1);

}
