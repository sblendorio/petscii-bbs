package eu.sblendorio.bbs.tenants.ascii;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import eu.sblendorio.bbs.core.BbsInputOutput;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class OneRssDigitantoAscii extends OneRssAscii {

    private BbsInputOutput inputOutput = null;

    public OneRssDigitantoAscii() {
        super();
        this.pageSize = 10;
        this.showAuthor = true;
        this.newlineAfterDate = false;
        this.twoColumns = false;
    }

    protected void readSections() throws Exception {
        this.sections = new LinkedHashMap<>();
        this.sections.put("", new NewsSection(null, "https://www.digitanto.it/feed_content.php"));
        this.LOGO_MENU = "www.digiTANTO.it".getBytes(StandardCharsets.ISO_8859_1);
        this.LOGO_SECTION = "www.digiTANTO.it".getBytes(StandardCharsets.ISO_8859_1);
    }

    public OneRssDigitantoAscii(BbsInputOutput x) {
        this();
        inputOutput = x;
    }

    @Override
    public void initBbs() throws Exception {
        super.initBbs();
        if (inputOutput != null) setBbsInputOutput(inputOutput);
    }

    @Override
    public String postProcess(String s) {
        return s
            .replaceAll("<rss version=\"2.0\">", "<rss version=\"2.0\" xmlns:content=\"http://purl.org/rss/1.0/modules/content/\">")
            .replaceAll("<content>", "<content:encoded>")
            .replaceAll("</content>", "</content:encoded>")
        ;
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

}
