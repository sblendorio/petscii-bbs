package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.Hidden;

import java.util.LinkedHashMap;

// wired.com is no longer on WordPress (wp-json answers 404): sections come from the official RSS feeds
// (https://www.wired.com/about/rss-feeds/), whose items only carry a summary
@Hidden
public class WiredCom extends OneRssPetscii {

    private static final String FEED = "https://www.wired.com/feed/";

    // regular stories and galleries, and "feature" pages with a custom layout
    private static final String ARTICLE_PARAGRAPHS = "[data-testid=BodyWrapper] p, div.text-rich-text p";

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
        this.screenRows = 19;
        this.pageSize = 7;
        this.showAuthor = true;
        this.newlineAfterDate = false;
        this.offsetX = 20;
        this.offsetY = 1;
        this.twoColumns = true;
        this.LOGO_MENU = this.LOGO_SECTION = LOGO_BYTES;
    }

    private static final byte[] LOGO_BYTES = new byte[] {
        18, 5, -84, 32, -84, -110, -95, -84, -94, 32, 18, -84, -94, 32, -110, -84,
        -94, -69, 18, -84, -94, 32, -110, 13, -95, -95, -95, -95, 32, -95, 32, -95,
        18, -94, -66, -95, -110, -94, 32, -95, 18, 32, -95, -110, 32, -101, 32, 32,
        32, 13, 18, 5, 32, -95, -95, -110, -95, -84, 18, -68, -110, 32, -95, 18,
        32, -95, -95, -110, -94, -69, -95, 18, -94, -66, -110, 13, 18, -94, -94, -94,
        -110, -66, 32, 32, 32, 18, -94, -94, -94, -110, 32, 32, 32, 18, -94, -94,
        -94, -110, 13
    };

}
