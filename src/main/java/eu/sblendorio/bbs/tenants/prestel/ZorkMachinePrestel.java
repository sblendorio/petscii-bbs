package eu.sblendorio.bbs.tenants.prestel;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.PrestelThread;
import org.zmpp.textui.VirtualConsole;
import org.zmpp.textui.bbs.BBSMachineFactory;

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
        if (getScreenColumns() >= 40) {
            readTextFile("prestel/intro-zork.txt").forEach(this::println);
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
            ex.printStackTrace();
        }
    }
}
