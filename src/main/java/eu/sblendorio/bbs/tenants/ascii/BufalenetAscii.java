package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.BbsInputOutput;
import eu.sblendorio.bbs.core.Hidden;

import java.nio.charset.StandardCharsets;

@Hidden
public class BufalenetAscii extends WordpressProxyAscii {

    private BbsInputOutput inputOutput;

    public BufalenetAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.bufale.net";
        this.showAuthor = true;
        this.pageSize = 5;
    }

    private static final byte[] LOGO_BYTES = "Bufale.net".getBytes(StandardCharsets.ISO_8859_1);

    public BufalenetAscii(BbsInputOutput x) {
        this();
        this.inputOutput = x;
    }

    @Override
    public void initBbs() throws Exception {
        super.initBbs();
        if (inputOutput != null) setBbsInputOutput(inputOutput);
    }
}
