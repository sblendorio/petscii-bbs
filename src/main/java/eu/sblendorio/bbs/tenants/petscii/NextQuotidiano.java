package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.Hidden;

@Hidden
public class NextQuotidiano extends WordpressProxy {

    public NextQuotidiano() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.nextquotidiano.it";
        this.pageSize = 8;
        this.screenRows = 19;
        this.showAuthor = true;
    }

    private static final byte[] LOGO_BYTES = new byte[] {
        32, 32, 32, 32, 32, 32, 28, -65, 32, -84, -66, 5, -69, 13, 32, -94,
        -69, 32, -94, 32, 32, 28, -65, -66, 32, 18, 5, -84, -110, 32, 32, -104,
        -84, 18, -94, -110, -69, 18, -95, -110, 32, -95, 18, -65, -110, -65, -68, 18,
        -84, -95, -95, -94, -110, -69, -95, 18, -65, -110, -65, 18, -95, -110, -69, -95,
        18, -65, -110, -65, 13, 32, 5, -95, 18, -95, -95, -65, -110, -66, 28, -84,
        -66, -65, 32, 5, -95, 32, 32, 18, -104, -95, -110, 32, -95, 18, -95, -110,
        32, -95, -95, 18, -95, -110, 32, -95, 18, -95, -95, -110, 32, -95, -95, 18,
        -68, -66, -95, -110, -68, -95, -95, 18, -95, -110, 13, 32, 5, -66, -68, 32,
        18, -94, -110, 32, 28, -66, 32, 32, -66, 5, -68, 32, 32, 32, 18, -104,
        -94, -94, -110, 32, 18, -94, -110, 32, -68, -66, 32, -66, -68, -68, 18, -94,
        -110, 32, -66, -66, -68, -68, 32, -66, -68, -66, 13
    };
}
