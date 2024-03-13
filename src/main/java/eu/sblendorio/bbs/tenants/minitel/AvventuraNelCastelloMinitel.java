package eu.sblendorio.bbs.tenants.minitel;

import eu.sblendorio.bbs.core.*;
import eu.sblendorio.bbs.games.AvventuraNelCastelloBridge;

import static eu.sblendorio.bbs.core.MinitelControls.*;

public class AvventuraNelCastelloMinitel extends MinitelThread {

    AvventuraNelCastelloBridge bridge;

    byte[] splashScreen;
    String locale;

    public AvventuraNelCastelloMinitel(byte[] splashScreen, String locale) {
        this.splashScreen = splashScreen;
        this.locale = locale;
    }

    class Bridge extends AvventuraNelCastelloBridge {
        public Bridge(BbsThread bbs) {
            super(bbs);
        }

        @Override
        public void pressAnyKey() throws Exception {
            flush(); bbs.resetInput();
            bbs.readKey();
            bbs.write(bbs.backspace()); bbs.write(bbs.backspace());
            bbs.newline();
        }

        @Override public boolean showOriginalBanner() { return false; }
        @Override public String transformDiacritics(String s) { return HtmlUtils.utilHtmlDiacriticsToAscii(s);}
        @Override public void revOn() { attributes(REV_ON); }
        @Override public void revOff() { attributes(REV_OFF); }
    }


    @Override
    public void doLoop() throws Exception {
        write(SCROLL_OFF);
        write(CURSOR_OFF);
        cls();
        write(splashScreen);
        flush(); resetInput();
        keyPressed(30000);
        resetInput();
        write(SCROLL_ON);
        write(CURSOR_ON);

        gotoXY(0, 24);
        newline();
        newline();
        newline();
        attributes(CHAR_WHITE);

        bridge = new Bridge(this);
        bridge.init(locale);
        bridge.start();
    }

}
