package eu.sblendorio.bbs.core;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static java.util.stream.Collectors.toSet;
import java.util.stream.Stream;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.trim;

public class Utils {

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

    public static final Set<Integer> SET_ALPHANUMERIC_WITH_PERIOD = Stream.of(
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.'
    ).map(Integer::valueOf).collect(toSet());

    public static final Set<Integer> SET_ALPHANUMERIC = Stream.of(
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    ).map(Integer::valueOf).collect(toSet());

    public static final Set<Integer> SET_LETTERS_WITH_PERIOD = Stream.of(
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '.'
    ).map(Integer::valueOf).collect(toSet());

    public static final Set<Integer> SET_LETTERS = Stream.of(
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    ).map(Integer::valueOf).collect(toSet());

    public static final Set<Integer> SET_NUMBERS_WITH_PERIOD = Stream.of(
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.'
    ).map(Integer::valueOf).collect(toSet());

    public static final Set<Integer> SET_NUMBERS = Stream.of(
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    ).map(Integer::valueOf).collect(toSet());

    public static boolean isControlChar(int c) { return PETSCII_CONTROL_CHARS.contains(c); }
    public static boolean isControlChar(char c) { return isControlChar((int) c); }

    public static boolean equalsDomain(String a, String b) {
        return normalizeDomain(a).equals(normalizeDomain(b));
    }

    public static String normalizeDomain(String s) {
        return lowerCase(trim(s)).replaceAll("https?:(//)?", EMPTY).replace("www.", EMPTY).replaceAll("/+?$", EMPTY);
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

    private Utils() {
        throw new IllegalStateException("Utility class");
    }

}
