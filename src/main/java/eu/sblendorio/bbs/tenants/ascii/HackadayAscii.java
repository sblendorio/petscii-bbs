package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.BbsInputOutput;
import eu.sblendorio.bbs.tenants.petscii.WordpressProxy;

import java.nio.charset.StandardCharsets;

public class HackadayAscii extends WordpressProxyAscii {

    private BbsInputOutput inputOutput = null;

    public HackadayAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.hackaday.com";
        this.pageSize = 9;
        this.screenLines = 19;
        this.showAuthor = true;
    }

    public String by() { return "by"; }
    private static final byte[] LOGO_BYTES = "Hackaday".getBytes(StandardCharsets.ISO_8859_1);

    public HackadayAscii(BbsInputOutput inputOutput) {
        this();
        this.inputOutput = inputOutput;
    }

    @Override
    public void initBbs() throws Exception {
        super.initBbs();
        if (inputOutput != null) setBbsInputOutput(inputOutput);
    }
}
