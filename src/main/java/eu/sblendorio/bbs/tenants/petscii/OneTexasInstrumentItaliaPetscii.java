package eu.sblendorio.bbs.tenants.petscii;

import java.util.LinkedHashMap;

public class OneTexasInstrumentItaliaPetscii extends OneRssPetscii {

    @Override
    protected void readSections() throws Exception {
        sections = new LinkedHashMap<>();
        sections.put("1", new NewsSection("", "http://www.facele.eu/web-history?format=feed&type=rss"));
        LOGO_SECTION = readBinaryFile("petscii/ti.seq");
    }
}
