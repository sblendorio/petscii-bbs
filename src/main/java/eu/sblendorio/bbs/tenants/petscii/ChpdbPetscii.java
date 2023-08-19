package eu.sblendorio.bbs.tenants.petscii;

public class ChpdbPetscii extends WordpressProxy {

    public ChpdbPetscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://chpdb.it";
        this.pageSize = 7;
        this.screenLines = 18;
        this.showAuthor = true;
        this.httpUserAgent = CHROME_AGENT;

    }

    private static final byte[] LOGO_BYTES = readBinaryFile("petscii/chpdb.seq");

}
