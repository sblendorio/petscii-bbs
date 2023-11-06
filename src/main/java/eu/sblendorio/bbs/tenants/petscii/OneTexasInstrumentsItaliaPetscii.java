package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.tenants.ascii.ViewFile;

import java.util.LinkedHashMap;

public class OneTexasInstrumentsItaliaPetscii extends OneRssPetscii {

    @Override
    protected void readSections() throws Exception {
        sections = new LinkedHashMap<>();
        sections.put("1", new NewsSection("www.facele.eu - web history", "https://www.facele.eu/web-history?format=feed&type=rss"));
        sections.put("2", new NewsSection("TI Watch Museum - OdV", "https://www.facele.eu/index.php/odv/odv?format=feed&type=rss"));
        sections.put("3", new NewsSection("TI Logo", new ViewFile("ascii/ti.txt", true)));
        LOGO_MENU = LOGO_SECTION = readBinaryFile("petscii/ti.seq");
        offsetX = -1;
        twoColumns = false;
    }
}
