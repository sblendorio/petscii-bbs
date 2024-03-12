package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.*;
import eu.sblendorio.bbs.games.AvventuraNelCastelloBridge;

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
        @Override public boolean showOriginalBanner() { return false; }
        @Override public String transformDiacritics(String s) { return HtmlUtils.utilHtmlDiacriticsToAscii(s);}
        @Override public void revOn() { write(PetsciiKeys.REVON); }
        @Override public void revOff() { write(PetsciiKeys.REVOFF); }
    }


    @Override
    public void doLoop() throws Exception {
        write(PetsciiKeys.CLR, PetsciiKeys.LOWERCASE, PetsciiKeys.CASE_LOCK, PetsciiKeys.HOME);
        write(splashScreen);
        write(PetsciiColors.GREY3);
        flush(); resetInput();
        keyPressed(30000);
        resetInput();
        newline();
        newline();
        newline();
        write(PetsciiColors.GREY3);

        bridge = new Bridge(this);
        bridge.init(locale);
        bridge.start();
    }

}
