package eu.sblendorio.bbs.tenants.mixed;

import com.oracle.truffle.js.runtime.builtins.JSON;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.StringTemplate.STR;

public class EnigmaCommons {

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
        String input = STR."""
            {
               "plugboard": {
                 "wirings": [\{strWirings}]
               },
               "auto_increment_rotors": \{autoIncrementRotors},
               "cleartext": "\{clearText.toUpperCase().trim()}",
               "rotors": [\{strRotors}],
               "reflector": "\{reflector}"
            }
            """;

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
