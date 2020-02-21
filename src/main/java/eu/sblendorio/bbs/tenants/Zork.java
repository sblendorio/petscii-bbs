package eu.sblendorio.bbs.tenants;

import org.zmpp.textbased.VirtualConsole;
import org.zmpp.textbased.bbs.BBSMachineFactory;

import eu.sblendorio.bbs.core.Colors;
import eu.sblendorio.bbs.core.Keys;
import eu.sblendorio.bbs.core.PetsciiThread;

public class Zork extends PetsciiThread {

    private final String filename;

    public Zork() {
        this.filename = "zmpp/minizork.z3";
    }

    public Zork(String filename) {
        this.filename = filename;
    }

    @Override
    public void doLoop() throws Exception {
        write(Keys.CLR, Keys.LOWERCASE, Keys.CASE_LOCK, Keys.HOME, Colors.GREY3);

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
