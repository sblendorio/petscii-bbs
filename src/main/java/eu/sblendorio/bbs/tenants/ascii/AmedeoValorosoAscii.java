package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.Hidden;

import java.nio.charset.StandardCharsets;

@Hidden
public class AmedeoValorosoAscii extends WordpressProxyAscii {

    public AmedeoValorosoAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.valoroso.it";
        this.showAuthor = true;
        this.pageSize = 10;
    }

    private static final byte[] LOGO_BYTES = "Amedeo Valoroso".getBytes(StandardCharsets.ISO_8859_1);

}
