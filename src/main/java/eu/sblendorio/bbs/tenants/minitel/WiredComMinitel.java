package eu.sblendorio.bbs.tenants.minitel;

import eu.sblendorio.bbs.core.BbsInputOutput;
import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.core.MinitelInputOutput;
import eu.sblendorio.bbs.tenants.ascii.OneRssAscii;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedHashMap;

import static eu.sblendorio.bbs.core.Utils.bytes;

// wired.com is no longer on WordPress (wp-json answers 404): sections come from the official RSS feeds
// (https://www.wired.com/about/rss-feeds/), whose items only carry a summary
@Hidden
public class WiredComMinitel extends OneRssAscii {
    private static final String FEED = "https://www.wired.com/feed/";

    // regular stories and galleries, and "feature" pages with a custom layout
    private static final String ARTICLE_PARAGRAPHS = "[data-testid=BodyWrapper] p, div.text-rich-text p";

    @Override
    public BbsInputOutput buildIO(Socket socket) throws IOException { return new MinitelInputOutput(socket); }

    @Override
    public boolean resizeable() { return false; }

    @Override
    public void initBbs() throws Exception {
        super.initBbs();
        HR_TOP = StringUtils.repeat('`', getScreenColumns() - 1);
    }

    public WiredComMinitel() {
        super();
        this.pageSize = 7;
        this.showAuthor = true;
        this.newlineAfterDate = false;
        this.twoColumns = true;
        // banner on rows 1-3, section title on row 4: two rows more than the banner alone
        this.gap = 7;
        this.titleSeparator = "";
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
        final byte[] banner = readBinaryFile("minitel/wired.vdt");
        // menu: list from row 4; sections: cursor on row 4 for the title, below the 3-row banner
        this.LOGO_MENU = bytes(banner, 30, 10, 0x1b, 0x3a, 0x69, 0x43, 17);
        this.LOGO_SECTION = bytes(banner, 30, 10, 10, 10, 0x1b, 0x3a, 0x69, 0x43, 17);
    }

}
