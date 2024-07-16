package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.tenants.mixed.LiteFanpageCommons;

@Hidden
public class LiteFanpageAscii80 extends LiteAscii {

    static class LiteCustom extends LiteFanpageCommons {
        public LiteCustom(BbsThread bbs) { super(bbs); }
    }

    public LiteFanpageAscii80() {
        liteCommons = new LiteFanpageAscii40.LiteCustom(this);
    }

    @Override
    public void doLoop() throws Exception {
        liteCommons.doLoop();
    }

}