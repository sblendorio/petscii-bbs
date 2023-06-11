package eu.sblendorio.bbs.tenants.ascii;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import eu.sblendorio.bbs.tenants.petscii.OneRssPetscii;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class OneRssPoliticoAscii extends OneRssAscii {

    public OneRssPoliticoAscii() {
        this.pageSize = 8;
        this.showAuthor = true;
        this.newlineAfterDate = false;
        this.twoColumns = false;
    }

    protected void readSections() throws Exception {
        this.sections = new LinkedHashMap<>();
        this.sections.put("", new NewsSection(null, "https://www.politico.com/rss/politicopicks.xml"));
        this.LOGO_MENU = "Politico".getBytes(StandardCharsets.ISO_8859_1);
        this.LOGO_SECTION = "Politico".getBytes(StandardCharsets.ISO_8859_1);
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
