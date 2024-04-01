package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.games.SwBasicBridge;

public class RunBasicAscii extends AsciiThread {

    private String source = null;

    public RunBasicAscii() {
        source = "basic/startrek-40-1.bas";
    }

    public RunBasicAscii(String source) {
        this.source = source;
    }

    @Override
    public void doLoop() throws Exception {
        SwBasicBridge bridge = new SwBasicBridge(this);
        bridge.init(source);
        bridge.start();
    }
}
