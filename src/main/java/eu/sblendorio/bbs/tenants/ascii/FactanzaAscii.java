package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.Hidden;

import java.nio.charset.StandardCharsets;

@Hidden
public class FactanzaAscii extends WordpressProxyAscii {

    public FactanzaAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://factanza.it";
        this.pageSize = 10;
        this.showAuthor = true;
        this.httpUserAgent = CHROME_AGENT;
    }

    @Override
    protected String downstreamTransform(String s) {
        return s.replaceAll("\">Reading Time: .*fix\">minutes?</span>", "\"></span>");
    }

    private static final byte[] LOGO_BYTES = "Factanza".getBytes(StandardCharsets.ISO_8859_1);

}