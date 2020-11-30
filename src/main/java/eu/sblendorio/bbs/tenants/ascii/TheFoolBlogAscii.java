package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.Hidden;
import java.nio.charset.StandardCharsets;

@Hidden
public class TheFoolBlogAscii extends WordpressProxyAscii {

    public TheFoolBlogAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://thefool.it";
        this.pageSize = 7;
    }

    private static final byte[] LOGO_BYTES = "The Fool - Blog".getBytes(StandardCharsets.ISO_8859_1);
}
