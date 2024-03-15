package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import org.zmpp.textui.BbsScreenModel;

public class ZorkMachineAscii extends AsciiThread {

    private final String nameOfTheGame;
    private final String filename;
    private Runnable boldOn;
    private Runnable boldOff;

    public ZorkMachineAscii() {
        this("ExampleZork", "zmpp/zork3.z3");
    }

    public ZorkMachineAscii(String nameOfTheGame, String filename) {
        this(nameOfTheGame, filename, null, null);
    }

    public ZorkMachineAscii(String nameOfTheGame, String filename, Runnable boldOn, Runnable boldOff) {
        super();
        this.nameOfTheGame = nameOfTheGame;
        this.filename = filename;
        this.boldOn = boldOn;
        this.boldOff = boldOff;
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
            BbsScreenModel zorkMachine = new BbsScreenModel(nameOfTheGame, story, this, 0, boldOn, boldOff);
            zorkMachine.runTheGame();
        } catch (Exception ex) {
            log("Unexpected Exception", ex);
        }
    }
}
