package eu.sblendorio.bbs.tenants.prestel;

import eu.sblendorio.bbs.core.PrestelControls;
import eu.sblendorio.bbs.core.PrestelThread;
import org.zmpp.textui.cli.BbsScreenModel;

public class ZorkMachinePrestel extends PrestelThread {

    private final String filename;

    public ZorkMachinePrestel() {
        this("zmpp/zork3.z3");
    }

    public ZorkMachinePrestel(String filename) {
        super();
        this.filename = filename;
        this.autoConceal = false;
    }

    public void logo() throws Exception {
        cls();
        flush();

    }

    @Override
    public void doLoop() throws Exception {
        logo();
        resetInput();
        write(PrestelControls.CURSOR_ON);
        try {
            final byte[] story = readBinaryFile(filename);
            BbsScreenModel zorkMachine = new BbsScreenModel(story, this);
            zorkMachine.runTheGame();
        } catch (Exception ex) {
            log("Unexpected Exception", ex);
        }
    }
}
