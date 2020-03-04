package eu.sblendorio.bbs.tenants;

import org.apache.commons.lang3.StringUtils;
import org.zmpp.textui.VirtualConsole;
import org.zmpp.textui.bbs.BBSMachineFactory;

import eu.sblendorio.bbs.core.Colors;
import eu.sblendorio.bbs.core.Keys;
import eu.sblendorio.bbs.core.PetsciiThread;

public class ZorkMachine extends PetsciiThread {

    private final String filename;

    public ZorkMachine() {
        this.filename = "zmpp/zork1.z3";
    }

    public ZorkMachine(String filename) {
        this.filename = filename;
    }

    @Override
    public void doLoop() throws Exception {
        log("Zork Machine started. Filename=" + this.filename);
        write(Keys.CLR, Keys.LOWERCASE, Keys.CASE_LOCK, Keys.HOME, Colors.GREY3);
        write(Colors.LIGHT_GREEN); println("Based on ZMPP by Wei-ju Wu");
        write(Colors.WHITE); println("BBS version (2020) by:");
        println(StringUtils.repeat(chr(163), 22));
        write(Colors.WHITE); print("- "); write(Colors.CYAN); println("Francesco Sblendorio");
        write(Colors.WHITE); print("- "); write(Colors.CYAN); println("Roberto Manicardi");
        println();
        write(Colors.CYAN); print("Use: "); write(Colors.GREY2); print("QUIT");     write(Colors.CYAN); println(" to exit");
        write(Colors.CYAN); print("     "); write(Colors.GREY2); print("SAVE");     write(Colors.CYAN); println(" to store game");
        write(Colors.CYAN); print("     "); write(Colors.GREY2); print("RESTORE");  write(Colors.CYAN); println(" to resume game");
        println();
        write(Colors.GREY3);

        try {
            final byte[] story = readBinaryFile(filename);
            BBSMachineFactory factory;
            factory = new BBSMachineFactory(story, this);
            factory.buildMachine();
            VirtualConsole console = factory.getUI();
            console.runTheGame();
        } catch (Exception ex) {
            if (!"Exit from ZMPP game".equalsIgnoreCase(ex.getMessage())) {
                ex.printStackTrace();
            }
            log("Exiting zork machine (" + filename + ")");
        }
    }
}
