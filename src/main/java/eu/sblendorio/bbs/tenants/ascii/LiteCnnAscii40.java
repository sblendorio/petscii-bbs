package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.tenants.mixed.LiteCommons;

@Hidden
public class LiteCnnAscii40 extends AsciiThread {

    static class LiteCnnCommons extends LiteCommons {
        public LiteCnnCommons(BbsThread bbs) {
            super(bbs);
            this.pageSize = 6;
        }
    }


    public LiteCommons liteCommons = new LiteCnnCommons(this);

    public LiteCnnAscii40() {
        this.screenColumns = 40;
    }


    @Override
    public void doLoop() throws Exception {
        liteCommons.doLoop();
    }

}