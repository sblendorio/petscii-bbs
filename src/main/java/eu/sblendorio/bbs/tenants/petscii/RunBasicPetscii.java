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
    private BbsThread bbsThread = null;


    public RunBasicPetscii() {
        source = "basic/startrek-40-2.bas";
    }

    public RunBasicPetscii(String source) {
        this.source = source;
        this.bbsThread = this;
    }

    public RunBasicPetscii(String source, BbsThread bbsThread) {
        this.source = source;
        this.bbsThread = bbsThread;
    }

    @Override
    public void doLoop() throws Exception {
        logger.info("Running BASIC Program: '{}', on '{}'", source, bbsThread.getClass().getSimpleName());
        bbsThread.cls();
        SwBasicBridge bridge = new SwBasicBridge(bbsThread);
        bridge.init(source);
        bridge.start();
    }
}
