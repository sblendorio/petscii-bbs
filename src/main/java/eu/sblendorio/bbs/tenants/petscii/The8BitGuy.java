package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.Hidden;

@Hidden
public class The8BitGuy extends WordpressProxy {

    public The8BitGuy() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "http://www.the8bitguy.com";
        this.pageSize = 8;
        this.screenRows = 15;
    }

    private static final byte[] LOGO_BYTES = new byte[] {
        5, -66, -66, -66, -66, -66, -66, 18, -94, -94, -94, -84, -110, -66, -95, 18,
        -95, -95, -94, -94, -94, -94, -94, -94, -110, -68, -68, -68, -68, -68, -68, 32,
        32, 32, -102, -84, -94, 32, -94, 32, -84, 13, -84, -94, -69, 32, 32, -84,
        -94, -94, 32, 5, -95, 32, 18, -84, -69, -95, -94, -110, 32, -102, -84, -94,
        -69, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, -95, 32, -95, -95, -65,
        18, -95, -110, 13, 18, 32, -110, 32, 18, 32, -110, 32, 32, 18, -95, -110,
        -95, 18, -95, -110, -95, 5, -66, 32, -66, -68, -68, 18, -94, -110, -66, 18,
        -102, 32, -110, 32, 18, -94, -110, 32, 32, 32, 32, 32, 32, 32, 32, 32,
        32, -68, 18, -94, -110, 32, -66, 32, 18, -94, -110, 13, 18, -69, -110, -94,
        18, -84, -110, -84, -94, 18, -95, -68, -66, -110, -66, -68, -66, -84, 18, 32,
        -110, -69, 32, 32, 18, 32, -110, -68, 18, -68, -95, -110, -95, 18, -95, -110,
        -95, 18, 32, -110, 32, 18, 32, -110, 13, 18, 32, -110, 32, 18, 32, -110,
        32, 32, 18, -95, -110, -95, 18, -95, -110, -95, 18, -69, -110, -95, 32, 18,
        32, -110, 32, 32, 32, 18, 32, -110, 32, 18, 32, -95, -110, -95, 18, -95,
        -110, -95, 18, -95, -110, -95, 18, 32, -110, 32, -97, -68, 32, 32, -95, 18,
        -68, -110, 32, 18, -95, -95, -94, -94, -94, -94, -110, 13, 18, -102, -69, -110,
        -94, 18, -84, -110, 32, 32, 18, -95, -68, -66, -110, -66, 18, -66, -68, -110,
        32, 18, -69, -110, -69, 32, 32, 18, -69, -110, -94, 18, -84, -110, -68, 18,
        -68, -66, -110, -66, 32, 18, -94, 32, -110, 32, -97, -68, 32, 32, -66, -95,
        -66, 18, -95, -95, -110, -94, 13, 32, 32, 32, 32, 32, 32, 32, 32, 32,
        32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, -102, -68,
        18, -94, -110, -66, 32, 18, -97, -95, -110, 32, 32, -66, -95, -68, 18, -66,
        -95, -110, 13, 5, -66, -66, -66, -66, -66, -66, 18, -94, -110, -66, 18, -94,
        -94, -110, -66, 18, -94, -94, -94, -94, -110, -68, 18, -94, -94, -110, -68, 18,
        -94, -110, -68, -68, -68, -68, -68, -68, 32, -97, -68, 18, -94, -110, -66, -66,
        -66, 32, -68, -68, 18, -94, -110, -68, -68, -68, 13
    };

}
