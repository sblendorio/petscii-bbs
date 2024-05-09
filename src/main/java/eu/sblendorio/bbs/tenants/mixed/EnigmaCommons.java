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
        private List<EnigmaCommons.Rotor> rotors = new LinkedList<>();
        private boolean autoIncrementRotors = false;
        private String reflector = "UKW-A";

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

    private static void validate(String machineType, List<Wiring> originWirings, List<Rotor> originRotors, String reflector) throws Exception {
        List<Rotor> rotors = originRotors == null ? Collections.emptyList() : originRotors;
        if (rotors.size() != 3)
            throw new IllegalArgumentException("Invalid rotors count");
        if (reflector == null || !Set.of("UKW-A", "UKW-B", "UKW-C").contains(reflector.toUpperCase().trim()))
            throw new IllegalArgumentException("Invalid reflector");
        if (rotors.stream().filter(x -> !Set.of("I", "II", "III").contains(x.type().toUpperCase().trim())).count() != 0)
            throw new IllegalArgumentException("Invalid rotor type");
    }

    public static String process(String originClearText, String machineType, List<Wiring> originWirings, boolean autoIncrementRotors, List<Rotor> originRotors, String reflector) throws Exception {
        validate(machineType, originWirings, originRotors, reflector);

        List<Wiring> wirings = originWirings == null ? Collections.emptyList() : originWirings;
        List<Rotor> rotors = originRotors == null ? Collections.emptyList() : originRotors;
        String clearText = originClearText == null ? "" : originClearText.toUpperCase().trim().replace("[^A-Z]", "");

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
                .replace("${autoIncrementRotors}", String.valueOf(autoIncrementRotors))
                .replace("${clearText}", clearText.toUpperCase().trim())
                .replace("${strRotors}", strRotors)
                .replace("${reflector}", reflector);


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
        return (String) root.get("cyphertext");
    }

    public static void main(String[] args) throws Exception {
        String result = process(
                "GCPDTZGHROYYSTEJR",
                "I",
                List.of(
                        new Wiring("D", "Z")
                ),
                true,
                List.of(
                        new Rotor(0, 0, "I"),
                        new Rotor(0, 0, "I"),
                        new Rotor(0, 0, "I")
                ),
                "UKW-A"
        );

        System.out.println(result);
    }

}
