package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.core.PetsciiColors;
import eu.sblendorio.bbs.core.PetsciiKeys;
import eu.sblendorio.bbs.core.PetsciiThread;
import eu.sblendorio.bbs.tenants.ascii.ZorkMachineAscii;
import org.apache.commons.lang3.StringUtils;
import org.zmpp.textui.BbsScreenModel;

import java.util.Map;

import static eu.sblendorio.bbs.core.PetsciiColors.GREY3;
import static eu.sblendorio.bbs.core.PetsciiColors.WHITE;
import static eu.sblendorio.bbs.core.PetsciiKeys.DEL;

@Hidden
public class ZorkMachine extends PetsciiThread {

    private final String nameOfTheGame;
    private final String filename;
    private final byte[] logo;

    private Map<String, Runnable> overrides = null;

    public ZorkMachine() {
        this.nameOfTheGame = "ExampleZork";
        this.filename = "zmpp/zork1.z3";
        this.logo = new byte[] {};
    }

    public ZorkMachine(String nameOfTheGame, String filename) {
        this.nameOfTheGame = nameOfTheGame;
        this.filename = filename;
        this.logo = new byte[] {};
    }

    public ZorkMachine(String nameOfTheGame, String filename, byte[] logo) {
        this.nameOfTheGame = nameOfTheGame;
        this.filename = filename;
        this.logo = logo;
    }

    public ZorkMachine(String nameOfTheGame, String filename, byte[] logo, Map<String, Runnable> overrides) {
        this(nameOfTheGame, filename, logo);
        this.overrides = overrides;
    }
    @Override
    public void doLoop() throws Exception {
        log("Zork Machine started. Filename=" + this.filename);
        write(PetsciiKeys.CLR, PetsciiKeys.LOWERCASE, PetsciiKeys.CASE_LOCK, PetsciiKeys.HOME);
        write(PetsciiColors.GREEN);

        if (logo != null) {
            write(logo);
            if (logo.length > 0) {
                flush();
                resetInput();
                keyPressed(30000);
                resetInput();
                newline();
                newline();
            }
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
            BbsScreenModel zorkMachine = new BbsScreenModel(
                    nameOfTheGame,
                    story, this, 9,
                    () -> write(WHITE),
                    () -> write(GREY3),
                    () -> {
                        for (int i=0; i<39; i++) write(DEL);
                    },
                    overrides);
            zorkMachine.runTheGame();
        } catch (Exception ex) {
            log("Exiting zork machine (" + filename + ")", ex);
        }
    }
}
