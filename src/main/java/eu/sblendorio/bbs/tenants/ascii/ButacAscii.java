package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.BbsInputOutput;
import eu.sblendorio.bbs.core.Hidden;
import java.nio.charset.StandardCharsets;

@Hidden
public class ButacAscii extends WordpressProxyAscii {

    private BbsInputOutput inputOutput;

    public ButacAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.butac.it";
        this.showAuthor = true;
        this.pageSize = 10;
    }

    private static final byte[] LOGO_BYTES = "Butac.it".getBytes(StandardCharsets.ISO_8859_1);

    public ButacAscii(BbsInputOutput x) {
        this();
        this.inputOutput = x;
    }

    @Override
    public void initBbs() throws Exception {
        super.initBbs();
        if (inputOutput != null) setBbsInputOutput(inputOutput);
    }
}
