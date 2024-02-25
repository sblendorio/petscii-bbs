package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.core.PetsciiColors;
import eu.sblendorio.bbs.core.PetsciiKeys;
import eu.sblendorio.bbs.core.PetsciiThread;
import org.apache.commons.lang3.StringUtils;
import org.zmpp.textui.BbsScreenModel;

import static eu.sblendorio.bbs.core.PetsciiColors.GREY3;
import static eu.sblendorio.bbs.core.PetsciiColors.WHITE;

@Hidden
public class ZorkMachine extends PetsciiThread {

    private final String filename;
    private final byte[] logo;

    public ZorkMachine() {
        this.filename = "zmpp/zork1.z3";
        this.logo = new byte[] {};
    }

    public ZorkMachine(String filename) {
        this.filename = filename;
        this.logo = new byte[] {};
    }

    public ZorkMachine(String filename, byte[] logo) {
        this.filename = filename;
        this.logo = logo;
    }

    @Override
    public void doLoop() throws Exception {
        log("Zork Machine started. Filename=" + this.filename);
        write(PetsciiKeys.CLR, PetsciiKeys.LOWERCASE, PetsciiKeys.CASE_LOCK, PetsciiKeys.HOME);
        write(PetsciiColors.GREEN);

        write(logo);
        if (logo.length > 0) {
            flush(); resetInput();
            keyPressed(30000);
            resetInput();
            newline();
            newline();
        }

        write(GREY3);
        write(PetsciiColors.LIGHT_GREEN); println("Based on ZMPP by Wei-ju Wu");
        write(WHITE); println("BBS version (2020) by:");
        println(StringUtils.repeat(chr(163), 22));
        write(WHITE); print("- "); write(PetsciiColors.CYAN); println("Francesco Sblendorio");
        write(WHITE); print("- "); write(PetsciiColors.CYAN); println("Roberto Manicardi");
        println();
        write(PetsciiColors.CYAN); print("Use: "); write(PetsciiColors.GREY2); print("QUIT");     write(PetsciiColors.CYAN); println(" to exit");
        write(PetsciiColors.CYAN); print("     "); write(PetsciiColors.GREY2); print("SAVE");     write(PetsciiColors.CYAN); println(" to store game");
        write(PetsciiColors.CYAN); print("     "); write(PetsciiColors.GREY2); print("RESTORE");  write(PetsciiColors.CYAN); println(" to resume game");
        println();
        write(GREY3);
        try {
            final byte[] story = readBinaryFile(filename);
            BbsScreenModel zorkMachine = new BbsScreenModel(story, this, 9, ()->write(WHITE), ()->write(GREY3));
            zorkMachine.runTheGame();
        } catch (Exception ex) {
            if (!"Exit from ZMPP game".equalsIgnoreCase(ex.getMessage())) {
                log("Forced exit from zork machine (" + filename + ")");
                throw ex;
            }
            log("Exiting zork machine (" + filename + ")");
        }
    }
}
