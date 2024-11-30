package eu.sblendorio.bbs.tenants.minitel;

import eu.sblendorio.bbs.core.MinitelControls;
import eu.sblendorio.bbs.core.MinitelThread;
import org.zmpp.textui.BbsScreenModel;

import java.util.Map;

public class ZorkMachineMinitel extends MinitelThread {

    private final String nameOfTheGame;
    private final String filename;
    private byte[] logo = null;
    private Runnable boldOn = null;
    private Runnable boldOff = null;

    private Map<String, Runnable> overrides = null;

    public ZorkMachineMinitel() {
        this("ExampleZork", "zmpp/zork3.z3");
    }

    public ZorkMachineMinitel(String nameOfTheGame, String filename, byte[] logo) {
        this(nameOfTheGame, filename, logo, null, null);
    }
    public ZorkMachineMinitel(String nameOfTheGame, String filename, byte[] logo, Runnable boldOn, Runnable boldOff) {
        this(nameOfTheGame, filename, logo, boldOn, boldOff, null);
    }
    public ZorkMachineMinitel(String nameOfTheGame, String filename, byte[] logo, Runnable boldOn, Runnable boldOff, Map<String, Runnable> overrides) {
        super();
        this.nameOfTheGame = nameOfTheGame;
        this.filename = filename;
        this.logo = logo;
        this.boldOn = boldOn;
        this.boldOff = boldOff;
        this.overrides = overrides;
    }

    public ZorkMachineMinitel(String nameOfTheGame, String filename) {
        this(nameOfTheGame, filename, null);
    }

    public void logo() throws Exception {
        log("Zork Machine started. Filename=" + this.filename);
        cls();
        if (logo == null) {
            readTextFile("minitel/intro-zork.txt").forEach(this::println);
        } else {
            write(MinitelControls.SCROLL_OFF);
            write(logo);
            write(MinitelControls.TEXT_MODE);
            write(MinitelControls.SCROLL_ON);
            gotoXY(39,23);
            flush(); resetInput();
            keyPressed(70_000);
            write(MinitelControls.CURSOR_ON);
            write(MinitelControls.CHAR_WHITE);
            write(MinitelControls.BACKGROUND_BLACK);
            cls();
        }
        println("BASED ON ZMPP PROJECT BY *WEI-JU WU*");
        println("");
        println("PORTED BY:");
        println("- FRANCESCO SBLENDORIO");
        println("- ROBERTO MANICARDI");
        println("");
        println("USE: QUIT to exit");
        println("SAVE to store match");
        println("RESTORE to resume match");
        println("");

        flush();

    }

    @Override
    public void doLoop() throws Exception {
        logo();
        resetInput();
        try {
            final byte[] story = readBinaryFile(filename);
            BbsScreenModel zorkMachine = new BbsScreenModel(
                    nameOfTheGame, story, this, 8, boldOn, boldOff, this::clearLineAndCr, overrides
            );
            zorkMachine.runTheGame();
        } catch (Exception ex) {
            log("Unexpected Exception", ex);
        }
    }
}
