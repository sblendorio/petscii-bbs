package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.Hidden;

@Hidden
public class IlPost extends WordpressProxy {

    public IlPost() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.ilpost.it";
        this.pageSize = 7;
        this.screenRows = 18;
        this.showAuthor = true;
    }

    private static final byte[] LOGO_BYTES = new byte[] {
        5, -66, -95, 18, -95, 32, 32, -110, -69, 18, -66, 32, 32, -110, -69,
        32, -97, -94, -94, -95, 18, 5, 32, 32, 32, 32, -110, 13, -95, -95,
        18, -95, -110, -95, 18, -95, -110, -95, 18, 32, -110, 32, 18, -95, -110, -95,
        18, -97, -66, -110, -66, -68, -66, 32, 18, 5, -95, -110, -95, 13, -66,
        -66, 18, -95, 32, 32, -110, -66, 18, 32, -110, 32, 18, -95, -110, -95, 18,
        -97, -94, -94, -69, -110, -95, 32, 18, 5, -95, -110, -95, 13, 32, 32,
        18, -95, -110, -95, 32, 32, 18, -69, 32, 32, -110, -66, 18, -97, -84, -68,
        -84, -110, 32, 32, 18, 5, -95, -110, -95, 13, 13
    };

    private static final byte[] LOGO_BYTES_OLD = new byte[] {
        32, 5, -66, -95, 32, 18, -95, 32, 32, -110, -69, -84, 18, 32, 32, -68,
        -110, 32, 32, -97, -94, -94, -95, 18, 5, -95, 32, 32, 32, -110, -95, 13,
        32, -95, -95, 32, 18, -95, -110, -95, 18, -95, -110, -95, 18, -95, -110, -95,
        32, 18, 32, -110, 32, 18, -97, -66, -110, -66, -68, -66, 32, 32, 18, 5,
        32, -110, 13, 32, -66, -66, 32, 18, -95, 32, 32, -110, -66, 18, -95, -110,
        -95, 32, 18, 32, -110, 32, 18, -97, -94, -94, -69, -110, -95, 32, 32, 18,
        5, 32, -110, 13, 32, 32, 32, 32, 18, -95, -110, -95, 32, 32, -68, 18,
        32, 32, -84, -110, 32, 18, -97, -84, -68, -84, -110, 32, 32, 32, 18, 5,
        32, -110, 13, 13
    };

}
