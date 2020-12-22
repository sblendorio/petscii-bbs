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

    public static byte[] line = new byte[] {13, 10, 13, 10};

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
        result.put("101", new NewsSection("Top Stories", prefix() + "edition.rss", bytes("CNN - Top Stories", line)));
        result.put("102", new NewsSection("World", prefix() + "edition_world.rss", bytes("CNN - World", line)));
        result.put("103", new NewsSection("Africa", prefix() + "edition_africa.rss", bytes("CNN - Africa", line)));
        result.put("104", new NewsSection("Americas", prefix() + "edition_americas.rss", bytes("CNN - Americas", line)));
        result.put("105", new NewsSection("Asia", prefix() + "edition_asia.rss", bytes("CNN - Asia", line)));
        result.put("106", new NewsSection("Europe", prefix() + "edition_europe.rss", bytes("CNN - Europe", line)));
        result.put("107", new NewsSection("Middle East", prefix() + "edition_meast.rss", bytes("CNN - Middle East", line)));
        result.put("108", new NewsSection("U.S.A.", prefix() + "edition_us.rss", bytes("CNN - U.S.A.", line)));
        result.put("109", new NewsSection("Technology", prefix() + "edition_technology.rss", bytes("CNN - Technology", line)));
        result.put("110", new NewsSection("Science-Space", prefix() + "edition_space.rss", bytes("CNN - Science & Space", line)));
        result.put("111", new NewsSection("Entertainment", prefix() + "edition_entertainment.rss", bytes("CNN - Entertainment", line)));
        result.put("112", new NewsSection("Money", prefix() + "money_news_international.rss", bytes("CNN - Money", line)));
        result.put("113", new NewsSection("World Sport", prefix() + "edition_sport.rss", bytes("CNN - World Sport", line)));
        result.put("114", new NewsSection("Football", prefix() + "edition_football.rss", bytes("CNN - Football", line)));
        result.put("115", new NewsSection("Travel", prefix() + "edition_travel.rss", bytes("CNN - Travel", line)));
        result.put("116", new NewsSection("Most Recent", prefix() + "cnn_latest.rss", bytes("CNN - Most Recent", line)));
        return result;
    }

    @Override
    public Map<String, NewsSection> sections() {
        return sections;
    }

    public Map<String, byte[]> logo = ImmutableMap.of(
        "ascii", bytes("CNN News\r\n--------", line),
        "ansi", readBinaryFile("ansi/CnnNews.ans"),
        "utf8", readBinaryFile("ansi/CnnNews.utf8ans")
    );

    public Map<String, Integer> logoHeightsMenu = ImmutableMap.of(
        "ascii", 2,
        "ansi", 4,
        "utf8", 4
    );

    public Map<String, Integer> logoHeightsNews = ImmutableMap.of(
        "ascii", 2,
        "ansi", 2,
        "utf8", 2
    );

    public Map<String, byte[]> hrDashes = ImmutableMap.of(
        "ascii", "-".getBytes(ISO_8859_1),
        "ansi", bytes(196),
        "utf8",  "\u2500".getBytes(UTF_8)
    );

}
