package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.BbsInputOutput;
import eu.sblendorio.bbs.core.Hidden;

import java.nio.charset.StandardCharsets;

@Hidden
public class MupinAscii extends WordpressProxyAscii {

    private BbsInputOutput inputOutput = null;

    public MupinAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.mupin.it";
        this.showAuthor = true;
        this.pageSize = 5;
    }

    private static final byte[] LOGO_BYTES = "Mupin.it".getBytes(StandardCharsets.ISO_8859_1);

    public MupinAscii(BbsInputOutput x) {
        this();
        this.inputOutput = x;
    }

    @Override
    public void initBbs() throws Exception {
        super.initBbs();
        if (inputOutput != null) setBbsInputOutput(inputOutput);
    }
}
