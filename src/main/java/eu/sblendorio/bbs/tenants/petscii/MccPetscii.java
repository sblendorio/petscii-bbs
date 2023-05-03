package eu.sblendorio.bbs.tenants.petscii;

public class MccPetscii extends WordpressProxy {

    public MccPetscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.camisanicalzolari.it";
        this.pageSize = 9;
        this.screenLines = 19;
        this.showAuthor = false;
        this.httpUserAgent =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/85.0.4183.102 Safari/537.36";

    }


    private static final byte[] LOGO_BYTES = readBinaryFile("petscii/mcc1.seq");

}
