package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.Hidden;

@Hidden
public class Linkiesta extends WordpressProxy {

    public Linkiesta() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.linkiesta.it";
        this.pageSize = 6;
        this.screenLines = 19;
    }

    private static final byte[] LOGO_BYTES = new byte[] {
        18, 31, -84, -94, 32, -84, -94, -84, -69, 32, -94, -84, -69, -84, -69, -110,
        -84, -94, -84, -94, -94, -69, -84, -94, -69, -94, -94, -69, 32, -69, 13, 18,
        32, -95, 32, 32, -95, 32, -110, -84, 18, -69, -95, 32, -110, -68, 18, -66,
        32, -110, 32, -95, 32, 18, -68, -110, -69, -66, -65, -94, 32, -66, -95, -66,
        18, -65, -110, -68, -69, 13, 18, -84, -110, -68, -66, 18, -84, -110, -68, 18,
        -84, -95, -68, -110, -68, 18, -84, -95, -65, -69, -110, -84, 18, -68, -110, -84,
        18, -68, -110, -94, -95, -94, -94, -66, -84, 18, -68, -110, 32, 18, -84, -94,
        -110, -95, 13, 18, -94, -94, -94, -94, -94, -94, -94, -94, -94, -94, -94, -94,
        -94, -110, 13
    };

}
