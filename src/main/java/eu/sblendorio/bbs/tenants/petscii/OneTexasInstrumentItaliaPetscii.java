package eu.sblendorio.bbs.tenants.petscii;

import java.util.LinkedHashMap;

public class OneTexasInstrumentItaliaPetscii extends OneRssPetscii {

    @Override
    protected void readSections() throws Exception {
        sections = new LinkedHashMap<>();
        sections.put("1", new NewsSection("www.facele.eu - web history", "https://www.facele.eu/web-history?format=feed&type=rss"));
        sections.put("2", new NewsSection("TI Watch Museum - OdV", "https://www.facele.eu/odv?format=feed&type=rss"));
        LOGO_MENU = LOGO_SECTION = readBinaryFile("petscii/ti.seq");
        offsetX = -1;
        twoColumns = false;
    }
}
