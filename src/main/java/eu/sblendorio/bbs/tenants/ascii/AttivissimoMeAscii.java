package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.BbsInputOutput;
import eu.sblendorio.bbs.core.Hidden;

import java.nio.charset.StandardCharsets;

@Hidden
public class AttivissimoMeAscii extends WordpressProxyAscii {
    private BbsInputOutput inputOutput;

    public AttivissimoMeAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://attivissimo.me/";
        this.pageSize = 6;
        this.showAuthor = true;
        this.httpUserAgent = CHROME_AGENT;
    }

    public AttivissimoMeAscii(BbsInputOutput x) {
        this();
        this.inputOutput = x;
    }

    public void initBbs() throws Exception {
        super.initBbs();
        if (inputOutput != null) setBbsInputOutput(inputOutput);
    }


    private static final byte[] LOGO_BYTES = "Attivissimo Me".getBytes(StandardCharsets.ISO_8859_1);

}