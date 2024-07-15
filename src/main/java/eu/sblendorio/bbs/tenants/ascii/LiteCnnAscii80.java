package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.tenants.mixed.LiteCommons;

@Hidden
public class LiteCnnAscii80 extends AsciiThread {

    static class LiteCustom extends LiteCommons {
        public LiteCustom(BbsThread bbs) { super(bbs); }
    }


    protected LiteCommons liteCommons = new LiteCustom(this);

    public LiteCnnAscii80() {
    }


    @Override
    public void doLoop() throws Exception {
        liteCommons.doLoop();
    }

}