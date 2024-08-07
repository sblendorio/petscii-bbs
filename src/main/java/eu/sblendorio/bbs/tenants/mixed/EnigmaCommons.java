package eu.sblendorio.bbs.tenants.mixed;

import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.Utils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static eu.sblendorio.bbs.core.Utils.STR_LETTER;
import static eu.sblendorio.bbs.core.Utils.setOfChars;

public class EnigmaCommons {

    public static class EnigmaStatus {
        private List<EnigmaCommons.Wiring> wirings = new LinkedList<>();
        private List<EnigmaCommons.Rotor> rotors = List.of(
                new Rotor(0, 0, "I"),
                new Rotor(0, 0, "I"),
                new Rotor(0, 0, "I")
        );
        private boolean autoIncrementRotors = true;
        private String reflector = "UKW-A";
        private String originalMessage = "";
        private String encodedMessage = "";

        public EnigmaStatus() {
        }

        public EnigmaStatus(List<Wiring> wirings, List<Rotor> rotors, boolean autoIncrementRotors, String reflector) {
            this.wirings = wirings;
            this.rotors = rotors;
            this.autoIncrementRotors = autoIncrementRotors;
            this.reflector = reflector;
        }

        public List<Wiring> getWirings() {
            return wirings;
        }

        public void setWirings(List<Wiring> wirings) {
            this.wirings = wirings;
        }

        public List<Rotor> getRotors() {
            return rotors;
        }

        public void setRotors(List<Rotor> rotors) {
            this.rotors = rotors;
        }

        public boolean getAutoIncrementRotors() {
            return autoIncrementRotors;
        }

        public void setAutoIncrementRotors(boolean autoIncrementRotors) {
            this.autoIncrementRotors = autoIncrementRotors;
        }

        public String getReflector() {
            return reflector;
        }

        public void setReflector(String reflector) {
            this.reflector = reflector;
        }

        public String getOriginalMessage() {
            return originalMessage;
        }

        public void setOriginalMessage(String originalMessage) {
            this.originalMessage = originalMessage;
        }

        public String getEncodedMessage() {
            return encodedMessage;
        }

        public void setEncodedMessage(String encodedMessage) {
            this.encodedMessage = encodedMessage;
        }
    }

    private static String URL = "https://nuvolaris.dev/api/v1/web/dmaggiorotto/Test/enigma/${MACHINE_TYPE}/encrypt";
    // private static String URL = "https://enigma-rest-api-1-0.onrender.com/enigma-api/v1/enigma/${MACHINE_TYPE}/encrypt";

    public record Wiring(String fromLetter, String toLetter) {}
    public record Rotor(int position, int ring, String type) {} // type = I/II/III

    private static String wiring2string(Wiring w) {
        return "{\"from_letter\":\""+w.fromLetter().toUpperCase().trim()+"\", \"to_letter\":\""+w.toLetter().toUpperCase().trim()+"\"}";
    }

    private static String rotor2string(Rotor r) {
        return "{\"position\":" + r.position() + ", \"ring\":" + r.ring() + ", \"type\":\"" + r.type().toUpperCase().trim() + "\"}";
    }
    // reflector: UKW-A, UKW-B, UKW-C

    private static void validate(EnigmaStatus machine) throws Exception {
        List<Rotor> rotors = machine.getRotors() == null ? Collections.emptyList() : machine.getRotors();
        if (rotors.size() != 3)
            throw new IllegalArgumentException("Invalid rotors count");
        if (machine.getReflector() == null || !Set.of("UKW-A", "UKW-B", "UKW-C").contains(machine.getReflector().toUpperCase().trim()))
            throw new IllegalArgumentException("Invalid reflector");
        if (rotors.stream().filter(x -> !Set.of("I", "II", "III").contains(x.type().toUpperCase().trim())).count() != 0)
            throw new IllegalArgumentException("Invalid rotor type");
    }

    public static EnigmaStatus process(EnigmaStatus machine) throws Exception {
        validate(machine);

        List<Wiring> wirings = machine.getWirings() == null ? Collections.emptyList() : machine.getWirings();
        List<Rotor> rotors = machine.getRotors() == null ? Collections.emptyList() : machine.getRotors();
        String clearText = machine.getOriginalMessage() == null ? "" : machine.getOriginalMessage().toUpperCase().trim().replace("[^A-Z]", "");

        String strWirings = wirings.stream().map(EnigmaCommons::wiring2string).collect(Collectors.joining(","));
        String strRotors = rotors.stream().map(EnigmaCommons::rotor2string).collect(Collectors.joining(","));
        String input = """
            {
               "plugboard": {
                 "wirings": [${strWirings}]
               },
               "auto_increment_rotors": ${autoIncrementRotors},
               "cleartext": "${clearText}",
               "rotors": [${strRotors}],
               "reflector": "${reflector}"
            }
            """
                .replace("${strWirings}", strWirings)
                .replace("${autoIncrementRotors}", String.valueOf(machine.getAutoIncrementRotors()))
                .replace("${clearText}", clearText.toUpperCase().trim())
                .replace("${strRotors}", strRotors)
                .replace("${reflector}", machine.getReflector());


        String url = URL.replace("${MACHINE_TYPE}", "I");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("accept", "application/json")
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(input))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            System.out.println(response.body());
            throw new IllegalStateException("BAD HTTP REQUEST");
        }

        JSONObject root = (JSONObject) new JSONParser().parse(response.body());
        machine.setEncodedMessage((String) root.get("cyphertext"));
        return machine;
    }







    public static void showConfig(BbsThread bbs, EnigmaStatus machine) {
        bbs.println("Reflector: " + machine.getReflector());
        bbs.println("Number of wirings: "+machine.getWirings().size());
        int i=0;
        for (EnigmaCommons.Wiring w: machine.getWirings()) {
            i++;
            bbs.println("  "+i+". From " + w.fromLetter() + " to " + w.toLetter());
        }
        i=0;
        for (EnigmaCommons.Rotor r: machine.getRotors()) {
            i++;
            bbs.println("Rotor#"+i+": Type "+r.type()+", Ring "+r.ring()+", Position "+r.position());
        }
    }

    public static void menu(BbsThread bbs, EnigmaStatus machine) throws Exception {
        do {
            bbs.cls();
            bbs.println("Enigma machine by Denis Maggiorotto");
            bbs.println("-----------------------------------");
            showConfig(bbs, machine);
            bbs.println();
            bbs.println("1. Change rotor 1");
            bbs.println("2. Change rotor 2");
            bbs.println("3. Change rotor 3");
            bbs.println("4. Change reflector");
            bbs.println("5. Add wiring");
            bbs.println("6. Remove wiring");
            bbs.println("7. Encrypt/Decrypt a string");
            bbs.println(". - Back to main menu");
            bbs.println();
            bbs.flush(); bbs.resetInput();
            int ch = bbs.readKey();
            if (ch == '.') break;
            if (ch == '1') {
                bbs.println("Change rotor 1.");
                EnigmaCommons.Rotor rotor0 = askRotor(bbs);
                machine.setRotors(List.of(
                        rotor0,
                        machine.getRotors().get(1),
                        machine.getRotors().get(2)
                ));
            }
            if (ch == '2') {
                bbs.println("Change rotor 2.");
                EnigmaCommons.Rotor rotor1 = askRotor(bbs);
                machine.setRotors(List.of(
                        machine.getRotors().get(0),
                        rotor1,
                        machine.getRotors().get(2)
                ));
            }
            if (ch == '3') {
                bbs.println("Change rotor 3.");
                EnigmaCommons.Rotor rotor2 = askRotor(bbs);
                machine.setRotors(List.of(
                        machine.getRotors().get(0),
                        machine.getRotors().get(1),
                        rotor2
                ));
            }
            if (ch == '4') {
                bbs.print("Enter reflector: ");
                do {
                    bbs.flush(); bbs.resetInput();
                    String candidate = bbs.readLine();
                    candidate = candidate.trim().toUpperCase();
                    if (".".equals(candidate)) continue;
                    if (Set.of("UKW-A", "UKW-B", "UKW-C").contains(candidate)) {
                        machine.setReflector(candidate);
                        break;
                    }
                    bbs.print("(UKW-A, UKW-B or UKW-C): ");
                } while (true);
            }
            if (ch == '5') {
                bbs.print("From: "); bbs.flush(); bbs.resetInput();
                String from = bbs.readLine(1, setOfChars(Utils.STR_LETTER));
                if (StringUtils.isEmpty(from)) continue;
                bbs.print("To: "); bbs.flush(); bbs.resetInput();
                String to = bbs.readLine(1, setOfChars(Utils.STR_LETTER));
                if (StringUtils.isEmpty(to)) continue;
                machine.getWirings().add(new EnigmaCommons.Wiring(from.toUpperCase(), to.toUpperCase()));
            }
            if (ch == '6' && !machine.getWirings().isEmpty()) {
                machine.getWirings().removeLast();
            }
            if (ch == '7') {
                bbs.print("Message: ");
                bbs.flush(); bbs.resetInput();
                String msg = bbs.readLineUppercase(setOfChars(STR_LETTER));
                if (StringUtils.isEmpty(msg)) continue;
                machine.setOriginalMessage(msg.toUpperCase());
                EnigmaStatus newMachine = EnigmaCommons.process(machine);
                bbs.println("Encoded: " + newMachine.getEncodedMessage());
                bbs.flush(); bbs.resetInput();
                bbs.readKey();
            }
        } while (true);
    }

    public static EnigmaCommons.Rotor askRotor(BbsThread bbs) throws Exception {
        String type;
        bbs.print("Type: ");
        do {
            bbs.flush(); bbs.resetInput();
            String candidate = bbs.readLine();
            candidate = candidate.trim().toUpperCase()
                    .replace("1", "I").replace("2", "II").replace("3", "III");
            if (".".equals(candidate)) continue;
            if (Set.of("I", "II", "III").contains(candidate)) {
                type = candidate;
                break;
            }
            bbs.print("(I, II, or III): ");
        } while (true);
        int ring;
        bbs.print("Ring: ");
        do {
            bbs.flush(); bbs.resetInput();
            String candidate = bbs.readLine();
            int candidateInt = NumberUtils.toInt(candidate.trim());
            if (".".equals(candidate)) continue;
            if (candidateInt >= 0 && candidateInt <= 26) {
                ring = candidateInt;
                break;
            }
            bbs.print("(0 to 26): ");
        } while (true);
        bbs.print("Position: "); bbs.flush(); bbs.resetInput();
        String candidate = bbs.readLine();
        int position = NumberUtils.toInt(candidate);
        return new EnigmaCommons.Rotor(position, ring, type);
    }
}
