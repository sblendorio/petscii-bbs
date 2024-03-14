package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.HtmlUtils;
import eu.sblendorio.bbs.games.AvventuraNelCastelloBridge;

public class AvventuraNelCastelloAscii extends AsciiThread {

    AvventuraNelCastelloBridge bridge;
    String locale;

    public AvventuraNelCastelloAscii(String locale) {
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
