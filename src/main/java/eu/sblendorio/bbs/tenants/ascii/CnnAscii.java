package eu.sblendorio.bbs.tenants.ascii;

import com.google.common.collect.ImmutableMap;
import eu.sblendorio.bbs.core.Hidden;
import static eu.sblendorio.bbs.core.Utils.bytes;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

@Hidden
public class CnnAscii extends RssAscii {

    protected Map<String, NewsSection> sections;
    protected String type;

    public CnnAscii() {
        this("rss.a1.timeout", "40000", "ascii");
    }

    public CnnAscii(String property, String defaultValue, String interfaceType) {
        super(property, defaultValue);
        type = interfaceType;
        sections = loadSections();
        timeout = toLong(System.getProperty(property, defaultValue));
        logoHeightMenu = logoHeightsMenu.get(interfaceType);
        logoHeightNews = logoHeightsNews.get(interfaceType);
        hrDash = hrDashes.get(interfaceType);
    }

    public CnnAscii(String property, String defaultValue, String interfaceType, byte[] rawMenuScreen, byte[] restartInput) {
        super(property, defaultValue);
        type = interfaceType;
        sections = loadSections();
        timeout = toLong(System.getProperty(property, defaultValue));
        logoHeightMenu = logoHeightsMenu.get(interfaceType);
        logoHeightNews = logoHeightsNews.get(interfaceType);
        hrDash = hrDashes.get(interfaceType);
        this.rawMenuScreen = rawMenuScreen;
        this.restartInput = restartInput;
    }

    public static byte[] line = new byte[] {13, 10, 13, 10};
    public static byte[] noattr = "\033[0m".getBytes(ISO_8859_1);

    @Override
    public byte[] getLogo() {
        return logo.get(type);
    }

    @Override
    public String prefix() {
        return "http://rss.cnn.com/rss/";
    }

    private Map<String, NewsSection> loadSections() {
        Map<String, NewsSection> result = new LinkedHashMap<>();
        result.put("101", new NewsSection("Latest News", prefix() + "cnn_latest.rss", logos.get(type).get("101")));
        result.put("102", new NewsSection("World", prefix() + "edition_world.rss", logos.get(type).get("102")));
        result.put("103", new NewsSection("Africa", prefix() + "edition_africa.rss", logos.get(type).get("103")));
        result.put("104", new NewsSection("Americas", prefix() + "edition_americas.rss", logos.get(type).get("104")));
        // result.put("105", new NewsSection("Asia", prefix() + "edition_asia.rss", logos.get(type).get("105")));
        // result.put("106", new NewsSection("Europe", prefix() + "edition_europe.rss", logos.get(type).get("106")));
        // result.put("107", new NewsSection("Middle East", prefix() + "edition_meast.rss", logos.get(type).get("107")));
        result.put("108", new NewsSection("U.S.A.", prefix() + "edition_us.rss", logos.get(type).get("108")));
        // result.put("109", new NewsSection("Technology", prefix() + "edition_technology.rss", logos.get(type).get("109")));
        // result.put("110", new NewsSection("Science-Space", prefix() + "edition_space.rss", logos.get(type).get("110")));
        // result.put("111", new NewsSection("Entertainment", prefix() + "edition_entertainment.rss", logos.get(type).get("111")));
        // result.put("112", new NewsSection("Money", prefix() + "money_news_international.rss", logos.get(type).get("112")));
        // result.put("113", new NewsSection("World Sport", prefix() + "edition_sport.rss", logos.get(type).get("113")));
        // result.put("114", new NewsSection("Football", prefix() + "edition_football.rss", logos.get(type).get("114")));
        // result.put("115", new NewsSection("Travel", prefix() + "edition_travel.rss", logos.get(type).get("115")));
        result.put("116", new NewsSection("General", prefix() + "edition.rss", logos.get(type).get("116")));
        return result;
    }

    @Override
    public Map<String, NewsSection> sections() {
        return sections;
    }

    public Map<String, byte[]> logo = ImmutableMap.of(
        "minitel", readBinaryFile("minitel/cnn.vdt"),
        "prestel", bytes(readBinaryFile("prestel/cnn.cept3")),
        "ascii", bytes("CNN News\r\n--------", line),
        "ansi", bytes(readBinaryFile("ansi/CnnNews.ans"), noattr),
        "utf8", bytes(readBinaryFile("ansi/CnnNews.utf8ans"), noattr)
    );

    public Map<String, Integer> logoHeightsMenu = ImmutableMap.of(
        "minitel", 3,
        "prestel", 4,
        "ascii", 3,
        "ansi", 4,
        "utf8", 4
    );

    public Map<String, Integer> logoHeightsNews = ImmutableMap.of(
        "minitel", 3,
        "prestel", 3,
        "ascii", 2,
        "ansi", 4,
        "utf8", 4
    );

    public Map<String, byte[]> hrDashes = ImmutableMap.of(
        "minitel", "-".getBytes(ISO_8859_1),
        "prestel", "-".getBytes(ISO_8859_1),
        "ascii", "-".getBytes(ISO_8859_1),
        "ansi", bytes(196),
        "utf8",  "\u2500".getBytes(UTF_8)
    );

    public Map<String, Map<String, byte[]>> logos = ImmutableMap.of(
        "minitel", ImmutableMap.<String, byte[]> builder()
            .put("101", bytes(20, readBinaryFile("minitel/cnn_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Top Stories",13,10,10,17))
            .put("102", bytes(20, readBinaryFile("minitel/cnn_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"World",13,10,10,17))
            .put("103", bytes(20, readBinaryFile("minitel/cnn_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Africa",13,10,10,17))
            .put("104", bytes(20, readBinaryFile("minitel/cnn_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Americas",13,10,10,17))
            .put("105", bytes(20, readBinaryFile("minitel/cnn_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Asia",13,10,10,17))
            .put("106", bytes(20, readBinaryFile("minitel/cnn_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Europe",13,10,10,17))
            .put("107", bytes(20, readBinaryFile("minitel/cnn_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Middle East",13,10,10,17))
            .put("108", bytes(20, readBinaryFile("minitel/cnn_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"U.S.A.",13,10,10,17))
            .put("109", bytes(20, readBinaryFile("minitel/cnn_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Technology",13,10,10,17))
            .put("110", bytes(20, readBinaryFile("minitel/cnn_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Science & Space",13,10,10,17))
            .put("111", bytes(20, readBinaryFile("minitel/cnn_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Entertainment",13,10,10,17))
            .put("112", bytes(20, readBinaryFile("minitel/cnn_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Money",13,10,10,17))
            .put("113", bytes(20, readBinaryFile("minitel/cnn_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"World Sport",13,10,10,17))
            .put("114", bytes(20, readBinaryFile("minitel/cnn_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Football",13,10,10,17))
            .put("115", bytes(20, readBinaryFile("minitel/cnn_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Travel",13,10,10,17))
            .put("116", bytes(20, readBinaryFile("minitel/cnn_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Most Recent",13,10,10,17))
            .build(),
        "prestel", ImmutableMap.<String, byte[]> builder()
            .put("101", bytes(readBinaryFile("prestel/cnn_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Top Stories",13,10,10))
            .put("102", bytes(readBinaryFile("prestel/cnn_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"World",13,10,10))
            .put("103", bytes(readBinaryFile("prestel/cnn_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Africa",13,10,10))
            .put("104", bytes(readBinaryFile("prestel/cnn_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Americas",13,10,10))
            .put("105", bytes(readBinaryFile("prestel/cnn_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Asia",13,10,10))
            .put("106", bytes(readBinaryFile("prestel/cnn_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Europe",13,10,10))
            .put("107", bytes(readBinaryFile("prestel/cnn_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Middle East",13,10,10))
            .put("108", bytes(readBinaryFile("prestel/cnn_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"U.S.A.",13,10,10))
            .put("109", bytes(readBinaryFile("prestel/cnn_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Technology",13,10,10))
            .put("110", bytes(readBinaryFile("prestel/cnn_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Science & Space",13,10,10))
            .put("111", bytes(readBinaryFile("prestel/cnn_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Entertainment",13,10,10))
            .put("112", bytes(readBinaryFile("prestel/cnn_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Money",13,10,10))
            .put("113", bytes(readBinaryFile("prestel/cnn_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"World Sport",13,10,10))
            .put("114", bytes(readBinaryFile("prestel/cnn_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Football",13,10,10))
            .put("115", bytes(readBinaryFile("prestel/cnn_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Travel",13,10,10))
            .put("116", bytes(readBinaryFile("prestel/cnn_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Most Recent",13,10,10))
            .build(),
        "ascii", ImmutableMap.<String, byte[]> builder()
            .put("101", bytes("CNN - Top Stories", line))
            .put("102", bytes("CNN - World", line))
            .put("103", bytes("CNN - Africa", line))
            .put("104", bytes("CNN - Americas", line))
            .put("105", bytes("CNN - Asia", line))
            .put("106", bytes("CNN - Europe", line))
            .put("107", bytes("CNN - Middle East", line))
            .put("108", bytes("CNN - U.S.A.", line))
            .put("109", bytes("CNN - Technology", line))
            .put("110", bytes("CNN - Science & Space", line))
            .put("111", bytes("CNN - Entertainment", line))
            .put("112", bytes("CNN - Money", line))
            .put("113", bytes("CNN - World Sport", line))
            .put("114", bytes("CNN - Football", line))
            .put("115", bytes("CNN - Travel", line))
            .put("116", bytes("CNN - Most Recent", line))
            .build(),
        "ansi", ImmutableMap.<String, byte[]> builder()
            .put("101", bytes(readBinaryFile("ansi/Cnn.ans"), "\033[2;23H\033[0m", "Top Stories", "\033[5;1H"))
            .put("102", bytes(readBinaryFile("ansi/Cnn.ans"), "\033[2;23H\033[0m", "World", "\033[5;1H"))
            .put("103", bytes(readBinaryFile("ansi/Cnn.ans"), "\033[2;23H\033[0m", "Africa", "\033[5;1H"))
            .put("104", bytes(readBinaryFile("ansi/Cnn.ans"), "\033[2;23H\033[0m", "Americas", "\033[5;1H"))
            .put("105", bytes(readBinaryFile("ansi/Cnn.ans"), "\033[2;23H\033[0m", "Asia", "\033[5;1H"))
            .put("106", bytes(readBinaryFile("ansi/Cnn.ans"), "\033[2;23H\033[0m", "Europe", "\033[5;1H"))
            .put("107", bytes(readBinaryFile("ansi/Cnn.ans"), "\033[2;23H\033[0m", "Middle East", "\033[5;1H"))
            .put("108", bytes(readBinaryFile("ansi/Cnn.ans"), "\033[2;23H\033[0m", "U.S.A.", "\033[5;1H"))
            .put("109", bytes(readBinaryFile("ansi/Cnn.ans"), "\033[2;23H\033[0m", "Technology", "\033[5;1H"))
            .put("110", bytes(readBinaryFile("ansi/Cnn.ans"), "\033[2;23H\033[0m", "Science & Space", "\033[5;1H"))
            .put("111", bytes(readBinaryFile("ansi/Cnn.ans"), "\033[2;23H\033[0m", "Entertainment", "\033[5;1H"))
            .put("112", bytes(readBinaryFile("ansi/Cnn.ans"), "\033[2;23H\033[0m", "Money", "\033[5;1H"))
            .put("113", bytes(readBinaryFile("ansi/Cnn.ans"), "\033[2;23H\033[0m", "World Sport", "\033[5;1H"))
            .put("114", bytes(readBinaryFile("ansi/Cnn.ans"), "\033[2;23H\033[0m", "Football", "\033[5;1H"))
            .put("115", bytes(readBinaryFile("ansi/Cnn.ans"), "\033[2;23H\033[0m", "Travel", "\033[5;1H"))
            .put("116", bytes(readBinaryFile("ansi/Cnn.ans"), "\033[2;23H\033[0m", "Most Recent", "\033[5;1H"))
            .build(),
        "utf8", ImmutableMap.<String, byte[]> builder()
            .put("101", bytes(readBinaryFile("ansi/Cnn.utf8ans"), "\033[2;23H\033[0m", "Top Stories", "\033[5;1H"))
            .put("102", bytes(readBinaryFile("ansi/Cnn.utf8ans"), "\033[2;23H\033[0m", "World", "\033[5;1H"))
            .put("103", bytes(readBinaryFile("ansi/Cnn.utf8ans"), "\033[2;23H\033[0m", "Africa", "\033[5;1H"))
            .put("104", bytes(readBinaryFile("ansi/Cnn.utf8ans"), "\033[2;23H\033[0m", "Americas", "\033[5;1H"))
            .put("105", bytes(readBinaryFile("ansi/Cnn.utf8ans"), "\033[2;23H\033[0m", "Asia", "\033[5;1H"))
            .put("106", bytes(readBinaryFile("ansi/Cnn.utf8ans"), "\033[2;23H\033[0m", "Europe", "\033[5;1H"))
            .put("107", bytes(readBinaryFile("ansi/Cnn.utf8ans"), "\033[2;23H\033[0m", "Middle East", "\033[5;1H"))
            .put("108", bytes(readBinaryFile("ansi/Cnn.utf8ans"), "\033[2;23H\033[0m", "U.S.A.", "\033[5;1H"))
            .put("109", bytes(readBinaryFile("ansi/Cnn.utf8ans"), "\033[2;23H\033[0m", "Technology", "\033[5;1H"))
            .put("110", bytes(readBinaryFile("ansi/Cnn.utf8ans"), "\033[2;23H\033[0m", "Science & Space", "\033[5;1H"))
            .put("111", bytes(readBinaryFile("ansi/Cnn.utf8ans"), "\033[2;23H\033[0m", "Entertainment", "\033[5;1H"))
            .put("112", bytes(readBinaryFile("ansi/Cnn.utf8ans"), "\033[2;23H\033[0m", "Money", "\033[5;1H"))
            .put("113", bytes(readBinaryFile("ansi/Cnn.utf8ans"), "\033[2;23H\033[0m", "World Sport", "\033[5;1H"))
            .put("114", bytes(readBinaryFile("ansi/Cnn.utf8ans"), "\033[2;23H\033[0m", "Football", "\033[5;1H"))
            .put("115", bytes(readBinaryFile("ansi/Cnn.utf8ans"), "\033[2;23H\033[0m", "Travel", "\033[5;1H"))
            .put("116", bytes(readBinaryFile("ansi/Cnn.utf8ans"), "\033[2;23H\033[0m", "Most Recent", "\033[5;1H"))
            .build()
    );
}
