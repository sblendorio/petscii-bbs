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

public class OneRssIlFattoQuotidianoMinitel extends OneRssAscii {
    @Override
    public BbsInputOutput buildIO(Socket socket) throws IOException { return new MinitelInputOutput(socket); }

    @Override
    public boolean resizeable() { return false; }

    @Override
    public void initBbs() throws Exception {
        super.initBbs();
        HR_TOP = StringUtils.repeat('`', getScreenColumns() - 1);
    }

    public OneRssIlFattoQuotidianoMinitel() {
        super();
        this.pageSize = 4;
        this.showAuthor = true;
        this.newlineAfterDate = false;
        this.twoColumns = false;
        this.gap = 5;
    }

    protected void readSections() throws Exception {
        this.sections = new LinkedHashMap<>();
        this.sections.put("", new NewsSection(null, "https://www.ilfattoquotidiano.it/feed/"));
        this.LOGO_MENU = this.LOGO_SECTION =
                bytes(readBinaryFile("minitel/fq.vdt"),30,10, 0x1b, 0x3a, 0x69, 0x43, 17);
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
