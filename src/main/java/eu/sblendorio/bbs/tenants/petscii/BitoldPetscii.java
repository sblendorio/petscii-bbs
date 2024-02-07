package eu.sblendorio.bbs.tenants.petscii;

public class BitoldPetscii extends WordpressProxy {

    public BitoldPetscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.bitold.eu";
        this.pageSize = 10;
        this.screenLines = 19;
    }

    private static final byte[] LOGO_BYTES = readBinaryFile("petscii/bitold.seq");
}
