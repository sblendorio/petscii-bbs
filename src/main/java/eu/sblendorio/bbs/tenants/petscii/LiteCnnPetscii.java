package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.*;
import eu.sblendorio.bbs.tenants.mixed.LiteCommons;

import static eu.sblendorio.bbs.core.PetsciiColors.GREY3;
import static eu.sblendorio.bbs.core.PetsciiColors.WHITE;

@Hidden
public class LiteCnnPetscii extends PetsciiThread {
    protected LiteCommons liteCommons = new LiteCnnCommons(this);

    static class LiteCnnCommons extends LiteCommons {
        public LiteCnnCommons(BbsThread bbs) {
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
        public void printArticleStatusLine(int page) {
            bbs.write(WHITE); bbs.print("-PAGE " + page + "-  SPACE=NEXT  -=PREV  .=EXIT"); bbs.write(GREY3);
        }
        public void printListStatusLine() {
            bbs.write(WHITE);bbs.print("#"); bbs.write(GREY3);
            bbs.print(" [");
            bbs.write(WHITE); bbs.print("N+-"); bbs.write(GREY3);
            bbs.print("]Page [");
            bbs.write(WHITE); bbs.print("H"); bbs.write(GREY3);
            bbs.print("]elp [");
            bbs.write(WHITE); bbs.print("R"); bbs.write(GREY3);
            bbs.print("]eload [");
            bbs.write(WHITE); bbs.print("."); bbs.write(GREY3);
            bbs.print("]");
            bbs.write(WHITE); bbs.print("Q"); bbs.write(GREY3);
            bbs.print("uit> ");
        }

    }

    public LiteCnnPetscii() {
    }

    @Override
    public void doLoop() throws Exception {
        liteCommons.doLoop();
    }

}