package eu.sblendorio.bbs.tenants.petscii;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class OneRssIlFattoQuotidianoPetscii extends OneRssPetscii {

    public OneRssIlFattoQuotidianoPetscii() {
        this.pageSize = 4;
        this.showAuthor = true;
        this.newlineAfterDate = false;
        this.twoColumns = false;
    }

    @Override
    protected void readSections() throws Exception {
        sections = new LinkedHashMap<>();
        sections.put("1", new NewsSection("", "https://www.ilfattoquotidiano.it/feed/"));
        newlineAfterDate = false;
        LOGO_SECTION = new byte[] {
                28, -84, -69, 18, -69, -110, -95, -68, 18, 32, -94, -69, -110, -95, -94, -69,
                32, 18, -66, -110, 32, -84, -95, 13, -84, -69, 18, -95, -110, -95, 32, 18,
                32, -66, -110, 32, -84, -94, 18, 32, -110, -68, 18, 32, -94, -69, -84, -110,
                -66, 18, -66, -94, -68, -110, 32, 18, -101, -65, -110, -69, 18, -95, -95, -110,
                -84, -65, -68, 18, -84, -95, -95, -110, -65, 18, -95, -110, -84, -65, 18, -95,
                -110, -65, -84, -65, 13, 18, 28, -95, -110, -95, 18, -95, -110, -95, 32, 18,
                32, -110, -68, 32, 18, 32, -110, 32, 18, 32, -110, 32, 18, 32, -110, 32,
                18, -95, -110, -95, 32, 18, 32, -110, 32, 18, 32, -110, 32, -101, -95, -95,
                18, -95, -95, -95, -95, -110, 32, -95, 18, -95, -95, -95, -95, -95, -69, -95,
                -95, -95, -95, -110, 13, 18, 28, -94, -94, -94, -94, -110, -68, 18, -94, -110,
                -66, 32, -68, 18, -94, -110, -68, -66, -68, 18, -94, -110, 32, 18, -94, -110,
                -66, -68, 18, -94, -110, -66, 32, -101, -68, 18, -94, -110, 32, -66, 32, -66,
                32, -66, -68, -68, -66, -68, -68, -68, -68, -68, 32, -66, 13
        };
    }

    @Override
    public String getArticleBody(SyndEntry e) {
        return (e == null || e.getContents() == null)
                ? ""
                : e.getContents().stream()
                .map(SyndContent::getValue)
                .map(StringUtils::defaultString)
                .collect(Collectors.joining("<br>"));
    }

}
