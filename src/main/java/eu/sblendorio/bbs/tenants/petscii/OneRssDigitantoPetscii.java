package eu.sblendorio.bbs.tenants.petscii;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class OneRssDigitantoPetscii extends OneRssPetscii {

    @Override
    protected void readSections() throws Exception {
        sections = new LinkedHashMap<>();
        sections.put("1", new NewsSection("", "https://www.digitanto.it/feed_content.php"));
        newlineAfterDate = false;
        LOGO_SECTION = readBinaryFile("petscii/digitanto.seq");
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

        return e.getDescription().getValue() + " " +
                e.getContents().stream()
                        .map(SyndContent::getValue)
                        .map(StringUtils::defaultString)
                        .collect(Collectors.joining("<br>"));
    }

}
