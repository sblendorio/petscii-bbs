package eu.sblendorio.bbs.tenants.petscii;

import java.util.LinkedHashMap;

public class OneRss2600Petscii extends OneRssPetscii {

    @Override
    protected void readSections() throws Exception {
        sections = new LinkedHashMap<>();
        sections.put("1", new NewsSection("", "https://2600.com/rss.xml"));
        newlineAfterDate = false;
        LOGO_SECTION = readBinaryFile("petscii/news2600.seq");
    }
}
