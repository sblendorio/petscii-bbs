package eu.sblendorio.bbs.tenants.ascii;

import org.zmpp.textui.VirtualConsole;
import org.zmpp.textui.bbs.BBSMachineFactory;

import eu.sblendorio.bbs.core.AsciiThread;

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
            BBSMachineFactory factory;
            factory = new BBSMachineFactory(story, this);
            factory.buildMachine();
            VirtualConsole console = factory.getUI();
            console.runTheGame();
        } catch (Exception ex) {
            log("Unexpected Exception", ex);
        }
    }
}
