package eu.sblendorio.bbs.tenants.ascii;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;

public class OneTexasInstrumentsItaliaAscii extends OneRssAscii {
    protected void readSections() throws Exception {
        sections = new LinkedHashMap<>();
        sections.put("1", new OneRssAscii.NewsSection("www.facele.eu - web history", "https://www.facele.eu/web-history?format=feed&type=rss"));
        sections.put("2", new OneRssAscii.NewsSection("TI Watch Museum - OdV", "https://www.facele.eu/odv?format=feed&type=rss"));
        LOGO_MENU = LOGO_SECTION = "".getBytes(StandardCharsets.ISO_8859_1);
        twoColumns = false;
    }

}
