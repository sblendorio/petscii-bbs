package eu.sblendorio.bbs.tenants.ascii;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import eu.sblendorio.bbs.core.BbsInputOutput;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class OneRssReady64Ascii extends OneRssAscii {

    private BbsInputOutput inputOutput = null;

    public OneRssReady64Ascii() {
        super();
        this.pageSize = 10;
        this.showAuthor = true;
        this.newlineAfterDate = false;
        this.twoColumns = false;
    }

    protected void readSections() throws Exception {
        this.sections = new LinkedHashMap<>();
        this.sections.put("", new NewsSection(null, "https://ready64.org/rss/rss.php"));
        this.LOGO_MENU = "Ready 64".getBytes(StandardCharsets.ISO_8859_1);
        this.LOGO_SECTION = "Ready 64".getBytes(StandardCharsets.ISO_8859_1);
    }

    public OneRssReady64Ascii(BbsInputOutput x) {
        this();
        inputOutput = x;
    }

    @Override
    public void initBbs() throws Exception {
        super.initBbs();
        if (inputOutput != null) setBbsInputOutput(inputOutput);
    }
}
