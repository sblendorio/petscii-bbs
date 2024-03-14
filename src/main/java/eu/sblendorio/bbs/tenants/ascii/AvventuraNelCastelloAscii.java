package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.games.AvventuraNelCastelloBridge;

public class AvventuraNelCastelloAscii extends AsciiThread {

    AvventuraNelCastelloBridge bridge;

    class Bridge extends AvventuraNelCastelloBridge {
        public Bridge(BbsThread bbs) {
            super(bbs);
        }
    }
    @Override
    public void doLoop() throws Exception {
        bridge = new Bridge(this);
        bridge.init("it-it");
        bridge.start();
    }

}
