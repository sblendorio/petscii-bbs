package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.BbsInputOutput;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;

// AJ+ English no longer publishes stories (ajplus.net is now a showcase site without RSS): news come from the single
// official Al Jazeera English feed, whose items only carry a short summary
public class OneRssAlJazeeraAscii extends OneRssAscii {
    private static final String ARTICLE_PARAGRAPHS = ".wysiwyg--all-content p";

    private BbsInputOutput inputOutput = null;

    public OneRssAlJazeeraAscii() {
        super();
        this.pageSize = 7;
        this.showAuthor = false;
        this.newlineAfterDate = false;
        this.twoColumns = false;
    }

    protected void readSections() throws Exception {
        this.sections = new LinkedHashMap<>();
        this.sections.put("1", new NewsSection(null, "https://www.aljazeera.com/xml/rss/all.xml"));
        // the feed items only carry a summary: the full text is taken from the article page
        this.fullTextSelector = ARTICLE_PARAGRAPHS;
        this.LOGO_MENU = "Al Jazeera".getBytes(StandardCharsets.ISO_8859_1);
        this.LOGO_SECTION = "Al Jazeera".getBytes(StandardCharsets.ISO_8859_1);
    }

    public OneRssAlJazeeraAscii(BbsInputOutput x) {
        this();
        inputOutput = x;
    }

    @Override
    public void initBbs() throws Exception {
        super.initBbs();
        if (inputOutput != null) setBbsInputOutput(inputOutput);
    }
}
