package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.Hidden;

@Hidden
public class DavidPuenteBlog extends WordpressProxy {

    public DavidPuenteBlog() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.davidpuente.it";
        this.pageSize = 7;
        this.screenLines = 18;
    }

    private static final byte[] LOGO_BYTES = new byte[] {
        32, 32, 32, 32, 32, 32, 32, 32, 32, 18, 5, -95, -94, -110, -69, 32,
        32, 32, 32, 32, 18, -97, -65, -110, -69, 32, 32, 5, -95, 32, 18, -84,
        -110, -65, 32, 32, 32, 32, 32, 32, 32, -84, 18, -68, -110, 13, 32, -97,
        -55, 76, 32, -62, 76, 79, 71, 32, 18, 5, -95, -110, 32, -95, -84, -69,
        -84, 32, -84, -97, -68, 32, 32, 5, -94, -95, 32, -95, 18, -95, -110, -84,
        32, -69, -84, -69, -84, -94, 32, -95, 32, -84, -69, 13, 32, 32, 32, 32,
        32, 32, 32, 32, 32, 18, -95, -110, 32, -95, -84, 18, -66, -110, -68, -69,
        18, -65, -110, 32, -97, -95, 18, 5, -95, -110, 32, -95, 32, 18, -84, -110,
        -66, 18, -95, -110, 32, -95, 18, -68, -66, -95, -110, 32, -95, -95, 32, 18,
        -68, -66, -110, 13, 32, 32, 32, 32, -97, 68, 73, 32, 32, 32, 18, 5,
        -95, -110, -94, -66, -65, 18, -66, -110, 32, -65, -66, 32, 18, -97, -95, -110,
        5, -68, -94, -95, 32, -95, 32, -68, -94, -95, -65, -94, 18, -95, -110, 32,
        -95, -65, -69, -65, -94, 13, 13
    };

}
