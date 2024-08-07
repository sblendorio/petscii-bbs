package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.*;
import eu.sblendorio.bbs.tenants.mixed.LiteCommons;

@Hidden
public class LiteCnnPetscii extends PetsciiThread {
    protected LiteCommons liteCommons = new LiteCustom(this);

    static class LiteCustom
            extends LiteCommons {
        public LiteCustom(BbsThread bbs) {
            super(bbs);
            pageSize = 6;
            gap = 6;
        }
        public char hrChar() { return 163; }
        public void highlight(boolean on) { bbs.write(on ? PetsciiColors.WHITE : PetsciiColors.GREY3); }
        public void drawLogo() throws Exception {
            bbs.cls();
            bbs.write(Utils.bytes(readBinaryFile("petscii/cnn-news.seq"), PetsciiColors.GREY3));
        }
        public void printArticleStatusLine(int page) { printArticleStatusLinePetscii(bbs, page); }
        public void printListStatusLine() { printListStatusLinePetscii(bbs); }

    }

    public LiteCnnPetscii() {
    }

    @Override
    public void doLoop() throws Exception {
        liteCommons.doLoop();
    }

}