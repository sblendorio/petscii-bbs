package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.PetsciiColors;
import eu.sblendorio.bbs.core.PetsciiKeys;
import eu.sblendorio.bbs.core.PetsciiThread;
import eu.sblendorio.bbs.core.Utils;
import eu.sblendorio.bbs.tenants.mixed.EnigmaCommons;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import eu.sblendorio.bbs.tenants.mixed.EnigmaCommons.EnigmaStatus;
import org.apache.commons.lang3.StringUtils;

public class EnigmaPetscii extends PetsciiThread {

    private EnigmaStatus machine = new EnigmaStatus();

    public EnigmaPetscii() {
    }

    @Override
    public void doLoop() throws Exception {
        write(PetsciiColors.GREY3, PetsciiKeys.LOWERCASE, PetsciiKeys.CASE_LOCK);
        menu();

    }

    public void showConfig() {
        println("Reflector: " + machine.getReflector());
        println("Auto increment rotors: "+(machine.getAutoIncrementRotors() ? "Yes" : "No"));
        println("Number of wirings: "+machine.getWirings().size());
        int i=0;
        for (EnigmaCommons.Wiring w: machine.getWirings()) {
            i++;
            println("  "+i+". From " + w.fromLetter() + " to " + w.toLetter());
        }
        println("Number of rotors: "+machine.getRotors().size());
        i=0;
        for (EnigmaCommons.Rotor r: machine.getRotors()) {
            i++;
            println("  "+i+". Type "+r.type()+", Ring "+r.ring()+", Position "+r.position());
        }
    }

    public void menu() throws Exception {
        do {
            cls();
            println("Enigma machine");
            println("--------------");
            showConfig();
            println("1. Change reflector");
            println("2. Toggle auto increment rotors");
            println("3. Add wiring");
            println("4. Remove wiring");
            println("5. Encrypt/Decrypt a string");
            println(". - Back to main menu");
            flush(); resetInput();
            int ch = readKey();
            if (ch == '.') break;
            if (ch == '1') {
                print("Enter reflector: ");
                do {
                    String candidate = readLine();
                    candidate = candidate.trim().toUpperCase();
                    if (".".equals(candidate)) continue;
                    if (Set.of("UKW-A", "UKW-B", "UKW-C").contains(candidate)) {
                        machine.setReflector(candidate);
                        break;
                    }
                    print("(UKW-A, UKW-B or UKW-C): ");
                } while (true);
            }
            if (ch == '2') machine.setAutoIncrementRotors(!machine.getAutoIncrementRotors());
            if (ch == '3') {
                print("From: ");
                String from = readLine(1, Utils.setOfChars(Utils.STR_LETTER));
                if (StringUtils.isEmpty(from)) continue;
                print("To: ");
                String to = readLine(1, Utils.setOfChars(Utils.STR_LETTER));
                if (StringUtils.isEmpty(to)) continue;
                machine.getWirings().add(new EnigmaCommons.Wiring(from.toUpperCase(), to.toUpperCase()));
            }
            if (ch == '4' && !machine.getWirings().isEmpty()) {
                machine.getWirings().removeLast();
            }
        } while (true);
    }
}
