package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.tenants.mixed.LiteCommons;

@Hidden
public class LiteCnnAscii80 extends LiteAscii {

    static class LiteCustom extends LiteCommons {
        public LiteCustom(BbsThread bbs) {
            super(bbs);
            this.pageSize = 10;
        }
    }

    public LiteCnnAscii80() {
        liteCommons = new LiteCustom(this);
    }

    @Override
    public void doLoop() throws Exception {
        liteCommons.doLoop();
    }

}