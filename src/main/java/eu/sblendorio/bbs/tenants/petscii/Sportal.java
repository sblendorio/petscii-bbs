package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.Hidden;

@Hidden
public class Sportal extends WordpressProxy {

    public Sportal() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "http://www.sportal.it";
        this.pageSize = 7;
        this.screenLines = 19;
    }

    private static final byte[] LOGO_BYTES = new byte[] {
        32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 31, -69, 32,
        32, 32, -84, 32, -127, -84, -84, 13, 31, -68, 18, -94, -68, -110, 32, -94,
        -69, -94, -69, 32, -94, 32, -69, -69, 18, -68, -110, 32, -84, -94, 18, -95,
        -110, 32, -127, -84, 18, -95, -110, -69, 13, -68, -65, 18, 31, -95, -110, -68,
        -65, -69, -95, 18, -95, -95, -127, 32, -110, 31, -95, 18, -84, -110, 32, -95,
        32, -95, 18, -95, -95, -110, 32, 18, -127, -95, -95, -110, 13, -68, -68, 31,
        -68, -68, 18, -94, -110, 32, 18, -84, -110, -66, 32, 18, -94, -110, 32, -66,
        32, -68, -66, -68, 18, -94, -110, -68, -127, -68, -68, 32, 18, -94, -110, 13
    };

}
