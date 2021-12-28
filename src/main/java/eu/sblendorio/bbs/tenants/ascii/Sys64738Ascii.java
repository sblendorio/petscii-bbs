package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.Hidden;
import java.nio.charset.StandardCharsets;

@Hidden
public class Sys64738Ascii extends WordpressProxyAscii {

    public Sys64738Ascii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://retroprogramming.it";
        this.pageSize = 10;
    }

    private static final byte[] LOGO_BYTES = "Sys64738".getBytes(StandardCharsets.ISO_8859_1);
}
