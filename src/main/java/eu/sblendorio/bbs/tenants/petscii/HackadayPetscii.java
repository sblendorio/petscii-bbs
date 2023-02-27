package eu.sblendorio.bbs.tenants.petscii;

public class HackadayPetscii extends WordpressProxy {

    public HackadayPetscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.hackaday.com";
        this.pageSize = 9;
        this.screenLines = 19;
        this.showAuthor = true;
    }

    private static final byte[] LOGO_BYTES = readBinaryFile("petscii/hackaday.seq");

}
