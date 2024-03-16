package eu.sblendorio.bbs.tenants.prestel;

import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.HtmlUtils;
import eu.sblendorio.bbs.core.PrestelThread;
import eu.sblendorio.bbs.games.AvventuraNelCastelloBridge;

public class AvventuraNelCastelloPrestel extends PrestelThread {

    AvventuraNelCastelloBridge bridge;
    String locale;

    public AvventuraNelCastelloPrestel(String locale) {
        this.locale = locale;
    }

    class Bridge extends AvventuraNelCastelloBridge {
        public Bridge(BbsThread bbs) {
            super(bbs);
        }
        @Override public String transformDiacritics(String s) { return HtmlUtils.utilHtmlDiacriticsToAscii(s);}
    }

    @Override
    public void doLoop() throws Exception {
        bridge = new Bridge(this);
        bridge.init(locale);
        bridge.start();
    }

}
