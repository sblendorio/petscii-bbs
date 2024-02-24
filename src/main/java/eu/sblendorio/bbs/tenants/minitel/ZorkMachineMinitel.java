package eu.sblendorio.bbs.tenants.minitel;

import eu.sblendorio.bbs.core.MinitelControls;
import eu.sblendorio.bbs.core.MinitelThread;

public class ZorkMachineMinitel extends MinitelThread {

    private final String filename;
    private byte[] logo = null;

    public ZorkMachineMinitel() {
        this("zmpp/zork3.z3");
    }

    public ZorkMachineMinitel(String filename, byte[] logo) {
        super();
        this.filename = filename;
        this.logo = logo;
    }

    public ZorkMachineMinitel(String filename) {
        this(filename, null);
    }

    public void logo() throws Exception {
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
        resetInput();/* SBLEND
        try {
            final byte[] story = readBinaryFile(filename);
            BBSMachineFactory factory;
            factory = new BBSMachineFactory(story, this, 8);
            factory.buildMachine();
            VirtualConsole console = factory.getUI();
            console.runTheGame();
        } catch (Exception ex) {
            log("Unexpected Exception", ex);
        } */
    }
}
