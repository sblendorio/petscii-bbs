package eu.sblendorio.bbs.tenants.petscii;

import java.util.LinkedHashMap;

public class OneTexasInstrumentItaliaPetscii extends OneRssPetscii {

    @Override
    protected void readSections() throws Exception {
        sections = new LinkedHashMap<>();
        sections.put("1", new NewsSection("TI Museum page", "https://www.facele.eu/web-history?format=feed&type=rss"));
        sections.put("2", new NewsSection("TI Museum: ODV", "https://www.facele.eu/odv?format=feed&type=rss"));
        LOGO_MENU = LOGO_SECTION = readBinaryFile("petscii/ti.seq");
        offsetX = -1;
    }
}
