package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.BbsInputOutput;
import eu.sblendorio.bbs.core.Hidden;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;

// wired.com is no longer on WordPress (wp-json answers 404): sections come from the official RSS feeds
// (https://www.wired.com/about/rss-feeds/), whose items only carry a summary
@Hidden
public class WiredComAscii extends OneRssAscii {
    private static final String FEED = "https://www.wired.com/feed/";

    // regular stories and galleries, and "feature" pages with a custom layout
    private static final String ARTICLE_PARAGRAPHS = "[data-testid=BodyWrapper] p, div.text-rich-text p";

    private BbsInputOutput inputOutput = null;

    public WiredComAscii() {
        super();
        this.showAuthor = true;
        this.newlineAfterDate = false;
        // one column: the menu must also fit the 22 columns of the VIC-20
        this.twoColumns = false;
    }

    public WiredComAscii(BbsInputOutput inputOutput) {
        this();
        this.inputOutput = inputOutput;
    }

    @Override
    public void initBbs() throws Exception {
        super.initBbs();
        if (inputOutput != null) setBbsInputOutput(inputOutput);
    }

    @Override
    protected void readSections() throws Exception {
        this.sections = new LinkedHashMap<>();
        this.sections.put("1", new NewsSection("Latest", FEED + "rss"));
        this.sections.put("2", new NewsSection("Business", FEED + "category/business/latest/rss"));
        this.sections.put("3", new NewsSection("Culture", FEED + "category/culture/latest/rss"));
        this.sections.put("4", new NewsSection("Gear", FEED + "category/gear/latest/rss"));
        this.sections.put("5", new NewsSection("Science", FEED + "category/science/latest/rss"));
        this.sections.put("6", new NewsSection("Security", FEED + "category/security/latest/rss"));
        this.sections.put("7", new NewsSection("Backchannel", FEED + "category/backchannel/latest/rss"));
        this.sections.put("8", new NewsSection("AI", FEED + "tag/ai/latest/rss"));
        this.sections.put("9", new NewsSection("Guides", FEED + "tag/wired-guide/latest/rss"));
        this.fullTextSelector = ARTICLE_PARAGRAPHS;
        // titles wrap a lot on narrow screens
        this.pageSize = getScreenColumns() < 40 ? 4 : 7;
        this.LOGO_MENU = LOGO_BYTES;
        this.LOGO_SECTION = LOGO_BYTES;
    }

    private static final byte[] LOGO_BYTES = "Wired".getBytes(StandardCharsets.ISO_8859_1);

}
