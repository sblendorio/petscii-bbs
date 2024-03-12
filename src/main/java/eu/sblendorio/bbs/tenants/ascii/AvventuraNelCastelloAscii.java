package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.games.AvventuraNelCastelloBridge;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.SimpleBindings;

import static java.nio.charset.StandardCharsets.UTF_8;

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
        bridge.init("it");
        bridge.start();
    }

}
