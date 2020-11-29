package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.PetsciiColors;
import eu.sblendorio.bbs.core.PetsciiKeys;
import eu.sblendorio.bbs.core.PetsciiThread;
import org.apache.commons.lang3.StringUtils;
import org.zmpp.textui.VirtualConsole;
import org.zmpp.textui.bbs.BBSMachineFactory;

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
        write(PetsciiKeys.CLR, PetsciiKeys.LOWERCASE, PetsciiKeys.CASE_LOCK, PetsciiKeys.HOME, PetsciiColors.GREY3);
        write(PetsciiColors.LIGHT_GREEN); println("Based on ZMPP by Wei-ju Wu");
        write(PetsciiColors.WHITE); println("BBS version (2020) by:");
        println(StringUtils.repeat(chr(163), 22));
        write(PetsciiColors.WHITE); print("- "); write(PetsciiColors.CYAN); println("Francesco Sblendorio");
        write(PetsciiColors.WHITE); print("- "); write(PetsciiColors.CYAN); println("Roberto Manicardi");
        println();
        write(PetsciiColors.CYAN); print("Use: "); write(PetsciiColors.GREY2); print("QUIT");     write(PetsciiColors.CYAN); println(" to exit");
        write(PetsciiColors.CYAN); print("     "); write(PetsciiColors.GREY2); print("SAVE");     write(PetsciiColors.CYAN); println(" to store game");
        write(PetsciiColors.CYAN); print("     "); write(PetsciiColors.GREY2); print("RESTORE");  write(PetsciiColors.CYAN); println(" to resume game");
        println();
        write(PetsciiColors.GREY3);

        try {
            final byte[] story = readBinaryFile(filename);
            BBSMachineFactory factory = new BBSMachineFactory(story, this);
            factory.buildMachine();
            VirtualConsole console = factory.getUI();
            console.runTheGame();
        } catch (Exception ex) {
            if (!"Exit from ZMPP game".equalsIgnoreCase(ex.getMessage())) {
                log("Forced exit from zork machine (" + filename + ")");
                throw ex;
            }
            log("Exiting zork machine (" + filename + ")");
        }
    }
}
