package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.BbsInputOutput;

import javax.swing.text.View;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;

public class OneTexasInstrumentsItaliaAscii extends OneRssAscii {

    private BbsInputOutput inputOutput = null;

    public OneTexasInstrumentsItaliaAscii() {
        super();
    }

    public OneTexasInstrumentsItaliaAscii(BbsInputOutput x) {
        this();
        this.inputOutput = x;
    }
    protected void readSections() throws Exception {
        sections = new LinkedHashMap<>();
        sections.put("1", new OneRssAscii.NewsSection("www.facele.eu - web history", "https://www.facele.eu/web-history?format=feed&type=rss"));
        sections.put("2", new OneRssAscii.NewsSection("TI Watch Museum - OdV", "https://www.facele.eu/index.php/odv/odv-1?format=feed&type=rss"));
        sections.put("3", new OneRssAscii.NewsSection("TI Logo", new ViewFile("ascii/ti.txt")));
        LOGO_MENU = LOGO_SECTION = "".getBytes(StandardCharsets.ISO_8859_1);
        twoColumns = false;
    }

    @Override
    public void initBbs() throws Exception {
        super.initBbs();
        if (inputOutput != null) setBbsInputOutput(inputOutput);
    }
}
