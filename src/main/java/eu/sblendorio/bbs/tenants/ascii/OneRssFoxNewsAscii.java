package eu.sblendorio.bbs.tenants.ascii;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import eu.sblendorio.bbs.core.BbsInputOutput;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class OneRssFoxNewsAscii extends OneRssAscii {
    private BbsInputOutput inputOutput = null;

    public OneRssFoxNewsAscii() {
        super();
        this.pageSize = 5;
        this.showAuthor = true;
        this.newlineAfterDate = false;
        this.twoColumns = false;
    }

    protected void readSections() throws Exception {
        this.sections = new LinkedHashMap<>();
        this.sections.put("1", new NewsSection("General news", "https://moxie.foxnews.com/google-publisher/latest.xml"));
        this.sections.put("2", new NewsSection("World", "https://moxie.foxnews.com/google-publisher/world.xml"));
        this.sections.put("3", new NewsSection("Politics", "https://moxie.foxnews.com/google-publisher/politics.xml"));
        this.sections.put("4", new NewsSection("Science", " https://moxie.foxnews.com/google-publisher/science.xml"));
        this.sections.put("5", new NewsSection("Health", "https://moxie.foxnews.com/google-publisher/health.xml"));
        this.sections.put("6", new NewsSection("Sports", "https://moxie.foxnews.com/google-publisher/sports.xml"));
        this.sections.put("7", new NewsSection("Travel", "https://moxie.foxnews.com/google-publisher/travel.xml"));
        this.sections.put("8", new NewsSection("Tech", "https://moxie.foxnews.com/google-publisher/tech.xml"));
        this.sections.put("9", new NewsSection("Opinion", "https://moxie.foxnews.com/google-publisher/opinion.xml"));
        this.LOGO_MENU = "Fox News".getBytes(StandardCharsets.ISO_8859_1);
        this.LOGO_SECTION = "Fox News".getBytes(StandardCharsets.ISO_8859_1);
    }

    @Override
    public String getArticleBody(SyndEntry e) {
        if (e == null || e.getContents() == null)
            return "";
        return e.getContents().stream()
            .map(SyndContent::getValue)
            .map(StringUtils::defaultString)
            .collect(Collectors.joining("<br>"));
    }

    public OneRssFoxNewsAscii(BbsInputOutput x) {
        this();
        inputOutput = x;
    }

    @Override
    public void initBbs() throws Exception {
        super.initBbs();
        if (inputOutput != null) setBbsInputOutput(inputOutput);
    }

}
