package eu.sblendorio.bbs.tenants;

import eu.sblendorio.bbs.core.Hidden;

@Hidden
public class Vcfed extends WordpressProxy {

    public Vcfed() {
        super();
        this.logo = LOGO;
        this.domain = "https://www.vcfed.org/wp";
    }

    public final static byte[] LOGO = new byte [] {
        32, 32, 32, 32, 32, 32, -127, -84, 32, 32, 32, 32, -84, 13, 18, -95,
        -110, 32, 18, -65, -110, -84, 18, -94, -110, -84, 18, -68, -110, -84, 18, -69,
        -110, -69, 18, -65, -69, -110, 32, 32, -98, -84, 18, -94, -110, -69, 18, -65,
        -68, -95, -110, 32, 18, -95, -110, -84, 18, -84, -110, 13, -127, -68, 18, -65,
        -110, 32, -68, -94, 32, -95, -68, 18, -68, -110, 32, -65, 18, -66, -110, 32,
        32, 18, -98, -95, -110, 32, -95, 18, -69, -110, -69, -68, 18, -65, -65, -110,
        -84, 18, -65, -110, 13, 13
    };

}
