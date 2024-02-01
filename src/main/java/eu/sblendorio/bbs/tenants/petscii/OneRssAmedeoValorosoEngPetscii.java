package eu.sblendorio.bbs.tenants.petscii;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class OneRssAmedeoValorosoEngPetscii extends OneRssPetscii {

    @Override
    protected void readSections() throws Exception {
        sections = new LinkedHashMap<>();
        sections.put("1", new NewsSection("", "https://www.valoroso.it/en/feed/"));
        newlineAfterDate = false;
        LOGO_SECTION = readBinaryFile("petscii/valoroso.seq");
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
