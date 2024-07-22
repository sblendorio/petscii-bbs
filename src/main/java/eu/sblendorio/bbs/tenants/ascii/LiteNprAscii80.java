package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.tenants.mixed.LiteNprCommons;

@Hidden
public class LiteNprAscii80 extends LiteAscii {

    static class LiteCustom extends LiteNprCommons {
        public LiteCustom(BbsThread bbs) {
            super(bbs);
            pageSize = 10;
        }
    }

    public LiteNprAscii80() {
        liteCommons = new LiteCustom(this);
    }

    @Override
    public void doLoop() throws Exception {
        liteCommons.doLoop();
    }

}