package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.*;
import eu.sblendorio.bbs.tenants.mixed.LiteCommons;

@Hidden
public class LiteCnnPetscii extends PetsciiThread {

    static class LiteCnnCommons extends LiteCommons {
        public LiteCnnCommons(BbsThread bbs) {
            super(bbs);
            pageSize = 7;
            gap = 3;
        }
        public char hrChar() { return 163; }
        public void drawLogo() throws Exception {
            bbs.cls();
            bbs.write(Utils.bytes(readBinaryFile("petscii/cnn.seq"), PetsciiColors.GREY3));
        }
    }

    protected LiteCommons liteCommons = new LiteCnnCommons(this);

    public LiteCnnPetscii() {
    }


    @Override
    public void doLoop() throws Exception {
        liteCommons.doLoop();
    }

}