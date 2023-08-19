package eu.sblendorio.bbs.tenants.petscii;

public class LaRagionePetscii extends WordpressProxy {

    public LaRagionePetscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://laragione.eu";
        this.pageSize = 7;
        this.screenLines = 19;
        this.showAuthor = true;
        this.httpUserAgent = CHROME_AGENT;

    }

    private static final byte[] LOGO_BYTES = readBinaryFile("petscii/laragione.seq");

}
