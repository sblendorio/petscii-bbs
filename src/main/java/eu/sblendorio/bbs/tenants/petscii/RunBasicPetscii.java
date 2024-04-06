package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.PetsciiThread;
import eu.sblendorio.bbs.games.SwBasicBridge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RunBasicPetscii extends PetsciiThread {

    private static Logger logger = LogManager.getLogger(RunBasicPetscii.class);

    private String source = null;


    public RunBasicPetscii() {
        source = "basic/sample.bas";
    }

    public RunBasicPetscii(String source) {
        this.source = source;
    }


    @Override
    public void doLoop() throws Exception {
        logger.info("Running BASIC Program: '{}'", source);
        this.cls();
        SwBasicBridge bridge = new SwBasicBridge(this);
        bridge.init(source);
        bridge.start();
    }
}
