package eu.sblendorio.bbs.tenants.ascii;

import com.google.common.collect.ImmutableMap;
import eu.sblendorio.bbs.core.BbsInputOutput;
import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.core.PrestelInputOutput;

import static eu.sblendorio.bbs.core.Utils.bytes;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

@Hidden
public class BbcAscii extends RssAscii {

    protected Map<String, NewsSection> sections;
    protected String type;

    public BbcAscii() {
        this("rss.a1.timeout", "40000", "ascii");
    }

    public BbcAscii(String property, String defaultValue, String interfaceType) {
        super(property, defaultValue);
        type = interfaceType;
        sections = loadSections();
        timeout = toLong(System.getProperty(property, defaultValue));
        logoHeightMenu = logoHeightsMenu.get(interfaceType);
        logoHeightNews = logoHeightsNews.get(interfaceType);
        hrDash = hrDashes.get(interfaceType);
    }

    public BbcAscii(String property, String defaultValue, String interfaceType, byte[] rawMenuScreen, byte[] restartInput) {
        this(null, property, defaultValue, interfaceType, rawMenuScreen, restartInput);
    }

    public BbcAscii(BbsInputOutput inout, String property, String defaultValue, String interfaceType, byte[] rawMenuScreen, byte[] restartInput) {
        super(property, defaultValue);
        this.inout = inout;
        type = interfaceType;
        sections = loadSections();
        timeout = toLong(System.getProperty(property, defaultValue));
        logoHeightMenu = logoHeightsMenu.get(interfaceType);
        logoHeightNews = logoHeightsNews.get(interfaceType);
        hrDash = hrDashes.get(interfaceType);
        this.rawMenuScreen = rawMenuScreen;
        this.restartInput = restartInput;
    }

    @Override
    public void initBbs() throws Exception {
        super.initBbs();
        if ("prestel".equals(type)) setBbsInputOutput(new PrestelInputOutput(socket));
    }

    public static byte[] line = new byte[] {13, 10, 13, 10};
    public static byte[] noattr = "\033[0m".getBytes(ISO_8859_1);

    @Override
    public byte[] getLogo() {
        return logo.get(type);
    }

    @Override
    public String prefix() {
        return "https://feeds.bbci.co.uk/news/";
    }

    private Map<String, NewsSection> loadSections() {
        Map<String, NewsSection> result = new LinkedHashMap<>();
        result.put("201", new NewsSection("Top Stories", prefix() + "rss.xml", logos.get(type).get("201")));
        result.put("202", new NewsSection("World", prefix() + "world/rss.xml", logos.get(type).get("202")));
        result.put("203", new NewsSection("U.K.", prefix() + "uk/rss.xml", logos.get(type).get("203")));
        result.put("204", new NewsSection("Business", prefix() + "business/rss.xml", logos.get(type).get("204")));
        result.put("205", new NewsSection("Politics", prefix() + "politics/rss.xml", logos.get(type).get("205")));
        result.put("206", new NewsSection("Health", prefix() + "health/rss.xml", logos.get(type).get("206")));
        result.put("207", new NewsSection("Education", prefix() + "education/rss.xml", logos.get(type).get("207")));
        result.put("208", new NewsSection("Science", prefix() + "science_and_environment/rss.xml", logos.get(type).get("208")));
        result.put("209", new NewsSection("Technology", prefix() + "technology/rss.xml", logos.get(type).get("209")));
        result.put("210", new NewsSection("Entertain.", prefix() + "entertainment_and_arts/rss.xml", logos.get(type).get("210")));
        result.put("211", new NewsSection("Africa", prefix() + "world/africa/rss.xml", logos.get(type).get("211")));
        result.put("212", new NewsSection("Asia", prefix() + "world/asia/rss.xml", logos.get(type).get("212")));
        result.put("213", new NewsSection("Europe", prefix() + "world/europe/rss.xml", logos.get(type).get("213")));
        result.put("214", new NewsSection("Middle East", prefix() + "world/middle_east/rss.xml", logos.get(type).get("214")));
        result.put("215", new NewsSection("US & Canada", prefix() + "world/us_and_canada/rss.xml", logos.get(type).get("215")));
        result.put("216", new NewsSection("Latin America", prefix() + "world/latin_america/rss.xml", logos.get(type).get("216")));
        result.put("217", new NewsSection("England", prefix() + "england/rss.xml", logos.get(type).get("217")));
        result.put("218", new NewsSection("North. Ireland", prefix() + "northern_ireland/rss.xml", logos.get(type).get("218")));
        result.put("219", new NewsSection("Scotland", prefix() + "scotland/rss.xml", logos.get(type).get("219")));
        result.put("220", new NewsSection("Wales", prefix() + "wales/rss.xml", logos.get(type).get("220")));
        return result;
    }

    @Override
    public Map<String, NewsSection> sections() {
        return sections;
    }

    public Map<String, byte[]> logo = ImmutableMap.of(
        "minitel", bytes("BBC News\r\n--------", line),
        "prestel", bytes("BBC News\r\n--------", line),
        "ascii", bytes("BBC News\r\n--------", line),
        "ansi", bytes(readBinaryFile("ansi/BbcNews.ans"), noattr),
        "utf8", bytes(readBinaryFile("ansi/BbcNews.utf8ans"), noattr)
    );

    public Map<String, Integer> logoHeightsMenu = ImmutableMap.of(
        "minitel", 3,
        "prestel", 3,
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
        "minitel", "`".getBytes(ISO_8859_1),
        "prestel", "-".getBytes(ISO_8859_1),
        "ascii", "-".getBytes(ISO_8859_1),
        "ansi", bytes(196),
        "utf8",  "\u2500".getBytes(UTF_8)
    );

    public Map<String, Map<String, byte[]>> logos = ImmutableMap.of(
        "minitel", ImmutableMap.<String, byte[]> builder()
            .put("201", bytes(20, readBinaryFile("minitel/bbc_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Top Stories", 13,10,10,17))
            .put("202", bytes(20, readBinaryFile("minitel/bbc_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"World", 13,10,10,17))
            .put("203", bytes(20, readBinaryFile("minitel/bbc_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"United Kingdom", 13,10,10,17))
            .put("204", bytes(20, readBinaryFile("minitel/bbc_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Business", 13,10,10,17))
            .put("205", bytes(20, readBinaryFile("minitel/bbc_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Politics", 13,10,10,17))
            .put("206", bytes(20, readBinaryFile("minitel/bbc_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Health", 13,10,10,17))
            .put("207", bytes(20, readBinaryFile("minitel/bbc_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Education", 13,10,10,17))
            .put("208", bytes(20, readBinaryFile("minitel/bbc_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Science", 13,10,10,17))
            .put("209", bytes(20, readBinaryFile("minitel/bbc_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Technology", 13,10,10,17))
            .put("210", bytes(20, readBinaryFile("minitel/bbc_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Entertainment", 13,10,10,17))
            .put("211", bytes(20, readBinaryFile("minitel/bbc_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Africa", 13,10,10,17))
            .put("212", bytes(20, readBinaryFile("minitel/bbc_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Asia", 13,10,10,17))
            .put("213", bytes(20, readBinaryFile("minitel/bbc_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Europe", 13,10,10,17))
            .put("214", bytes(20, readBinaryFile("minitel/bbc_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Middle East", 13,10,10,17))
            .put("215", bytes(20, readBinaryFile("minitel/bbc_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"US & Canada", 13,10,10,17))
            .put("216", bytes(20, readBinaryFile("minitel/bbc_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Latin America", 13,10,10,17))
            .put("217", bytes(20, readBinaryFile("minitel/bbc_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"England", 13,10,10,17))
            .put("218", bytes(20, readBinaryFile("minitel/bbc_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Northern Ireland", 13,10,10,17))
            .put("219", bytes(20, readBinaryFile("minitel/bbc_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Scotland", 13,10,10,17))
            .put("220", bytes(20, readBinaryFile("minitel/bbc_logo.vdt"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Wales", 13,10,10,17))
            .build(),
        "prestel", ImmutableMap.<String, byte[]> builder()
            .put("201", bytes(readBinaryFile("prestel/bbc_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Top Stories",13,10,10))
            .put("202", bytes(readBinaryFile("prestel/bbc_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"World",13,10,10))
            .put("203", bytes(readBinaryFile("prestel/bbc_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"United Kingdom",13,10,10))
            .put("204", bytes(readBinaryFile("prestel/bbc_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Business",13,10,10))
            .put("205", bytes(readBinaryFile("prestel/bbc_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Politics",13,10,10))
            .put("206", bytes(readBinaryFile("prestel/bbc_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Health",13,10,10))
            .put("207", bytes(readBinaryFile("prestel/bbc_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Education",13,10,10))
            .put("208", bytes(readBinaryFile("prestel/bbc_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Science",13,10,10))
            .put("209", bytes(readBinaryFile("prestel/bbc_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Technology",13,10,10))
            .put("210", bytes(readBinaryFile("prestel/bbc_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Entertainment",13,10,10))
            .put("211", bytes(readBinaryFile("prestel/bbc_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Africa",13,10,10))
            .put("212", bytes(readBinaryFile("prestel/bbc_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Asia",13,10,10))
            .put("213", bytes(readBinaryFile("prestel/bbc_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Europe",13,10,10))
            .put("214", bytes(readBinaryFile("prestel/bbc_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Middle East",13,10,10))
            .put("215", bytes(readBinaryFile("prestel/bbc_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"US & Canada",13,10,10))
            .put("216", bytes(readBinaryFile("prestel/bbc_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Latin America",13,10,10))
            .put("217", bytes(readBinaryFile("prestel/bbc_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"England",13,10,10))
            .put("218", bytes(readBinaryFile("prestel/bbc_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Northern Ireland",13,10,10))
            .put("219", bytes(readBinaryFile("prestel/bbc_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Scotland",13,10,10))
            .put("220", bytes(readBinaryFile("prestel/bbc_logo.cept3"), 30, 10,9,9,9,9,9,9,9,9,9,9,9,9,0x1b,0x47,"Wales",13,10,10))
            .build(),
        "ascii", ImmutableMap.<String, byte[]> builder()
            .put("201", bytes("BBC - Top Stories", line))
            .put("202", bytes("BBC - World", line))
            .put("203", bytes("BBC - United Kingdom", line))
            .put("204", bytes("BBC - Business", line))
            .put("205", bytes("BBC - Politics", line))
            .put("206", bytes("BBC - Health", line))
            .put("207", bytes("BBC - Education", line))
            .put("208", bytes("BBC - Science", line))
            .put("209", bytes("BBC - Technology", line))
            .put("210", bytes("BBC - Entertainment", line))
            .put("211", bytes("BBC - Africa", line))
            .put("212", bytes("BBC - Asia", line))
            .put("213", bytes("BBC - Europe", line))
            .put("214", bytes("BBC - Middle East", line))
            .put("215", bytes("BBC - US & Canada", line))
            .put("216", bytes("BBC - Latin America", line))
            .put("217", bytes("BBC - England", line))
            .put("218", bytes("BBC - Northern Ireland", line))
            .put("219", bytes("BBC - Scotland", line))
            .put("220", bytes("BBC - Wales", line))
            .build(),
        "ansi", ImmutableMap.<String, byte[]> builder()
            .put("201", bytes(readBinaryFile("ansi/Bbc.ans"), "\033[2;23H\033[0m", "Top Stories", "\033[5;1H"))
            .put("202", bytes(readBinaryFile("ansi/Bbc.ans"), "\033[2;23H\033[0m", "World", "\033[5;1H"))
            .put("203", bytes(readBinaryFile("ansi/Bbc.ans"), "\033[2;23H\033[0m", "United Kingdom", "\033[5;1H"))
            .put("204", bytes(readBinaryFile("ansi/Bbc.ans"), "\033[2;23H\033[0m", "Business", "\033[5;1H"))
            .put("205", bytes(readBinaryFile("ansi/Bbc.ans"), "\033[2;23H\033[0m", "Politics", "\033[5;1H"))
            .put("206", bytes(readBinaryFile("ansi/Bbc.ans"), "\033[2;23H\033[0m", "Health", "\033[5;1H"))
            .put("207", bytes(readBinaryFile("ansi/Bbc.ans"), "\033[2;23H\033[0m", "Education", "\033[5;1H"))
            .put("208", bytes(readBinaryFile("ansi/Bbc.ans"), "\033[2;23H\033[0m", "Science", "\033[5;1H"))
            .put("209", bytes(readBinaryFile("ansi/Bbc.ans"), "\033[2;23H\033[0m", "Technology", "\033[5;1H"))
            .put("210", bytes(readBinaryFile("ansi/Bbc.ans"), "\033[2;23H\033[0m", "Entertainment", "\033[5;1H"))
            .put("211", bytes(readBinaryFile("ansi/Bbc.ans"), "\033[2;23H\033[0m", "Africa", "\033[5;1H"))
            .put("212", bytes(readBinaryFile("ansi/Bbc.ans"), "\033[2;23H\033[0m", "Asia", "\033[5;1H"))
            .put("213", bytes(readBinaryFile("ansi/Bbc.ans"), "\033[2;23H\033[0m", "Europe", "\033[5;1H"))
            .put("214", bytes(readBinaryFile("ansi/Bbc.ans"), "\033[2;23H\033[0m", "Middle East", "\033[5;1H"))
            .put("215", bytes(readBinaryFile("ansi/Bbc.ans"), "\033[2;23H\033[0m", "US & Canada", "\033[5;1H"))
            .put("216", bytes(readBinaryFile("ansi/Bbc.ans"), "\033[2;23H\033[0m", "Latin America", "\033[5;1H"))
            .put("217", bytes(readBinaryFile("ansi/Bbc.ans"), "\033[2;23H\033[0m", "England", "\033[5;1H"))
            .put("218", bytes(readBinaryFile("ansi/Bbc.ans"), "\033[2;23H\033[0m", "Northern Ireland", "\033[5;1H"))
            .put("219", bytes(readBinaryFile("ansi/Bbc.ans"), "\033[2;23H\033[0m", "Scotland", "\033[5;1H"))
            .put("220", bytes(readBinaryFile("ansi/Bbc.ans"), "\033[2;23H\033[0m", "Wales", "\033[5;1H"))
            .build(),
        "utf8", ImmutableMap.<String, byte[]> builder()
            .put("201", bytes(readBinaryFile("ansi/Bbc.utf8ans"), "\033[2;23H\033[0m", "Top Stories", "\033[5;1H"))
            .put("202", bytes(readBinaryFile("ansi/Bbc.utf8ans"), "\033[2;23H\033[0m", "World", "\033[5;1H"))
            .put("203", bytes(readBinaryFile("ansi/Bbc.utf8ans"), "\033[2;23H\033[0m", "United Kingdom", "\033[5;1H"))
            .put("204", bytes(readBinaryFile("ansi/Bbc.utf8ans"), "\033[2;23H\033[0m", "Business", "\033[5;1H"))
            .put("205", bytes(readBinaryFile("ansi/Bbc.utf8ans"), "\033[2;23H\033[0m", "Politics", "\033[5;1H"))
            .put("206", bytes(readBinaryFile("ansi/Bbc.utf8ans"), "\033[2;23H\033[0m", "Health", "\033[5;1H"))
            .put("207", bytes(readBinaryFile("ansi/Bbc.utf8ans"), "\033[2;23H\033[0m", "Education", "\033[5;1H"))
            .put("208", bytes(readBinaryFile("ansi/Bbc.utf8ans"), "\033[2;23H\033[0m", "Science", "\033[5;1H"))
            .put("209", bytes(readBinaryFile("ansi/Bbc.utf8ans"), "\033[2;23H\033[0m", "Technology", "\033[5;1H"))
            .put("210", bytes(readBinaryFile("ansi/Bbc.utf8ans"), "\033[2;23H\033[0m", "Entertainment", "\033[5;1H"))
            .put("211", bytes(readBinaryFile("ansi/Bbc.utf8ans"), "\033[2;23H\033[0m", "Africa", "\033[5;1H"))
            .put("212", bytes(readBinaryFile("ansi/Bbc.utf8ans"), "\033[2;23H\033[0m", "Asia", "\033[5;1H"))
            .put("213", bytes(readBinaryFile("ansi/Bbc.utf8ans"), "\033[2;23H\033[0m", "Europe", "\033[5;1H"))
            .put("214", bytes(readBinaryFile("ansi/Bbc.utf8ans"), "\033[2;23H\033[0m", "Middle East", "\033[5;1H"))
            .put("215", bytes(readBinaryFile("ansi/Bbc.utf8ans"), "\033[2;23H\033[0m", "US & Canada", "\033[5;1H"))
            .put("216", bytes(readBinaryFile("ansi/Bbc.utf8ans"), "\033[2;23H\033[0m", "Latin America", "\033[5;1H"))
            .put("217", bytes(readBinaryFile("ansi/Bbc.utf8ans"), "\033[2;23H\033[0m", "England", "\033[5;1H"))
            .put("218", bytes(readBinaryFile("ansi/Bbc.utf8ans"), "\033[2;23H\033[0m", "Northern Ireland", "\033[5;1H"))
            .put("219", bytes(readBinaryFile("ansi/Bbc.utf8ans"), "\033[2;23H\033[0m", "Scotland", "\033[5;1H"))
            .put("220", bytes(readBinaryFile("ansi/Bbc.utf8ans"), "\033[2;23H\033[0m", "Wales", "\033[5;1H"))
            .build()
    );

}
