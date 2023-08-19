package eu.sblendorio.bbs.tenants.petscii;

public class DetaPetscii extends WordpressProxy {

    public DetaPetscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.detarobot.it/";
        this.pageSize = 7;
        this.screenLines = 18;
        this.showAuthor = true;
        this.httpUserAgent = CHROME_AGENT;
    }

    private static final byte[] LOGO_BYTES = readBinaryFile("petscii/deta.seq");

}
