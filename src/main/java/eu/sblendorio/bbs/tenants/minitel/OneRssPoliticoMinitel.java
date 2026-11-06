package eu.sblendorio.bbs.tenants.minitel;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import eu.sblendorio.bbs.core.BbsInputOutput;
import eu.sblendorio.bbs.core.MinitelInputOutput;
import eu.sblendorio.bbs.tenants.ascii.OneRssAscii;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import static eu.sblendorio.bbs.core.Utils.bytes;

public class OneRssPoliticoMinitel extends OneRssAscii {
    @Override
    public BbsInputOutput buildIO(Socket socket) throws IOException { return new MinitelInputOutput(socket); }

    @Override
    public boolean resizeable() { return false; }

    @Override
    public void initBbs() throws Exception {
        super.initBbs();
        HR_TOP = StringUtils.repeat('`', getScreenColumns() - 1);
    }

    public OneRssPoliticoMinitel() {
        super();
        this.pageSize = 7;
        this.showAuthor = true;
        this.newlineAfterDate = false;
        this.twoColumns = true;
        // banner on rows 1-3, section title on row 4: two rows more than the banner alone
        this.gap = 7;
        this.titleSeparator = "";
    }

    @Override
    protected void readSections() throws Exception {
        this.sections = new LinkedHashMap<>();
        this.sections.put("1", new NewsSection("Politics", "https://rss.politico.com/politics-news.xml"));
        this.sections.put("2", new NewsSection("Congress", "https://rss.politico.com/congress.xml"));
        this.sections.put("3", new NewsSection("Magazine", "https://rss.politico.com/magazine.xml"));
        this.sections.put("4", new NewsSection("Defense", "https://rss.politico.com/defense.xml"));
        this.sections.put("5", new NewsSection("Energy & Env.", "https://rss.politico.com/energy.xml"));
        this.sections.put("6", new NewsSection("Health Care", "https://rss.politico.com/healthcare.xml"));
        this.sections.put("7", new NewsSection("Politico Pulse", "https://rss.politico.com/politicopulse.xml"));
        this.sections.put("8", new NewsSection("Morning Money", "https://rss.politico.com/morningmoney.xml"));
        this.sections.put("9", new NewsSection("Morning Trade", "https://rss.politico.com/morningtrade.xml"));
        this.sections.put("a", new NewsSection("Morning Tax", "https://rss.politico.com/morningtax.xml"));
        this.sections.put("b", new NewsSection("Cybersecurity", "https://rss.politico.com/morningcybersecurity.xml"));
        this.sections.put("c", new NewsSection("Morning Score", "https://rss.politico.com/morningscore.xml"));
        final byte[] banner = readBinaryFile("minitel/politico.vdt");
        // menu: list from row 4; sections: cursor on row 4 for the title, below the 3-row banner
        this.LOGO_MENU = bytes(banner, 30, 10, 0x1b, 0x3a, 0x69, 0x43, 17);
        this.LOGO_SECTION = bytes(banner, 30, 10, 10, 10, 0x1b, 0x3a, 0x69, 0x43, 17);
        this.CHROME_AGENT = "curl/8.6.0";
    }

    // Only some feeds carry the whole article (content:encoded): the others fall back to the RSS description
    @Override
    public String getArticleBody(SyndEntry e) {
        if (e == null)
            return "";
        String content = e.getContents() == null ? "" : e.getContents().stream()
                .map(SyndContent::getValue)
                .map(StringUtils::defaultString)
                .collect(Collectors.joining("<br>"));
        if (StringUtils.isNotBlank(content) || e.getDescription() == null)
            return content;
        return StringUtils.defaultString(e.getDescription().getValue());
    }

}
