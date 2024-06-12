package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.BbsInputOutput;
import eu.sblendorio.bbs.core.Hidden;

import java.nio.charset.StandardCharsets;

@Hidden
public class CommessoPerplessoAscii extends WordpressProxyAscii {

    private BbsInputOutput inputOutput = null;

    public CommessoPerplessoAscii(BbsInputOutput x) {
        this();
        this.inputOutput = x;
    }

    @Override
    public void initBbs() throws Exception {
        super.initBbs();
        if (inputOutput != null) setBbsInputOutput(inputOutput);
    }

    public CommessoPerplessoAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.domain = "https://www.commessoperplesso.it";
        this.pageSize = 10;
    }


    private static final byte[] LOGO_BYTES = "Commesso Perplesso".getBytes(StandardCharsets.ISO_8859_1);

}
