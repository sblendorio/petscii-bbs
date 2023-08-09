package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.BbsInputOutput;
import eu.sblendorio.bbs.core.Hidden;
import java.nio.charset.StandardCharsets;

@Hidden
public class DisinformaticoAscii extends GoogleBloggerProxyAscii {

    private BbsInputOutput inputOutput;

    public DisinformaticoAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.blogUrl = "https://attivissimo.blogspot.com";
        this.pageSize = 7;
    }

    private static final byte[] LOGO_BYTES = "Il disinformatico".getBytes(StandardCharsets.ISO_8859_1);

    public DisinformaticoAscii(BbsInputOutput x) {
        this();
        this.inputOutput = x;
    }

    @Override
    public void initBbs() throws Exception {
        super.initBbs();
        if (inputOutput != null) setBbsInputOutput(inputOutput);
    }
}
