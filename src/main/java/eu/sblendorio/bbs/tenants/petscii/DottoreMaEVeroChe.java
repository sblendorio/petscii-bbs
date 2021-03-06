package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.Hidden;
import static eu.sblendorio.bbs.core.PetsciiColors.*;

@Hidden
public class DottoreMaEVeroChe extends WordpressProxy {

    public DottoreMaEVeroChe() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://dottoremaeveroche.it";
        this.pageSize = 8;
        this.screenLines = 19;
        this.showAuthor = false;
    }

    private static final byte[] LOGO_BYTES = new byte[] {
        32, (byte) GREY2, -84, 32, 32, 32, -69, -84, 32, 32, 5, 'f', 'n', 'o', 'm', 'c', 'E', 'o', (byte) GREY2,
        32, 32, -127, -65, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
        32, 32, 32, -69, 13, (byte) GREY2, -84, 18, -66, -110, 32, -94, 32, 18, -84, -95,
        -110, -66, -84, -69, -84, -84, 32, -94, 32, 32, 32, 32, -127, -84, -69, 32,
        -69, 32, -69, -94, 32, -69, -69, -84, -69, 32, -84, -69, 18, -68, -110, 32,
        -84, -69, 13, (byte) GREY2, -95, 18, -95, -95, -110, 32, -95, -95, 18, -95, -110, 32,
        -95, 18, -95, -95, -110, -66, 18, -95, -65, -110, -66, 32, 32, 32, 18, -127,
        -68, -94, -110, 32, 18, -95, -95, -95, -65, -110, -66, 18, -84, -110, 32, -95,
        18, -95, -110, 32, -95, 32, -95, -95, 18, -68, -94, -110, 13, (byte) GREY2, -68, 18,
        -94, -110, 32, 18, -94, -110, 32, -68, 32, -66, -68, -66, -68, 32, 32, 18,
        -94, -110, -68, -68, -68, 32, -127, -68, -66, 32, 32, -66, 32, 18, -94, -110,
        32, -66, 32, -68, -66, 32, -68, -66, -66, -66, -68, -66, -66, -66, -66
    };

}
