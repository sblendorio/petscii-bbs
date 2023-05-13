package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.core.PetsciiColors;
import eu.sblendorio.bbs.core.PetsciiKeys;
import eu.sblendorio.bbs.core.PetsciiThread;

@Hidden
public class DovePetscii extends PetsciiThread {

    private static byte[] LOGO = readBinaryFile("petscii/dove.seq");

    @Override
    public void doLoop() throws Exception {
        cls();
        write(PetsciiKeys.LOWERCASE);
        write(LOGO);
        write(PetsciiColors.GREY3);
        gotoXY(0, 8);
        println("Inserisci il tuo nome");
        write(PetsciiColors.WHITE);
        flush(); resetInput();
        String name = readLine();
        write(PetsciiColors.GREY3);
        println();
        println();
        println("Indirizzo immobile:");
        write(PetsciiColors.WHITE);
        flush(); resetInput();
        String address = readLine();
        println();
        write(PetsciiColors.GREY3);
        println("Il tuo numero di telefono:");
        write(PetsciiColors.WHITE);
        flush(); resetInput();
        String phone = readLine();
        println();
        write(PetsciiColors.GREY3);
        println("Il tuo indirizzo email");
        write(PetsciiColors.WHITE);
        flush(); resetInput();
        String email = readLine();
        println();
        println();
        write(PetsciiColors.GREY3);
        print("Premi "); write(PetsciiColors.WHITE); print("SPAZIO"); write(PetsciiColors.GREY3); println(" per accettare termini del");
        println("servizio e l'informativa privacy");
        print("Premi "); write(PetsciiColors.WHITE); print("'.'"); write(PetsciiColors.GREY3); print(" per annullare.");
        readKey();

    }
}
