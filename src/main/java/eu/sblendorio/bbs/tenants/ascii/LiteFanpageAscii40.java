package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.tenants.mixed.LiteCommons;
import eu.sblendorio.bbs.tenants.mixed.LiteFanpageCommons;

@Hidden
public class LiteFanpageAscii40 extends AsciiThread {

    static class LiteCustom extends LiteFanpageCommons {
        public LiteCustom(BbsThread bbs) {
            super(bbs);
            pageSize = 5;
        }
    }

    protected LiteCommons liteCommons = new LiteCustom(this);

    public LiteFanpageAscii40() {
        super();
        this.screenColumns = 40;
    }

    @Override
    public void doLoop() throws Exception {
        liteCommons.doLoop();
    }

}