package eu.sblendorio.bbs.tenants.minitel;

import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.core.MinitelThread;
import eu.sblendorio.bbs.core.Utils;
import eu.sblendorio.bbs.tenants.mixed.LiteCommons;
import eu.sblendorio.bbs.tenants.mixed.LiteNprCommons;

@Hidden
public class LiteNprMinitel extends MinitelThread {

    static class LiteCnnCommons extends LiteNprCommons {
        public LiteCnnCommons(BbsThread bbs) {
            super(bbs);
            this.pageSize = 5;
            this.gap = 5;
        }
        public char hrChar() { return '`'; }
        public void drawLogo() throws Exception {
            bbs.cls();
            bbs.write(Utils.bytes(readBinaryFile("minitel/npr.vdt"), 13, 10));
        }
    }

    protected LiteCommons liteCommons = new LiteCnnCommons(this);

    public LiteNprMinitel() {
    }


    @Override
    public void doLoop() throws Exception {
        liteCommons.doLoop();
    }

}