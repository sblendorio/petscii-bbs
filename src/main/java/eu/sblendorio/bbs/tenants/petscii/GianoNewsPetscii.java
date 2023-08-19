package eu.sblendorio.bbs.tenants.petscii;

public class GianoNewsPetscii extends WordpressProxy {

    public GianoNewsPetscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.giano.news";
        this.pageSize = 8;
        this.screenLines = 19;
        this.showAuthor = true;
        this.httpUserAgent = CHROME_AGENT;

    }

    private static final byte[] LOGO_BYTES = readBinaryFile("petscii/giano.seq");

    @Override
    protected String downstreamTransform(String s) {
        return s
                .replaceAll("\">0:00</span>", "\"></span>")
                .replaceAll("TE LO LEGGO IO", "")
                .replaceAll("\">&#47;</span>", "\"></span>");
    }

}
