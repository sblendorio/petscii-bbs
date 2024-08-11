package eu.sblendorio.bbs.tenants.prestel;

import eu.sblendorio.bbs.core.PrestelControls;
import eu.sblendorio.bbs.core.PrestelThread;
import org.zmpp.textui.BbsScreenModel;

public class ZorkMachinePrestel extends PrestelThread {

    private final String nameOfTheGame;

    private final String filename;

    public ZorkMachinePrestel() {
        this("ExampleZork", "zmpp/zork3.z3");
    }

    public ZorkMachinePrestel(String nameOfTheGame, String filename) {
        super();
        this.nameOfTheGame = nameOfTheGame;
        this.filename = filename;
        this.autoConceal = false;
    }

    public void logo() throws Exception {
        cls();
        flush();

    }

    @Override
    public void doLoop() throws Exception {
        log("Zork Machine started. Filename=" + this.filename);
        logo();
        resetInput();
        write(PrestelControls.CURSOR_ON);
        try {
            final byte[] story = readBinaryFile(filename);
            BbsScreenModel zorkMachine = new BbsScreenModel(nameOfTheGame, story, this);
            zorkMachine.runTheGame();
        } catch (Exception ex) {
            log("Unexpected Exception", ex);
        }
    }
}
