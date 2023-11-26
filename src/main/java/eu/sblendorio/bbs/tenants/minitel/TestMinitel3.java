package eu.sblendorio.bbs.tenants.minitel;

import eu.sblendorio.bbs.core.BlockGraphicsMinitel;
import eu.sblendorio.bbs.core.MinitelThread;
import eu.sblendorio.bbs.tenants.mixed.WikipediaCommons;

import static eu.sblendorio.bbs.core.MinitelControls.*;

public class TestMinitel3 extends MinitelThread {
    @Override
    public void doLoop() throws Exception {
        cls();
        write(GRAPHICS_MODE);
        write(BlockGraphicsMinitel.getRenderedMidres(0, WikipediaCommons.WIKILOGO, false, true));
    }
}
