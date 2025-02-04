package eu.sblendorio.bbs.tenants;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.function.Supplier;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toMap;

public class CommonConstants {
    private static final String CONFIG_FILE_NAME = "bbsconf.txt";

    public static final String CHATGPT_API = "https://api.openai.com/v1/chat/completions";
    public static final String MISTRAL_API = "https://api.mistral.ai/v1/chat/completions";

    public static Supplier<String> chatGptModel = () -> get("CHATGPT_MODEL", "gpt-4o-mini");
    public static Supplier<String> mistralModel = () -> get("MISTRAL_MODEL", "ministral-8b-latest");

    public static String get(String name, String defaultValue) {
        File f = new File(System.getProperty("user.home") + File.separator + CONFIG_FILE_NAME);
        boolean exists = f.exists();
        if (!exists) return defaultValue;

        try (var lines = Files.lines(f.toPath(), UTF_8)) {
            Map<String, String> configMap = lines
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                    .map(line -> line.split("=", 2))
                    .filter(parts -> parts.length == 2)
                    .collect(toMap(
                            parts -> parts[0].trim(),
                            parts -> parts[1].trim(),
                            (v1, v2) -> v1  // In case of duplicate keys, keep first value
                    ));

            var value = configMap.getOrDefault(name, defaultValue);
            return value.isBlank() ? defaultValue : value;
        } catch (IOException e) {
            e.printStackTrace();
            return defaultValue;
        }

    }
}
