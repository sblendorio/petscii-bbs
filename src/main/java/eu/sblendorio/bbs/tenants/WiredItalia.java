package eu.sblendorio.bbs.tenants;

public class WiredItalia extends WordpressProxy {

    public WiredItalia() {
        super();
        this.logo = LOGO;
        this.domain = "https://www.wired.it";
        this.pageSize = 7;
        this.screenRows = 19;
    }

    public final static byte[] LOGO = new byte[] {
        18, 5, -84, 32, -84, -110, -95, -84, -94, 32, 18, -84, -94, 32, -110, -84,
        -94, -69, 18, -84, -94, 32, -110, 13, -95, -95, -95, -95, 32, -95, 32, -95,
        18, -94, -66, -95, -110, -94, 32, -95, 18, 32, -95, -110, 32, -101, 46, -55,
        -44, 13, 18, 5, 32, -95, -95, -110, -95, -84, 18, -68, -110, 32, -95, 18,
        32, -95, -95, -110, -94, -69, -95, 18, -94, -66, -110, 13, 18, -94, -94, -94,
        -110, -66, 32, 32, 32, 18, -94, -94, -94, -110, 32, 32, 32, 18, -94, -94,
        -94, -110, 13
    };

}