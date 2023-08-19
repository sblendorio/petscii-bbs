package eu.sblendorio.bbs.tenants.petscii;

public class InfosecNewsPetscii extends WordpressProxy {

    public InfosecNewsPetscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.infosec.news";
        this.pageSize = 8;
        this.screenLines = 19;
        this.showAuthor = true;
        this.httpUserAgent = CHROME_AGENT;

    }

    private static final byte[] LOGO_BYTES = readBinaryFile("petscii/infosecnews.seq");

}
