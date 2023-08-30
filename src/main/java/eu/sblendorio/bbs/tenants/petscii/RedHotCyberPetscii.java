package eu.sblendorio.bbs.tenants.petscii;

public class RedHotCyberPetscii extends WordpressProxy {

    public RedHotCyberPetscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.redhotcyber.com";
        this.pageSize = 6;
        this.screenLines = 19;
        this.showAuthor = true;
        this.httpUserAgent = CHROME_AGENT;

    }

    private static final byte[] LOGO_BYTES = readBinaryFile("petscii/rhc.seq");

}
