package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.BbsInputOutput;
import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.tenants.mixed.HolidayCommons;

import java.nio.charset.StandardCharsets;

public class SanremoAscii extends GoogleBloggerProxyAscii {

    private BbsInputOutput inputOutput;

    public SanremoAscii() {
        super();
        this.logo = LOGO_BYTES;
        this.blogUrl = "https://sanremo-commenti.blogspot.com";
        this.pageSize = 6;
    }

    @Override
    public String disclaimer() {
        return "Retrocampus BBS Sanremo non è una testata giornalistica ma un progetto a termine che pubblica abstract di notizie senza alcuna cadenza continuativa nè lucro citando sempre la fonte.";
    }

    private static final byte[] LOGO_BYTES = ("Sanremo "+ HolidayCommons.currentYear() +" on BBS").getBytes(StandardCharsets.ISO_8859_1);

    public SanremoAscii(BbsInputOutput x) {
        this();
        this.inputOutput = x;
    }

    @Override
    public void initBbs() throws Exception {
        super.initBbs();
        if (inputOutput != null) setBbsInputOutput(inputOutput);
    }
}
