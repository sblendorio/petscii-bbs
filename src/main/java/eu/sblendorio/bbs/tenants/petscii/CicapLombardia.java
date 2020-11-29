package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.Hidden;

@Hidden
public class CicapLombardia extends WordpressProxy {

    public CicapLombardia() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://lombardia.cicap.org";
        this.pageSize = 10;
        this.screenRows = 18;
    }

    private static final byte[] LOGO_BYTES = new byte[] {
        32, 31, -94, -84, -69, -94, -84, -69, -94, 32, 5, -84, 18, -94, -110, -68,
        18, -84, -110, -84, 18, -94, -110, -84, 18, -94, -110, -69, 18, -84, -110, -65,
        13, 32, 18, 31, -94, -110, -68, -66, 18, -94, -110, -68, -66, 18, -94, -110,
        32, 5, -68, -94, -84, 18, -68, -110, -68, -94, 18, -95, -94, -110, -95, 18,
        -84, -110, -66, 13, 32, 18, 31, 32, -95, -110, -95, 18, 32, -95, -110, -95,
        18, 32, -110, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 5, -95,
        32, 18, -65, -110, -65, 18, -95, -110, -69, 18, -66, -95, -110, -65, -84, 18,
        -94, -110, -69, 18, -84, -110, -65, 18, -95, -94, -110, -69, -95, 18, -65, -110,
        -65, 13, 32, 31, -94, -84, -69, -94, -84, -69, -94, 32, 32, 32, 32, 32,
        32, 32, 32, 32, 32, 32, 5, -95, 32, -95, 18, -95, -95, -110, -68, 18,
        -95, -95, -110, -65, 18, -95, -94, -110, -95, 18, -84, -110, -65, 18, -95, -110,
        32, -95, -95, 18, -84, -69, -110, 13, 32, 18, 31, -94, -110, -68, -66, 18,
        -94, -110, -68, -66, 18, -94, -110, 32, 32, 32, 32, 32, 32, 32, 32, 32,
        32, 32, 18, 5, -94, -110, -66, -68, -66, -68, 32, -68, -68, -66, -68, 32,
        -66, -66, -68, -68, 18, -94, -110, 32, -66, -66, -68, 13
    };

}
