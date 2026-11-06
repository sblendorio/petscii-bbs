package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.BbsInputOutput;
import eu.sblendorio.bbs.tenants.mixed.ArticleTexts;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        this.LOGO_MENU = "Al Jazeera".getBytes(StandardCharsets.ISO_8859_1);
        this.LOGO_SECTION = "Al Jazeera".getBytes(StandardCharsets.ISO_8859_1);
    }

    // The full text is taken from the article page; items without text (e.g. videos) keep the RSS summary
    @Override
    public List<NewsFeed> getFeeds(String urlString) throws Exception {
        List<NewsFeed> feeds = super.getFeeds(urlString);
        Map<String, String> texts = ArticleTexts.fetchTexts(feeds.stream().map(feed -> feed.uri).toList(), ARTICLE_PARAGRAPHS, ArticleTexts.NO_LIMIT);
        return feeds.stream()
            .map(feed -> texts.containsKey(feed.uri)
                ? new NewsFeed(feed.publishedDate, feed.title, texts.get(feed.uri), feed.uri, feed.author)
                : feed)
            .toList();
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
