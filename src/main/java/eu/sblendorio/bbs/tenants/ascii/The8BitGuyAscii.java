package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.BbsInputOutput;
import eu.sblendorio.bbs.core.Hidden;
import java.nio.charset.StandardCharsets;

@Hidden
public class The8BitGuyAscii extends WordpressProxyAscii {

    private BbsInputOutput inputOutput = null;

    public The8BitGuyAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "http://www.the8bitguy.com";
        this.showAuthor = true;
        this.pageSize = 10;
    }

    private static final byte[] LOGO_BYTES = "The 8-Bit Guy".getBytes(StandardCharsets.ISO_8859_1);

    public The8BitGuyAscii(BbsInputOutput x) {
        this();
        this.inputOutput = x;
    }

    @Override
    public void initBbs() throws Exception {
        super.initBbs();
        if (inputOutput != null) setBbsInputOutput(inputOutput);
    }

}
