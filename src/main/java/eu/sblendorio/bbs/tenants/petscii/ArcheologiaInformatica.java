package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.Hidden;

@Hidden
public class ArcheologiaInformatica extends WordpressProxy {

    public ArcheologiaInformatica() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.archeologiainformatica.it/";
        this.pageSize = 6;
        this.screenLines = 16;
        this.httpUserAgent = CHROME_AGENT;
    }

    private static final byte[] LOGO_BYTES = new byte[] {
        32, 32, 30, -68, -68, -68, 32, 32, 32, -68, -68, -68, -68, -68, -68, 13,
        32, -68, -68, 32, -68, -68, 32, 32, 32, 32, -68, -68, 13, -68, -68, 32,
        32, 32, -68, -68, 32, 32, 32, -68, -68, 13, -68, -68, 32, 32, 32, -68,
        -68, 32, 32, 32, -68, -68, 32, 32, -63, -46, -61, -56, -59, -49, -52, -49,
        -57, -55, -63, -55, -50, -58, -49, -46, -51, -63, -44, -55, -61, -63, 46, -55,
        -44, 13, -68, -68, -68, -68, -68, -68, -68, 32, 32, 32, -68, -68, 13, -68,
        -68, 32, 32, 32, -68, -68, 32, 32, 32, -68, -68, 13, -68, -68, 32, 32,
        32, -68, -68, 32, -68, -68, -68, -68, -68, -68, 13
    };

}
