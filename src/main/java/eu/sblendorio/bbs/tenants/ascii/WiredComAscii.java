package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.BbsInputOutput;
import eu.sblendorio.bbs.core.Hidden;

import java.nio.charset.StandardCharsets;

@Hidden
public class WiredComAscii extends WordpressProxyAscii {
    private BbsInputOutput inputOutput = null;

    public WiredComAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.wired.com";
    }

    private static final byte[] LOGO_BYTES = "Wired".getBytes(StandardCharsets.ISO_8859_1);

    public WiredComAscii(BbsInputOutput inputOutput) {
        this();
        this.inputOutput = inputOutput;
    }

    @Override
    public void initBbs() throws Exception {
        super.initBbs();
        if (inputOutput != null) setBbsInputOutput(inputOutput);
    }


}