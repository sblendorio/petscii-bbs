package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import org.zmpp.textui.cli.BbsScreenModel;

public class ZorkMachineAscii extends AsciiThread {

    private final String filename;

    public ZorkMachineAscii() {
        this("zmpp/zork3.z3");
    }

    public ZorkMachineAscii(String filename) {
        super();
        this.filename = filename;
    }

    public void logo() throws Exception {
        cls();
        if (getScreenColumns() >= 40) {
            readTextFile("apple1/intro-zork.txt").forEach(this::println);
        }
        flush();

    }

    @Override
    public void doLoop() throws Exception {
        logo();
        resetInput();
        try {
            final byte[] story = readBinaryFile(filename);
            BbsScreenModel zorkMachine = new BbsScreenModel(story, this);
            zorkMachine.runTheGame();
        } catch (Exception ex) {
            log("Unexpected Exception", ex);
        }
    }
}
