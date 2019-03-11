package eu.sblendorio.bbs.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.maxmind.db.Reader;

import java.io.File;
import java.net.InetAddress;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.*;

public class Utils {

    private static Set<Integer> CONTROL_CHARS = new HashSet<>(Arrays.asList(
            1, 2, 3, 4, 5, 6, 7, 8, 9,
            11, 12,
            14, 15, 16, 17, 18, 19,
            21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31,
            128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140,
            142, 143, 144, 145, 146, 147,
            149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159
    ));

    private static Set<Integer> EXTRA_CONTROL_CHARS = new HashSet<>(Arrays.asList(0, 10, 13, 20, 141, 148));

    public static boolean isControlChar(int c) { return CONTROL_CHARS.contains(c); }
    public static boolean isControlChar(char c) { return isControlChar((int) c); }

    public static boolean isPrintableChar(int c) { return (c >= 32 && c <= 127) || (c >= 160 && c <= 255); }
    public static boolean isPrintableChar(char c) { return isPrintableChar((int) c); }

    public static int lengthPrintable(String s) {
        return filterPrintable(defaultString(s)).length();
    }

    public static String filterPrintable(String s) {
        StringBuffer result = new StringBuffer();
        for (char c: defaultString(s).toCharArray())
            if (isPrintableChar(c)) result.append(c);
        return result.toString();
    }

    public static String filterPrintableWithNewline(String s) {
        StringBuffer result = new StringBuffer();
        for (char c: defaultString(s).toCharArray())
            if (isPrintableChar(c) || c == '\n' || c == '\r') result.append(c);
        return result.toString();
    }

    public static boolean equalsDomain(String a, String b) {
        return normalizeDomain(a).equals(normalizeDomain(b));
    }

    public static String normalizeDomain(String s) {
        return lowerCase(trim(s)).replaceAll("https?:(//)?", EMPTY).replace("www.", EMPTY).replaceAll("/+?$", EMPTY);
    }

}
