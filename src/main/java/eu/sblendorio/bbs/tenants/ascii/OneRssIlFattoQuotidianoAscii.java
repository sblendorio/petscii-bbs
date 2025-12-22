package eu.sblendorio.bbs.tenants.ascii;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import eu.sblendorio.bbs.core.BbsInputOutput;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class OneRssIlFattoQuotidianoAscii extends OneRssAscii {

    private BbsInputOutput inputOutput = null;

    public OneRssIlFattoQuotidianoAscii() {
        super();
        this.pageSize = 4;
        this.showAuthor = true;
        this.newlineAfterDate = false;
        this.twoColumns = false;
    }

    protected void readSections() throws Exception {
        this.sections = new LinkedHashMap<>();
        this.sections.put("", new NewsSection(null, "https://www.ilfattoquotidiano.it/feed/"));
        this.LOGO_MENU = "Il Fatto Quotidiano".getBytes(StandardCharsets.ISO_8859_1);
        this.LOGO_SECTION = "Il Fatto Quotidiano".getBytes(StandardCharsets.ISO_8859_1);
    }

    public OneRssIlFattoQuotidianoAscii(BbsInputOutput x) {
        this();
        inputOutput = x;
    }

    @Override
    public void initBbs() throws Exception {
        super.initBbs();
        if (inputOutput != null) setBbsInputOutput(inputOutput);
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
