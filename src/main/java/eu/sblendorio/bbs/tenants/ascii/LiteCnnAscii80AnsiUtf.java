package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.core.Utils;
import eu.sblendorio.bbs.tenants.mixed.LiteCommons;

@Hidden
public class LiteCnnAscii80AnsiUtf extends LiteAscii {

    static class LiteCustom extends LiteCommons {
        public LiteCustom(BbsThread bbs) {
            super(bbs);
            gap = 6;
        }

        public void drawLogo() throws Exception {
            bbs.cls();
            bbs.write(Utils.bytes(readBinaryFile("ansi/CnnNews.utf8ans"), "\033[0m"));
        }

    }

    public LiteCnnAscii80AnsiUtf() {
        liteCommons = new LiteCustom(this);
    }


    @Override
    public void doLoop() throws Exception {
        liteCommons.doLoop();
    }

}