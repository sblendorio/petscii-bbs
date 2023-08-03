package eu.sblendorio.bbs.tenants.minitel;

import eu.sblendorio.bbs.core.MinitelThread;
import static eu.sblendorio.bbs.core.MinitelControls.*;

public class TestMinitel3 extends MinitelThread {
    @Override
    public void doLoop() throws Exception {
        cls();

        // gotoXY(0,2);
        attributes(TEXTSIZE_DOUBLE_ALL);
        print("GotoXY");
    }
}
