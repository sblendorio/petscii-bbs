package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.*;
import eu.sblendorio.bbs.games.AvventuraNelCastelloBridge;

import java.nio.charset.StandardCharsets;

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
        for (byte b : bytes)
            if (b != '\n')
                write(b);
            else
                println();
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

    }

    @Override
    public void doLoop() throws Exception {
        cls();
        printText(splashScreen);
        write("\033[m".getBytes(ISO_8859_1));
        flush(); resetInput();
        keyPressed(30000);
        resetInput();
        newline();
        newline();
        bridge = new Bridge(this);
        bridge.init(locale);
        bridge.start();
    }

}
