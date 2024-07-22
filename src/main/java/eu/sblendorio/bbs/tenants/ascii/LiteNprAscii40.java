package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.tenants.mixed.LiteNprCommons;

@Hidden
public class LiteNprAscii40 extends LiteAscii {

    static class LiteCustom extends LiteNprCommons {
        public LiteCustom(BbsThread bbs) {
            super(bbs);
            pageSize = 5;
        }
    }

    public LiteNprAscii40() {
        liteCommons = new LiteCustom(this);
        this.screenColumns = 40;
    }

    @Override
    public void doLoop() throws Exception {
        liteCommons.doLoop();
    }

}