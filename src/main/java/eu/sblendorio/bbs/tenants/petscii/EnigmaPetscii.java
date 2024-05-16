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
import org.apache.commons.lang3.math.NumberUtils;

import static eu.sblendorio.bbs.core.Utils.STR_LETTER_UPPER;
import static eu.sblendorio.bbs.core.Utils.setOfChars;

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
        i=0;
        for (EnigmaCommons.Rotor r: machine.getRotors()) {
            i++;
            println("Rotor#"+i+": Type "+r.type()+", Ring "+r.ring()+", Position "+r.position());
        }
    }

    public void menu() throws Exception {
        do {
            cls();
            println("Enigma machine");
            println("--------------");
            showConfig();
            println();
            println("1. Change rotor 1");
            println("2. Change rotor 2");
            println("3. Change rotor 3");
            println("4. Change reflector");
            println("5. Toggle auto increment rotors");
            println("6. Add wiring");
            println("7. Remove wiring");
            println("8. Encrypt/Decrypt a string");
            println(". - Back to main menu");
            println();
            flush(); resetInput();
            int ch = readKey();
            if (ch == '.') break;
            if (ch == '1') {
                println("Change rotor 1.");
                EnigmaCommons.Rotor rotor0 = askRotor();
                machine.setRotors(List.of(
                        rotor0,
                        machine.getRotors().get(1),
                        machine.getRotors().get(2)
                ));
            }
            if (ch == '2') {
                println("Change rotor 2.");
                EnigmaCommons.Rotor rotor1 = askRotor();
                machine.setRotors(List.of(
                        machine.getRotors().get(0),
                        rotor1,
                        machine.getRotors().get(2)
                ));
            }
            if (ch == '3') {
                println("Change rotor 3.");
                EnigmaCommons.Rotor rotor2 = askRotor();
                machine.setRotors(List.of(
                        machine.getRotors().get(0),
                        machine.getRotors().get(1),
                        rotor2
                ));
            }
            if (ch == '4') {
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
            if (ch == '5') machine.setAutoIncrementRotors(!machine.getAutoIncrementRotors());
            if (ch == '6') {
                print("From: ");
                String from = readLine(1, setOfChars(Utils.STR_LETTER));
                if (StringUtils.isEmpty(from)) continue;
                print("To: ");
                String to = readLine(1, setOfChars(Utils.STR_LETTER));
                if (StringUtils.isEmpty(to)) continue;
                machine.getWirings().add(new EnigmaCommons.Wiring(from.toUpperCase(), to.toUpperCase()));
            }
            if (ch == '7' && !machine.getWirings().isEmpty()) {
                machine.getWirings().removeLast();
            }
            if (ch == '8') {
                print("Message: ");
                flush(); resetInput();
                String msg = readLine(setOfChars(STR_LETTER_UPPER));
                machine.setOriginalMessage(msg);
                EnigmaStatus newMachine = EnigmaCommons.process(machine);
                println("Encoded: " + newMachine.getEncodedMessage());
                flush(); resetInput();
                readKey();
            }
        } while (true);
    }

    public EnigmaCommons.Rotor askRotor() throws Exception {
        String type;
        print("Type: ");
        do {
            String candidate = readLine();
            candidate = candidate.trim().toUpperCase()
                    .replace("1", "I").replace("2", "II").replace("3", "III");
            if (".".equals(candidate)) continue;
            if (Set.of("I", "II", "III").contains(candidate)) {
                type = candidate;
                break;
            }
            print("(I, II, or III): ");
        } while (true);
        int ring;
        print("Ring: ");
        do {
            String candidate = readLine();
            int candidateInt = NumberUtils.toInt(candidate.trim());
            if (".".equals(candidate)) continue;
            if (candidateInt >= 0 && candidateInt <= 26) {
                ring = candidateInt;
                break;
            }
            print("(0 to 26): ");
        } while (true);
        print("Position: ");
        String candidate = readLine();
        int position = NumberUtils.toInt(candidate);
        return new EnigmaCommons.Rotor(position, ring, type);
    }
}
