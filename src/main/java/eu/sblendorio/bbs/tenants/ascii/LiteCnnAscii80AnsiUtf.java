package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.core.Utils;
import eu.sblendorio.bbs.tenants.mixed.LiteCommons;

@Hidden
public class LiteCnnAscii80AnsiUtf extends AsciiThread {

    static class LiteCnnCommons extends LiteCommons {
        public LiteCnnCommons(BbsThread bbs) {
            super(bbs);
            gap = 6;
        }

        public void drawLogo() throws Exception {
            bbs.cls();
            bbs.write(Utils.bytes(readBinaryFile("ansi/CnnNews.utf8ans"), "\033[0m"));
        }

    }


    protected LiteCommons liteCommons = new LiteCnnCommons(this);

    public LiteCnnAscii80AnsiUtf() {
    }


    @Override
    public void doLoop() throws Exception {
        liteCommons.doLoop();
    }

}