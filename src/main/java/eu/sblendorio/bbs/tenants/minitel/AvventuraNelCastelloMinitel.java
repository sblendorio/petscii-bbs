package eu.sblendorio.bbs.tenants.minitel;

import eu.sblendorio.bbs.core.*;
import eu.sblendorio.bbs.games.AvventuraNelCastelloBridge;

import java.util.concurrent.ThreadLocalRandom;

import static eu.sblendorio.bbs.core.MinitelControls.*;
import static java.nio.charset.StandardCharsets.ISO_8859_1;

public class AvventuraNelCastelloMinitel extends MinitelThread {

    AvventuraNelCastelloBridge bridge;

    byte[] splashScreen;
    byte[] copyright;
    String locale;

    public AvventuraNelCastelloMinitel(byte[] splashScreen, byte[] copyright, String locale) {
        this.splashScreen = splashScreen;
        this.copyright = copyright;
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
        @Override public void revOn() { attributes(REV_ON); }
        @Override public void revOff() { attributes(REV_OFF); }

        @Override
        public void joke() throws Exception {
            for (int i=0; i < 1000; i++) {
                int x = ThreadLocalRandom.current().nextInt(0, 39);
                int y = ThreadLocalRandom.current().nextInt(0, 24);
                int ch = ThreadLocalRandom.current().nextInt(32, 127);
                gotoXY(x, y);
                write(ch);
                int probability = ThreadLocalRandom.current().nextInt(0, 100);
                if (probability < 5) beep();
            }
            Thread.sleep(3000L);
            // write("\033[24;1H".getBytes(ISO_8859_1));
            cls();
        }

    }

    public void printText(byte[] bytes) {
        for (byte b : bytes)
            if (b != '\n') {
                write(b);
            } else {
                println();
            }
        flush();
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
        attributes(CHAR_WHITE);
        cls();
        printText(copyright);

        bridge = new Bridge(this);
        bridge.init(locale);
        bridge.start();
    }

}
