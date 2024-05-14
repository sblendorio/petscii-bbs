package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.PetsciiThread;
import eu.sblendorio.bbs.tenants.mixed.SwBasicBridge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.TriConsumer;

public class RunBasicPetscii extends PetsciiThread {

    private static Logger logger = LogManager.getLogger(RunBasicPetscii.class);
    private TriConsumer<BbsThread, Integer, Integer> locateFunction;

    private String source = null;


    public RunBasicPetscii() {
        source = "basic/sample.bas";
        locateFunction = null;
    }

    public RunBasicPetscii(String source, TriConsumer<BbsThread, Integer, Integer> locateFunction) {
        this.source = source;
        this.locateFunction = locateFunction;
    }


    @Override
    public void doLoop() throws Exception {
        logger.info("Running BASIC Program: '{}'", source);
        this.cls();
        SwBasicBridge bridge = new SwBasicBridge(this, locateFunction);
        bridge.init(source);
        bridge.start();
    }
}
