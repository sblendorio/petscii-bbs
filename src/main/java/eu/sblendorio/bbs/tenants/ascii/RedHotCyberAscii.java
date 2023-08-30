package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.BbsInputOutput;
import eu.sblendorio.bbs.core.Hidden;

import java.nio.charset.StandardCharsets;

@Hidden
public class RedHotCyberAscii extends WordpressProxyAscii {

    private BbsInputOutput inputOutput;

    public RedHotCyberAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.redhotcyber.com";
        this.showAuthor = true;
        this.pageSize = 5;
    }

    private static final byte[] LOGO_BYTES = "Redhotcyber.com".getBytes(StandardCharsets.ISO_8859_1);

    public RedHotCyberAscii(BbsInputOutput x) {
        this();
        this.inputOutput = x;
    }

    @Override
    public void initBbs() throws Exception {
        super.initBbs();
        if (inputOutput != null) setBbsInputOutput(inputOutput);
    }
}
