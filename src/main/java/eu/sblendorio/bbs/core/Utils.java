package eu.sblendorio.bbs.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.trim;

public class Utils {
    private static final Logger logger = LogManager.getLogger(Utils.class);
    private static final ClassLoader NULL_CLASSLOADER = null;

    private static final Set<Integer> PETSCII_CONTROL_CHARS = new HashSet<>(Arrays.asList(
            1, 2, 3, 4, 5, 6, 7, 8, 9,
            11, 12,
            14, 15, 16, 17, 18, 19,
            21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31,
            128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140,
            142, 143, 144, 145, 146, 147,
            149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159
    ));

    // EXTRA_CONTROL_CHARS: 0, 10, 13, 20, 141, 148

    public static final String STR_ALPHANUMERIC = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static final String STR_LETTER = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static final String STR_NUMBERS = "0123456789";

    public static boolean isControlChar(int c) { return PETSCII_CONTROL_CHARS.contains(c); }
    public static boolean isControlChar(char c) { return isControlChar((int) c); }

    public static boolean equalsDomain(String a, String b) {
        return normalizeDomain(a).equals(normalizeDomain(b));
    }

    public static String normalizeDomain(String s) {
        return lowerCase(trim(s)).replaceAll("https?:(//)?", EMPTY).replace("www.", EMPTY).replaceAll("/+?$", EMPTY);
    }

    public static Set<Integer> setOfChars(String... strings) {
        if (strings == null)
            return Collections.emptySet();

        Set<Integer> result = new HashSet<>();
        for (String str: strings) {
            if (str == null) continue;
            for (char ch : str.toCharArray()) {
                result.add((int) ch);
            }
        }
        return result;
    }

    public static byte[] bytes(Object... objects) {
        if (objects == null)
            return new byte[] {};

        List<Byte> result = new ArrayList<>();
        for (Object o: objects) {
            if (o instanceof Integer) {
                int i = (Integer) o;
                result.add((byte) i);
            } else if (o instanceof Long) {
                long l = (Long) o;
                result.add((byte) l);
            } else if (o instanceof Short) {
                short s = (Short) o;
                result.add((byte) s);
            } else if (o instanceof Byte) {
                byte b = (Byte) o;
                result.add(b);
            } else if (o instanceof String) {
                byte[] bytes = ((String) o).getBytes(StandardCharsets.ISO_8859_1);
                for (byte b : bytes) result.add(b);
            } else if (o instanceof byte[]) {
                byte[] bytes = (byte[]) o;
                for (byte b : bytes) result.add(b);
            }
        }
        byte[] bytes = new byte[result.size()];
        for (int i = 0; i < result.size(); ++i) bytes[i] = result.get(i);
        return bytes;
    }


    public static List<Path> getDirContent(String path) throws URISyntaxException, IOException {
        List<Path> result = new ArrayList<>();
        URL jar = Utils.class.getProtectionDomain().getCodeSource().getLocation();
        Path jarFile = Paths.get(jar.toURI());
        try (FileSystem fs = FileSystems.newFileSystem(jarFile, NULL_CLASSLOADER);
             DirectoryStream<Path> directoryStream = Files.newDirectoryStream(fs.getPath(path))) {
            for (Path p : directoryStream) {
                result.add(p);
            }

            result.sort((o1, o2) -> o1 == null || o2 == null ? 0 :
                o1.getFileName().toString().toLowerCase().compareTo(o2.getFileName().toString().toLowerCase()));
            return result;
        }
    }

    public static List<String> readExternalTxt(String filename) {
        List<String> result = new LinkedList<>();
        try {
            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                result.add(myReader.nextLine());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            logger.error("An error occurred.", e);
        }
        return result;
    }

    public static String hex(byte[] byteArray) {
        StringBuffer hexStringBuffer = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            hexStringBuffer.append(hex(byteArray[i]));
        }
        return hexStringBuffer.toString();
    }

    public static String hex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }

    private Utils() {
        throw new IllegalStateException("Utility class");
    }
}
