package eu.sblendorio.bbs.tenants.ascii;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import eu.sblendorio.bbs.core.BbsInputOutput;
import eu.sblendorio.bbs.tenants.petscii.OneRssPetscii;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class OneRssAmedeoValorosoEngAscii extends OneRssAscii {

    private BbsInputOutput inputOutput = null;

    public OneRssAmedeoValorosoEngAscii() {
        super();
        this.pageSize = 8;
        this.showAuthor = true;
        this.newlineAfterDate = false;
        this.twoColumns = false;
    }

    protected void readSections() throws Exception {
        sections = new LinkedHashMap<>();
        sections.put("1", new OneRssAscii.NewsSection("", "https://www.valoroso.it/en/feed/"));
        newlineAfterDate = false;

        this.LOGO_MENU = "Amedeo Valoroso - ENG".getBytes(StandardCharsets.ISO_8859_1);
        this.LOGO_SECTION = "Amedeo Valoroso - ENG".getBytes(StandardCharsets.ISO_8859_1);
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

    public OneRssAmedeoValorosoEngAscii(BbsInputOutput x) {
        this();
        inputOutput = x;
    }

    @Override
    public void initBbs() throws Exception {
        super.initBbs();
        if (inputOutput != null) setBbsInputOutput(inputOutput);
    }
}
