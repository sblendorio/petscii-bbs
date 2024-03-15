package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.*;
import eu.sblendorio.bbs.games.AvventuraNelCastelloBridge;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static eu.sblendorio.bbs.core.PetsciiColors.GREY3;
import static eu.sblendorio.bbs.core.PetsciiKeys.DOWN;
import static eu.sblendorio.bbs.core.PetsciiKeys.HOME;

public class AvventuraNelCastelloPetscii extends PetsciiThread {

    AvventuraNelCastelloBridge bridge;

    byte[] splashScreen;
    String locale;

    public AvventuraNelCastelloPetscii(byte[] splashScreen, String locale) {
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
        @Override public void revOn() { write(PetsciiKeys.REVON); }
        @Override public void revOff() { write(PetsciiKeys.REVOFF); }

        @Override
        public void joke() throws Exception {
            write(HOME);
            int i = 0;
            while (i < 2000) {
                int ch = ThreadLocalRandom.current().nextInt(1, 256);
                int probability = ThreadLocalRandom.current().nextInt(0, 100);
                if (Set.of(144, 142, 147, 8, 9).contains(ch))
                    continue;
                write(ch);
                if (probability < 5) beep();
                i++;
            }
            write(HOME); write(GREY3);
            for (i = 0; i < 25; i++) write(DOWN);
            Thread.sleep(2000L);
        }

    }


    @Override
    public void doLoop() throws Exception {
        write(PetsciiKeys.CLR, PetsciiKeys.LOWERCASE, PetsciiKeys.CASE_LOCK, HOME);
        write(splashScreen);
        write(GREY3);
        flush(); resetInput();
        keyPressed(30000);
        resetInput();
        newline();
        newline();
        newline();
        write(GREY3);

        bridge = new Bridge(this);
        bridge.init(locale);
        bridge.start();
    }

}
