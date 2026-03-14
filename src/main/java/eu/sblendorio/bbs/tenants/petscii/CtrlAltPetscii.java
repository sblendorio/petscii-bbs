package eu.sblendorio.bbs.tenants.petscii;

public class CtrlAltPetscii extends WordpressProxy {

    public CtrlAltPetscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://ctrlalt.museum";
        this.pageSize = 7;
        this.screenLines = 19;
    }

    private static final byte[] LOGO_BYTES = readBinaryFile("petscii/ctrlalt.seq");

}
