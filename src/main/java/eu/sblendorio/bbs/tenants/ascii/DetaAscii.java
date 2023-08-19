package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.Hidden;

import java.nio.charset.StandardCharsets;

@Hidden
public class DetaAscii extends WordpressProxyAscii {

    public DetaAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.detarobot.it/";
        this.pageSize = 5;
        this.showAuthor = true;
        this.httpUserAgent = CHROME_AGENT;
    }

    private static final byte[] LOGO_BYTES = "D.E.T.A.".getBytes(StandardCharsets.ISO_8859_1);

}