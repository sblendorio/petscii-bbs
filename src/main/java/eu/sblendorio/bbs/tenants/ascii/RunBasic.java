package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.games.SwBasicBridge;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

public class RunBasic extends AsciiThread {

    private static Logger logger = LogManager.getLogger(RunBasic.class);

    private String source = null;
    private BbsThread bbsThread = null;


    public RunBasic() {
        source = "basic/sample.bas";
        this.bbsThread = this;
    }

    public RunBasic(String source) {
        this.source = source;
        this.bbsThread = this;
    }

    public RunBasic(String source, BbsThread bbsThread) {
        this.source = source;
        this.bbsThread = bbsThread;
    }

    @Override
    public byte[] initializingBytes() {
        return "\377\375\042\377\373\001".getBytes(ISO_8859_1);
    }

    @Override
    public void doLoop() throws Exception {
        print("src? "); flush(); resetInput();
        String src = readLine();
        if (StringUtils.isNotBlank(src)) this.source = "basic_cc/"+src+".bas";
        println("Running "+this.source);
        println("---------------------");
        newline();
        logger.info("Running BASIC Program: '{}', on '{}'", source, bbsThread.getClass().getSimpleName());
        bbsThread.cls();
        SwBasicBridge bridge = new SwBasicBridge(bbsThread);
        bridge.init(source);
        bridge.start();
    }
}
