package eu.sblendorio.bbs.tenants.petscii;

public class SyncroWebPetscii extends WordpressProxy {

    public SyncroWebPetscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.syncroweb.eu";
        this.pageSize = 8;
        this.screenLines = 19;
    }

    private static final byte[] LOGO_BYTES = readBinaryFile("petscii/syncroweb.seq");

}
