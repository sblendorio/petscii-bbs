package eu.sblendorio.bbs.tenants.petscii;

import java.util.LinkedHashMap;

public class OneApuliaRetrocomputingPetscii extends OneRssPetscii {

    @Override
    protected void readSections() throws Exception {
        sections = new LinkedHashMap<>();
        sections.put("1", new NewsSection("", "https://www.apuliaretrocomputing.it/wordpress/?feed=rss2"));
        LOGO_SECTION = readBinaryFile("petscii/apulia-retrocomputing.seq");
    }
}
