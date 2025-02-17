package eu.sblendorio.bbs.tenants;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;
import java.util.function.Supplier;

public class CommonConstants {
    private static final String CONFIG_FILE_NAME = "bbsconf.txt";

    public static final String CHATGPT_API = "https://api.openai.com/v1/chat/completions";
    public static final String MISTRAL_API = "https://api.mistral.ai/v1/chat/completions";
    public static final String DSOR_API = "https://openrouter.ai/api/v1/chat/completions";

    public static Supplier<String> chatGptModel = () -> get("CHATGPT_MODEL", "gpt-4o-mini");
    public static Supplier<String> mistralModel = () -> get("MISTRAL_MODEL", "ministral-8b-latest");
    public static Supplier<String> dsorModel = () -> get("DSOR_MODEL", "deepseek/deepseek-r1");

    public static String get(String name, String defaultValue) {
        File f = new File(System.getProperty("user.home") + File.separator + CONFIG_FILE_NAME);
        boolean exists = f.exists();
        if (!exists) return defaultValue;

        Properties properties = new Properties();
        try (FileReader reader = new FileReader(f)) {
            properties.load(reader);
            String value = (String) properties.getOrDefault(name, defaultValue);
            return value.isBlank() ? defaultValue : value;
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }
}
