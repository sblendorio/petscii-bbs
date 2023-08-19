package eu.sblendorio.bbs.tenants.petscii;

public class FormichePetscii extends WordpressProxy {

    public FormichePetscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://formiche.net";
        this.pageSize = 7;
        this.screenLines = 19;
        this.showAuthor = true;
        this.httpUserAgent = CHROME_AGENT;

    }

    private static final byte[] LOGO_BYTES = readBinaryFile("petscii/formiche.seq");

}
