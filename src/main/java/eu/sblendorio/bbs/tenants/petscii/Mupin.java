package eu.sblendorio.bbs.tenants.petscii;

public class Mupin extends WordpressProxy {

    public Mupin() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.mupin.it";
        this.pageSize = 5;
        this.screenLines = 19;
        this.showAuthor = true;
    }

    private static final byte[] LOGO_BYTES = readBinaryFile("petscii/mupin.seq");

}
