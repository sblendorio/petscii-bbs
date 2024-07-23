package eu.sblendorio.bbs.tenants.minitel;

import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.core.MinitelThread;
import eu.sblendorio.bbs.core.Utils;
import eu.sblendorio.bbs.tenants.mixed.LiteCommons;
import eu.sblendorio.bbs.tenants.mixed.LiteFanpageCommons;

@Hidden
public class LiteFanpageMinitel extends MinitelThread {

    static class LiteCnnCommons extends LiteFanpageCommons {
        public LiteCnnCommons(BbsThread bbs) {
            super(bbs);
            this.pageSize = 5;
            this.gap = 5;
        }
        public char hrChar() { return '`'; }
        public void drawLogo() throws Exception {
            bbs.cls();
            bbs.write(Utils.bytes(readBinaryFile("minitel/fanpage2.vdt")));
        }
    }

    protected LiteCommons liteCommons = new LiteCnnCommons(this);

    public LiteFanpageMinitel() {
    }


    @Override
    public void doLoop() throws Exception {
        liteCommons.doLoop();
    }

}