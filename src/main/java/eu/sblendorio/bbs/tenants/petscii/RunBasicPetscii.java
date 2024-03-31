package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.PetsciiThread;
import eu.sblendorio.bbs.games.SwBasicBridge;

public class RunBasicPetscii extends PetsciiThread {

    private String source = null;

    public RunBasicPetscii() {
        source = "basic/sample.bas";
    }

    public RunBasicPetscii(String source) {
        this.source = source;
    }

    @Override
    public void doLoop() throws Exception {
        SwBasicBridge bridge = new SwBasicBridge(this);
        bridge.init(source);
        bridge.start();
    }
}
