package eu.sblendorio.bbs.tenants.prestel;

import eu.sblendorio.bbs.core.*;
import eu.sblendorio.bbs.tenants.mixed.LiteCommons;

@Hidden
public class LiteCnnPrestel extends PrestelThread {

    static class LiteCnnCommons extends LiteCommons {
        public LiteCnnCommons(BbsThread bbs) {
            super(bbs);
            this.pageSize = 6;
            this.gap = 6;
        }
        public char hrChar() { return '-'; }
        public void drawLogo() throws Exception {
            bbs.cls();
            bbs.write(Utils.bytes(readBinaryFile("prestel/cnn.cept3")));
        }
    }

    protected LiteCommons liteCommons = new LiteCnnCommons(this);

    public LiteCnnPrestel() {
    }

    @Override
    public void doLoop() throws Exception {
        liteCommons.doLoop();
    }

}