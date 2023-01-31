package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.Hidden;

@Hidden
public class RetroCampus extends WordpressProxy {

    public RetroCampus() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "http://www.retrocampus.com";
        this.pageSize = 9;
        this.screenLines = 19;
        this.httpUserAgent =
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/85.0.4183.102 Safari/537.36";

    }

    private static final byte[] LOGO_BYTES = new byte[] {
        18, 5, -84, -94, -69, -84, -94, -69, -94, -94, -69, -94, -94, 32, -94, -94,
        -69, -110, 32, -94, -69, 32, -84, 32, -84, -94, -94, -94, -94, -84, -94, 32,
        -69, 32, -69, -84, -94, -69, 13, -95, 18, -94, -110, -68, -95, 18, -94, 32,
        32, -110, -68, 18, 32, -110, -68, -66, 18, -69, -95, -110, -95, 18, -95, -95,
        -110, 32, -68, -84, -66, -65, 32, -95, 18, -95, -110, 32, -95, 18, -95, -110,
        32, -95, -95, 32, -95, -65, -94, 13, -95, 18, -95, -110, -95, -95, -68, 18,
        -69, 32, -110, 32, 18, 32, -110, 32, 18, 32, -95, -110, -68, 18, -94, -95,
        -110, -68, -94, 18, -65, -95, -94, -69, -110, 32, -95, 18, -95, -110, 32, -95,
        18, -95, -94, -110, 32, -65, -94, -66, -94, -94, -66, 13, 18, -94, -94, -94,
        -94, -94, -94, -94, -94, -94, -94, -94, -94, -94, -94, -94, -110, 13
    };

}
