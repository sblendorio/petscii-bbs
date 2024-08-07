package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.tenants.mixed.LiteCommons;

@Hidden
public class LiteCnnAscii40 extends LiteAscii {

    static class LiteCustom extends LiteCommons {
        public LiteCustom(BbsThread bbs) {
            super(bbs);
            this.pageSize = 6;
        }
    }

    public LiteCnnAscii40() {
        liteCommons = new LiteCustom(this);
        this.screenColumns = 40;
    }


    @Override
    public void doLoop() throws Exception {
        liteCommons.doLoop();
    }

}