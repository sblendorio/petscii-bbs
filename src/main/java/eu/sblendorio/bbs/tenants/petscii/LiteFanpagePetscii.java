package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.*;
import eu.sblendorio.bbs.tenants.mixed.LiteCommons;
import eu.sblendorio.bbs.tenants.mixed.LiteFanpageCommons;

@Hidden
public class LiteFanpagePetscii extends PetsciiThread {
    protected LiteCommons liteCommons = new LiteCustom(this);

    static class LiteCustom extends LiteFanpageCommons {
        public LiteCustom(BbsThread bbs) {
            super(bbs);
            pageSize = 5;
            gap = 6;
        }
        public char hrChar() { return 163; }
        public void highlight(boolean on) { bbs.write(on ? PetsciiColors.WHITE : PetsciiColors.GREY3); }
        public void drawLogo() throws Exception {
            bbs.cls();
            bbs.write(Utils.bytes(readBinaryFile("petscii/fanpage.seq"), PetsciiColors.GREY3));
        }
        public void printArticleStatusLine(int page) { printArticleStatusLinePetscii(bbs, page); }
        public void printListStatusLine() { printListStatusLinePetscii(bbs); }

    }

    public LiteFanpagePetscii() {
    }

    @Override
    public void doLoop() throws Exception {
        liteCommons.doLoop();
    }

}