package eu.sblendorio.bbs.tenants.petscii;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class OneRssPoliticoPetscii extends OneRssPetscii {

    protected void readSections() throws Exception {
        this.sections = new LinkedHashMap<>();
        this.sections.put("", new NewsSection(null, "https://www.politico.com/rss/politicopicks.xml"));
        this.screenRows = 19;
        this.pageSize = 7;
        this.showAuthor = true;
        this.newlineAfterDate = false;
        this.LOGO_MENU = this.LOGO_SECTION = readBinaryFile("petscii/politico.seq");
        this.CHROME_AGENT = "curl/8.6.0";
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
