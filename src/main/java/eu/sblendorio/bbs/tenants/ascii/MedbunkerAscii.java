package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.Hidden;
import java.nio.charset.StandardCharsets;

@Hidden
public class MedbunkerAscii extends GoogleBloggerProxyAscii {

    public MedbunkerAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.blogUrl = "https://medbunker.blogspot.com";
        this.pageSize = 10;
    }

    private static final byte[] LOGO_BYTES = "Medbunker".getBytes(StandardCharsets.ISO_8859_1);

}
