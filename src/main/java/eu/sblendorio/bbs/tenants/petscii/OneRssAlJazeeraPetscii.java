package eu.sblendorio.bbs.tenants.petscii;

import java.util.LinkedHashMap;

// AJ+ English no longer publishes stories (ajplus.net is now a showcase site without RSS): news come from the single
// official Al Jazeera English feed, whose items only carry a short summary
public class OneRssAlJazeeraPetscii extends OneRssPetscii {

    private static final String ARTICLE_PARAGRAPHS = ".wysiwyg--all-content p";

    protected void readSections() throws Exception {
        this.sections = new LinkedHashMap<>();
        this.sections.put("1", new NewsSection(null, "https://www.aljazeera.com/xml/rss/all.xml"));
        // the feed items only carry a summary: the full text is taken from the article page
        this.fullTextSelector = ARTICLE_PARAGRAPHS;
        this.screenRows = 19;
        this.pageSize = 7;
        this.showAuthor = false;
        this.newlineAfterDate = false;
        this.offsetX = 25;
        this.offsetY = 1;
        this.twoColumns = false;
        this.LOGO_MENU = this.LOGO_SECTION = readBinaryFile("petscii/aj.seq");
    }
}
