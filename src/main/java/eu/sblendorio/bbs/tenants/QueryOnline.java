package eu.sblendorio.bbs.tenants;

public class QueryOnline extends WordpressProxy {

    public QueryOnline() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.queryonline.it";
        this.pageSize = 10;
        this.screenRows = 18;
        this.showAuthor = false;
    }

    private static final byte[] LOGO_BYTES = new byte[] {
        5, -84, 18, -94, -94, -110, -65, 32, 18, -95, -110, 32, 32, -95, -84, 18,
        -94, -94, -110, -69, 18, -95, -65, -94, -110, 32, -95, 32, 18, -95, -110, 32,
        -102, -52, 65, 32, 82, 73, 86, 73, 83, 84, 65, 32, 31, -94, -84, -69,
        -94, -84, -69, -94, 13, 5, -95, 32, 32, 32, -95, 18, -95, -110, 32, 32,
        -95, 18, -95, -94, -94, -110, 32, 18, -95, -110, 32, 32, 32, -65, 32, 18,
        -95, -110, 32, 32, -102, 68, 69, 76, 32, -61, -55, -61, -63, -48, 32, 18,
        31, -94, -110, -68, -66, 18, -94, -110, -68, -66, 18, -94, -110, 13, 5, -95,
        32, 32, 32, -95, 32, 18, -94, -94, -110, 32, 32, 18, -94, -94, -110, 32,
        -68, 32, 32, 32, 32, 18, -94, -69, -110, 32, 32, 32, 32, 32, 32, 32,
        32, 32, 32, 32, 32, 18, 31, 32, -95, -110, -95, 18, 32, -95, -110, -95,
        18, 32, -110, 13, 5, -65, 32, 32, -127, -65, 5, -66, -127, -52, -63, 32,
        -45, -61, -55, -59, -50, -38, -63, 32, -55, -50, -60, -63, -57, -63, 32, -55,
        32, -51, -39, -45, -44, -59, -46, -55, 31, -94, -84, -69, -94, -84, -69, -94,
        13, 32, 18, 5, -94, -94, -110, -66, -127, -66, 32, 32, 32, 32, 32, 32,
        32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
        32, 32, 32, 32, 32, 18, 31, -94, -110, -68, -66, 18, -94, -110, -68, -66,
        18, -94, -110, 13
    };
}
