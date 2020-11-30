package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.Hidden;
import java.nio.charset.StandardCharsets;

@Hidden
public class IndieRetroNewsAscii extends GoogleBloggerProxyAscii {

    public IndieRetroNewsAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.blogUrl = "https://www.indieretronews.com/";
        this.pageSize = 7;
    }

    private static final byte[] LOGO_BYTES = "Indie Retro News".getBytes(StandardCharsets.ISO_8859_1);

}
