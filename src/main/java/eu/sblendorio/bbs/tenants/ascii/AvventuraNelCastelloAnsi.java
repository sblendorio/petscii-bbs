package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.*;
import eu.sblendorio.bbs.games.AvventuraNelCastelloBridge;

import java.util.concurrent.ThreadLocalRandom;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

public class AvventuraNelCastelloAnsi extends AsciiThread {

    AvventuraNelCastelloBridge bridge;
    byte[] splashScreen;
    String locale;
    boolean utf8;

    public AvventuraNelCastelloAnsi(byte[] splashScreen, String locale, boolean utf8) {
        this.splashScreen = splashScreen;
        this.locale = locale;
        this.utf8 = utf8;
    }

    public void printText(byte[] bytes) {
        int col = 0;

        for (byte b : bytes)
            if (b != '\n') {
                write(b);
                col++;
            } else {
                for (int j=col; j<78; j++) write(' ');
                write("\033[m".getBytes(ISO_8859_1));
                println();
                write("\033[7m".getBytes(ISO_8859_1));
                col = 0;
            }
        if (col != 0) for (int j=col; j<78; j++) write(' ');
        write("\033[m".getBytes(ISO_8859_1));
        flush();
    }

    class Bridge extends AvventuraNelCastelloBridge {
        public Bridge(BbsThread bbs) {
            super(bbs);
        }
        @Override public boolean showOriginalBanner() { return false; }
        @Override public String transformDiacritics(String s) { return utf8 ? s : HtmlUtils.utilHtmlDiacriticsToAscii(s);}
        @Override public void revOn() { write("\033[7m".getBytes(ISO_8859_1)); }
        @Override public void revOff() { write("\033[m".getBytes(ISO_8859_1)); }

        @Override
        public void joke() throws Exception {
            for (int i=0; i < 1000; i++) {
                int x = ThreadLocalRandom.current().nextInt(0, 79) + 1;
                int y = ThreadLocalRandom.current().nextInt(0, 24) + 1;
                String pos = "\033[" + y + ";" + x + "H";
                int ch = ThreadLocalRandom.current().nextInt(32, utf8 ? 127 : 256);
                write(pos.getBytes(ISO_8859_1));
                write(ch);
                int probability = ThreadLocalRandom.current().nextInt(0, 100);
                if (probability < 5) beep();
            }
            Thread.sleep(3000L);
            // write("\033[24;1H".getBytes(ISO_8859_1));
            cls();
        }

    }

    @Override
    public void doLoop() throws Exception {
        cls();
        printText(splashScreen);
        write("\033[m".getBytes(ISO_8859_1));
        flush(); resetInput();
        keyPressed(30000);
        resetInput();
        cls();
        bridge = new Bridge(this);
        bridge.init(locale);
        bridge.start();
    }

}
