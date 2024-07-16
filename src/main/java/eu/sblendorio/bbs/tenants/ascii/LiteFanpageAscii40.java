package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.tenants.mixed.LiteFanpageCommons;

@Hidden
public class LiteFanpageAscii40 extends LiteAscii {

    static class LiteCustom extends LiteFanpageCommons {
        public LiteCustom(BbsThread bbs) {
            super(bbs);
            pageSize = 5;
        }
    }

    public LiteFanpageAscii40() {
        liteCommons = new LiteCustom(this);
        this.screenColumns = 40;
    }

    @Override
    public void doLoop() throws Exception {
        liteCommons.doLoop();
    }

}