package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.BbsInputOutput;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;

public class OneRss2600Ascii extends OneRssAscii {

    private BbsInputOutput inputOutput = null;

    public OneRss2600Ascii() {
        super();
        this.pageSize = 10;
        this.showAuthor = true;
        this.newlineAfterDate = false;
        this.twoColumns = false;
    }

    protected void readSections() throws Exception {
        this.sections = new LinkedHashMap<>();
        this.sections.put("", new NewsSection(null, "https://2600.com/rss.xml"));
        this.LOGO_MENU = "2600 News".getBytes(StandardCharsets.ISO_8859_1);
        this.LOGO_SECTION = "2600 News".getBytes(StandardCharsets.ISO_8859_1);
    }

    public OneRss2600Ascii(BbsInputOutput x) {
        this();
        inputOutput = x;
    }

    @Override
    public void initBbs() throws Exception {
        super.initBbs();
        if (inputOutput != null) setBbsInputOutput(inputOutput);
    }
}
