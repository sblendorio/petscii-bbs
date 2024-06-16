package eu.sblendorio.bbs.tenants.minitel;

import eu.sblendorio.bbs.core.BbsInputOutput;
import eu.sblendorio.bbs.core.MinitelInputOutput;
import eu.sblendorio.bbs.tenants.ascii.OneRssAscii;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedHashMap;

import static eu.sblendorio.bbs.core.Utils.bytes;

public class OneRssReady64Minitel extends OneRssAscii {
    @Override
    public BbsInputOutput buildIO(Socket socket) throws IOException { return new MinitelInputOutput(socket); }

    @Override
    public boolean resizeable() { return false; }

    @Override
    public void initBbs() throws Exception {
        super.initBbs();
        HR_TOP = StringUtils.repeat('`', getScreenColumns() - 1);
    }

    public OneRssReady64Minitel() {
        super();
        this.pageSize = 10;
        this.showAuthor = true;
        this.newlineAfterDate = false;
        this.twoColumns = false;
        this.gap = 5;
    }

    protected void readSections() throws Exception {
        this.sections = new LinkedHashMap<>();
        this.sections.put("", new NewsSection(null, "https://ready64.org/rss/rss.php"));
        this.LOGO_MENU = this.LOGO_SECTION =
                bytes(readBinaryFile("minitel/ready64.vdt"),30,10, 0x1b, 0x3a, 0x69, 0x43, 17);
    }

}
