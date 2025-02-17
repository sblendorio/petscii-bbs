package eu.sblendorio.bbs.tenants.minitel;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import eu.sblendorio.bbs.core.BbsInputOutput;
import eu.sblendorio.bbs.core.MinitelInputOutput;
import eu.sblendorio.bbs.tenants.ascii.OneRssAscii;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import static eu.sblendorio.bbs.core.Utils.bytes;

public class OneRssDigitantoMinitel extends OneRssAscii {
    @Override
    public BbsInputOutput buildIO(Socket socket) throws IOException { return new MinitelInputOutput(socket); }

    @Override
    public boolean resizeable() { return false; }

    @Override
    public void initBbs() throws Exception {
        super.initBbs();
        HR_TOP = StringUtils.repeat('`', getScreenColumns() - 1);
    }

    public OneRssDigitantoMinitel() {
        super();
        this.pageSize = 10;
        this.showAuthor = true;
        this.newlineAfterDate = false;
        this.twoColumns = false;
        this.gap = 5;
    }

    protected void readSections() throws Exception {
        this.sections = new LinkedHashMap<>();
        this.sections.put("", new NewsSection(null, "https://www.digitanto.it/feed_content.php"));
        this.LOGO_MENU = this.LOGO_SECTION =
                bytes(readBinaryFile("minitel/digitanto.vdt"),30,10, 0x1b, 0x3a, 0x69, 0x43, 17);
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
