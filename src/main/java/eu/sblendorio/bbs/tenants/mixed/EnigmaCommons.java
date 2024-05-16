package eu.sblendorio.bbs.tenants.mixed;

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

}
