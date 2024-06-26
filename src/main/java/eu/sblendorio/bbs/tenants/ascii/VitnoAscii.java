package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.BbsInputOutput;

import java.nio.charset.StandardCharsets;

public class VitnoAscii extends WordpressProxyAscii {
    private BbsInputOutput inputOutput = null;

    public VitnoAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://vitno.org";
        this.pageSize = 9;
        this.httpUserAgent = CHROME_AGENT;

    }
    private static final byte[] LOGO_BYTES = "Vintage is the new old".getBytes(StandardCharsets.ISO_8859_1);

    public VitnoAscii(BbsInputOutput inputOutput) {
        this();
        this.inputOutput = inputOutput;
    }

    @Override
    public void initBbs() throws Exception {
        super.initBbs();
        if (inputOutput != null) setBbsInputOutput(inputOutput);
    }

}
