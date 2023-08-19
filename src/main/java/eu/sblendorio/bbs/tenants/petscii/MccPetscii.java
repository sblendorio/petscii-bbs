package eu.sblendorio.bbs.tenants.petscii;

public class MccPetscii extends WordpressProxy {

    public MccPetscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.camisanicalzolari.it";
        this.pageSize = 9;
        this.screenLines = 19;
        this.showAuthor = false;
        this.httpUserAgent = CHROME_AGENT;

    }


    private static final byte[] LOGO_BYTES = readBinaryFile("petscii/mcc1.seq");

}
