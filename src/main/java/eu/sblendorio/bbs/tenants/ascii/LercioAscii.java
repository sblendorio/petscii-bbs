package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.BbsInputOutput;
import eu.sblendorio.bbs.core.Hidden;

import java.nio.charset.StandardCharsets;

@Hidden
public class LercioAscii extends WordpressProxyAscii {

    private BbsInputOutput inputOutput;

    public LercioAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.lercio.it";
        this.pageSize = 5;

    }

    private static final byte[] LOGO_BYTES = "Lercio - lo sporco che fa notizia".getBytes(StandardCharsets.ISO_8859_1);

    public LercioAscii(BbsInputOutput x) {
        this();
        this.inputOutput = x;
    }

    @Override
    public void initBbs() throws Exception {
        super.initBbs();
        if (inputOutput != null) setBbsInputOutput(inputOutput);
    }
}