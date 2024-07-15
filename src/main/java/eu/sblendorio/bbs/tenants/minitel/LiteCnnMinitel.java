package eu.sblendorio.bbs.tenants.minitel;

import eu.sblendorio.bbs.core.*;
import eu.sblendorio.bbs.tenants.mixed.LiteCommons;

@Hidden
public class LiteCnnMinitel extends MinitelThread {

    static class LiteCnnCommons extends LiteCommons {
        public LiteCnnCommons(BbsThread bbs) {
            super(bbs);
            this.pageSize = 6;
            this.gap = 5;
        }
        public char hrChar() { return '`'; }
        public void drawLogo() throws Exception {
            bbs.cls();
            bbs.write(Utils.bytes(readBinaryFile("minitel/cnn.vdt")));
        }
    }

    protected LiteCommons liteCommons = new LiteCnnCommons(this);

    public LiteCnnMinitel() {
    }


    @Override
    public void doLoop() throws Exception {
        liteCommons.doLoop();
    }

}