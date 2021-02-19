package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.Hidden;

@Hidden
public class WiredCom extends WordpressProxy {

    public WiredCom() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.wired.com";
        this.pageSize = 7;
        this.screenLines = 19;
    }

    private static final byte[] LOGO_BYTES = new byte[] {
        18, 5, -84, 32, -84, -110, -95, -84, -94, 32, 18, -84, -94, 32, -110, -84,
        -94, -69, 18, -84, -94, 32, -110, 13, -95, -95, -95, -95, 32, -95, 32, -95,
        18, -94, -66, -95, -110, -94, 32, -95, 18, 32, -95, -110, 32, -101, 32, 32,
        32, 13, 18, 5, 32, -95, -95, -110, -95, -84, 18, -68, -110, 32, -95, 18,
        32, -95, -95, -110, -94, -69, -95, 18, -94, -66, -110, 13, 18, -94, -94, -94,
        -110, -66, 32, 32, 32, 18, -94, -94, -94, -110, 32, 32, 32, 18, -94, -94,
        -94, -110, 13
    };

}
