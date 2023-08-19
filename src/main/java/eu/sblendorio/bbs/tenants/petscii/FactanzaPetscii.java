package eu.sblendorio.bbs.tenants.petscii;

public class FactanzaPetscii extends WordpressProxy {

    public FactanzaPetscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://factanza.it";
        this.pageSize = 10;
        this.screenLines = 19;
        this.showAuthor = true;
        this.httpUserAgent = CHROME_AGENT;

    }

    @Override
    protected String downstreamTransform(String s) {
        return s.replaceAll("\">Reading Time: .*fix\">minutes?</span>", "\"></span>");
    }

    private static final byte[] LOGO_BYTES = readBinaryFile("petscii/factanza.seq");

}
