package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import org.zmpp.textui.BbsScreenModel;

import java.util.Map;

public class ZorkMachineAscii extends AsciiThread {

    private final String nameOfTheGame;
    private final String filename;
    private Runnable boldOn;
    private Runnable boldOff;

    private Map<String, Runnable> overrides = null;

    public ZorkMachineAscii() {
        this("ExampleZork", "zmpp/zork3.z3");
    }

    public ZorkMachineAscii(String nameOfTheGame, String filename) {
        this(nameOfTheGame, filename, null, null);
    }

    public ZorkMachineAscii(String nameOfTheGame, String filename, Runnable boldOn, Runnable boldOff) {
        this(nameOfTheGame, filename, boldOn, boldOff, null);
    }

    public ZorkMachineAscii(String nameOfTheGame, String filename, Runnable boldOn, Runnable boldOff, Map<String, Runnable> overrides) {
        super();
        this.nameOfTheGame = nameOfTheGame;
        this.filename = filename;
        this.boldOn = boldOn;
        this.boldOff = boldOff;
        this.overrides = overrides;
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
        log("Zork Machine started. Filename=" + this.filename);
        logo();
        resetInput();
        try {
            final byte[] story = readBinaryFile(filename);
            BbsScreenModel zorkMachine = new BbsScreenModel(nameOfTheGame, story, this, 0, boldOn, boldOff, null, overrides);
            zorkMachine.runTheGame();
        } catch (Exception ex) {
            log("Unexpected Exception", ex);
        }
    }
}
