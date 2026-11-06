package eu.sblendorio.bbs.tenants.ascii;

import com.google.common.collect.ImmutableMap;
import eu.sblendorio.bbs.core.BbsInputOutput;
import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.core.PrestelInputOutput;
import eu.sblendorio.bbs.core.Utils;
import eu.sblendorio.bbs.tenants.mixed.RaiNewsArticles;
import static eu.sblendorio.bbs.core.Utils.bytes;
import java.nio.charset.StandardCharsets;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

@Hidden
public class TelevideoRaiAscii extends RssAscii {
    protected Map<String, NewsSection> sections;
    protected String type;

    public TelevideoRaiAscii() {
        this("rss.a1.timeout", "40000", "ascii");
    }

    public TelevideoRaiAscii(String timeoutProperty, String defaultValue, String interfaceType, byte[] rawMenuScreen, byte[] restartInput) {
        this(null, timeoutProperty, defaultValue, interfaceType, rawMenuScreen, restartInput);
    }
    public TelevideoRaiAscii(BbsInputOutput inout, String timeoutProperty, String defaultValue, String interfaceType, byte[] rawMenuScreen, byte[] restartInput) {
        super(timeoutProperty, defaultValue);
        this.inout = inout;
        type = interfaceType;
        sections = loadSections();
        timeout = toLong(System.getProperty(timeoutProperty, defaultValue));
        logoHeightMenu = logoHeightsMenu.get(interfaceType);
        logoHeightNews = logoHeightsNews.get(interfaceType);
        hrDash = hrDashes.get(interfaceType);
        this.rawMenuScreen = rawMenuScreen;
        this.restartInput = restartInput;
    }

    public TelevideoRaiAscii(String property, String defaultValue, String interfaceType, byte[] rawMenuScreen) {
        this(property, defaultValue, interfaceType, rawMenuScreen, null);
    }

    public TelevideoRaiAscii(String property, String defaultValue, String interfaceType) {
        this(property, defaultValue, interfaceType, null, null);
    }

    @Override
    public void initBbs() throws Exception {
        super.initBbs();
        if ("prestel".equals(type)) setBbsInputOutput(new PrestelInputOutput(socket));
    }

    @Override
    public byte[] getLogo() {
        return logo.get(type);
    }

    @Override
    public String prefix() {
        return "https://www.rainews.it/rss/";
    }

    private static final int ARTICLE_MAX_CHARS = 4000;

    // Televideo RSS feeds (servizitelevideo.rai.it/televideo/pub/rssNNN.xml) were dismissed in Nov 2026:
    // sections now come from the RaiNews feeds, https://www.rainews.it/rss/<thematic area>
    private Map<String, NewsSection> loadSections() {
        Map<String, NewsSection> result = new LinkedHashMap<>();
        result.put("101", new NewsSection("Ultim'Ora", prefix() + "ultimora", logos.get(type).get("101")));
        result.put("110", new NewsSection("Primo Piano", prefix() + "tutti", logos.get(type).get("110")));
        result.put("120", new NewsSection("Politica", prefix() + "politica", logos.get(type).get("120")));
        result.put("130", new NewsSection("Economia", prefix() + "economiaefinanza", logos.get(type).get("130")));
        result.put("140", new NewsSection("Dall'Italia", prefix() + "cronaca", logos.get(type).get("140")));
        result.put("150", new NewsSection("Dal Mondo", prefix() + "esteri", logos.get(type).get("150")));
        result.put("160", new NewsSection("Culture", prefix() + "artiespettacolo", logos.get(type).get("160")));
        result.put("260", new NewsSection("Sport", prefix() + "sport", logos.get(type).get("260")));
        return result;
    }

    @Override
    public Map<String, NewsSection> sections() {
        return sections;
    }

    // RaiNews RSS descriptions are short: the text, up to about one Televideo page, is taken from the article page.
    // Pages without text (e.g. whole TG episodes, only video) keep the RSS description
    @Override
    protected Map<String, String> articleTexts(NewsSection section, List<NewsFeed> feeds) {
        return section.url.startsWith(prefix())
                ? RaiNewsArticles.fetchTexts(feeds.stream().map(feed -> feed.uri).toList(), ARTICLE_MAX_CHARS)
                : Map.of();
    }

    public static byte[] line = new byte[] {13, 10, 13, 10};
    public static byte[] noattr = "\033[0m".getBytes(ISO_8859_1);

    public Map<String, byte[]> logo = ImmutableMap.of(
    "minitel", bytes("Televideo\r\n---------", line),
    "prestel", bytes("Televideo\r\n---------", line),
    "ascii", bytes("Televideo\r\n---------", line),
    "ansi", bytes(readBinaryFile("ansi/Televideo.ans"), noattr),
    "utf8", bytes(readBinaryFile("ansi/Televideo.utf8ans"), noattr)
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
            .put("101", bytes(20, 0x1b, 0x3a, 0x6a, 0x43, readBinaryFile("minitel/televideo/ultimora.vdt"),30,10,10,10, 0x1b, 0x3a, 0x69, 0x43, 17))
            .put("102", bytes(20, 0x1b, 0x3a, 0x6a, 0x43, readBinaryFile("minitel/televideo/24h.vdt"),30,10,10,10, 0x1b, 0x3a, 0x69, 0x43, 17))
            .put("110", bytes(20, 0x1b, 0x3a, 0x6a, 0x43, readBinaryFile("minitel/televideo/primo_piano.vdt"),30,10,10,10, 0x1b, 0x3a, 0x69, 0x43, 17))
            .put("120", bytes(20, 0x1b, 0x3a, 0x6a, 0x43, readBinaryFile("minitel/televideo/politica.vdt"),30,10,10,10, 0x1b, 0x3a, 0x69, 0x43, 17))
            .put("130", bytes(20, 0x1b, 0x3a, 0x6a, 0x43, readBinaryFile("minitel/televideo/economia.vdt"),30,10,10,10, 0x1b, 0x3a, 0x69, 0x43, 17))
            .put("140", bytes(20, 0x1b, 0x3a, 0x6a, 0x43, readBinaryFile("minitel/televideo/dall_italia.vdt"),30,10,10,10, 0x1b, 0x3a, 0x69, 0x43, 17))
            .put("150", bytes(20, 0x1b, 0x3a, 0x6a, 0x43, readBinaryFile("minitel/televideo/dal_mondo.vdt"),30,10,10,10, 0x1b, 0x3a, 0x69, 0x43, 17))
            .put("160", bytes(20, 0x1b, 0x3a, 0x6a, 0x43, readBinaryFile("minitel/televideo/culture.vdt"),30,10,10,10, 0x1b, 0x3a, 0x69, 0x43, 17))
            .put("170", bytes(20, 0x1b, 0x3a, 0x6a, 0x43, readBinaryFile("minitel/televideo/cittadini.vdt"),30,10,10,10, 0x1b, 0x3a, 0x69, 0x43, 17))
            .put("180", bytes(20, 0x1b, 0x3a, 0x6a, 0x43, readBinaryFile("minitel/televideo/speciale.vdt"),30,10,10,10, 0x1b, 0x3a, 0x69, 0x43, 17))
            .put("190", bytes(20, 0x1b, 0x3a, 0x6a, 0x43, readBinaryFile("minitel/televideo/atlante_crisi.vdt"),30,10,10,10, 0x1b, 0x3a, 0x69, 0x43, 17))
            .put("229", bytes(20, 0x1b, 0x3a, 0x6a, 0x43, readBinaryFile("minitel/televideo/calcio_brevi.vdt"),30,10,10,10, 0x1b, 0x3a, 0x69, 0x43, 17))
            .put("230", bytes(20, 0x1b, 0x3a, 0x6a, 0x43, readBinaryFile("minitel/televideo/calcio_squadre.vdt"),30,10,10,10, 0x1b, 0x3a, 0x69, 0x43, 17))
            .put("260", bytes(20, 0x1b, 0x3a, 0x6a, 0x43, readBinaryFile("minitel/televideo/altri_sport.vdt"),30,10,10,10, 0x1b, 0x3a, 0x69, 0x43, 17))
            .put("299", bytes(20, 0x1b, 0x3a, 0x6a, 0x43, readBinaryFile("minitel/televideo/brevissime.vdt"),30,10,10,10, 0x1b, 0x3a, 0x69, 0x43, 17))
            .put("810", bytes(20, 0x1b, 0x3a, 0x6a, 0x43, readBinaryFile("minitel/televideo/motori.vdt"),30,10,10,10, 0x1b, 0x3a, 0x69, 0x43, 17))
            .build(),
        "prestel", ImmutableMap.<String, byte[]> builder()
            .put("101", bytes(readBinaryFile("prestel/televideo/ultimora.cept3"),30,10,10,10))
            .put("102", bytes(readBinaryFile("prestel/televideo/24h.cept3"),30,10,10,10))
            .put("110", bytes(readBinaryFile("prestel/televideo/primopiano.cept3"),30,10,10,10))
            .put("120", bytes(readBinaryFile("prestel/televideo/politica.cept3"),30,10,10,10))
            .put("130", bytes(readBinaryFile("prestel/televideo/economia.cept3"),30,10,10,10))
            .put("140", bytes(readBinaryFile("prestel/televideo/dallitalia.cept3"),30,10,10,10))
            .put("150", bytes(readBinaryFile("prestel/televideo/dalmondo.cept3"),30,10,10,10))
            .put("160", bytes(readBinaryFile("prestel/televideo/culture.cept3"),30,10,10,10))
            .put("170", bytes(readBinaryFile("prestel/televideo/cittadini.cept3"),30,10,10,10))
            .put("180", bytes(readBinaryFile("prestel/televideo/speciale.cept3"),30,10,10,10))
            .put("190", bytes(readBinaryFile("prestel/televideo/atlantecrisi.cept3"),30,10,10,10))
            .put("229", bytes(readBinaryFile("prestel/televideo/calciobrevi.cept3"),30,10,10,10))
            .put("230", bytes(readBinaryFile("prestel/televideo/calciosquadre.cept3"),30,10,10,10))
            .put("260", bytes(readBinaryFile("prestel/televideo/sport.cept3"),30,10,10,10))
            .put("299", bytes(readBinaryFile("prestel/televideo/sportb.cept3"),30,10,10,10))
            .put("810", bytes(readBinaryFile("prestel/televideo/motori.cept3"),30,10,10,10))
            .build(),
        "ascii", ImmutableMap.<String, byte[]> builder()
            .put("101", bytes("Ultim'ora", line))
            .put("102", bytes("24 ore", line))
            .put("110", bytes("Primo piano", line))
            .put("120", bytes("Politica", line))
            .put("130", bytes("Economia", line))
            .put("140", bytes("Dall'Italia", line))
            .put("150", bytes("Dal mondo", line))
            .put("160", bytes("Culture", line))
            .put("170", bytes("Cittadini", line))
            .put("180", bytes("Speciale", line))
            .put("190", bytes("Atlante Crisi", line))
            .put("229", bytes("Brevi calcio", line))
            .put("230", bytes("Squadre", line))
            .put("260", bytes("Altri sport", line))
            .put("299", bytes("Brevissime", line))
            .put("810", bytes("Motori", line))
            .build(),
        "ansi", ImmutableMap.<String, byte[]> builder()
            .put("101", bytes(readBinaryFile("ansi/Ultimora.ans"), noattr))
            .put("102", bytes(readBinaryFile("ansi/24hNoStop.ans"), noattr))
            .put("110", bytes(readBinaryFile("ansi/PrimoPiano.ans"), noattr))
            .put("120", bytes(readBinaryFile("ansi/Politica.ans"), noattr))
            .put("130", bytes(readBinaryFile("ansi/Economia.ans"), noattr))
            .put("140", bytes(readBinaryFile("ansi/DallItalia.ans"), noattr))
            .put("150", bytes(readBinaryFile("ansi/DalMondo.ans"), noattr))
            .put("160", bytes(readBinaryFile("ansi/Culture.ans"), noattr))
            .put("170", bytes(readBinaryFile("ansi/Cittadini.ans"), noattr))
            .put("180", bytes(readBinaryFile("ansi/Speciale.ans"), noattr))
            .put("190", bytes(readBinaryFile("ansi/AtlanteCrisi.ans"), noattr))
            .put("229", bytes(readBinaryFile("ansi/BreviCalcio.ans"), noattr))
            .put("230", bytes(readBinaryFile("ansi/CalcioSquadre.ans"), noattr))
            .put("260", bytes(readBinaryFile("ansi/AltriSport.ans"), noattr))
            .put("299", bytes(readBinaryFile("ansi/Brevissime.ans"), noattr))
            .put("810", bytes(readBinaryFile("ansi/Motori.ans"), noattr))
            .build(),
        "utf8", ImmutableMap.<String, byte[]> builder()
            .put("101", bytes(readBinaryFile("ansi/Ultimora.utf8ans"), noattr))
            .put("102", bytes(readBinaryFile("ansi/24hNoStop.utf8ans"), noattr))
            .put("110", bytes(readBinaryFile("ansi/PrimoPiano.utf8ans"), noattr))
            .put("120", bytes(readBinaryFile("ansi/Politica.utf8ans"), noattr))
            .put("130", bytes(readBinaryFile("ansi/Economia.utf8ans"), noattr))
            .put("140", bytes(readBinaryFile("ansi/DallItalia.utf8ans"), noattr))
            .put("150", bytes(readBinaryFile("ansi/DalMondo.utf8ans"), noattr))
            .put("160", bytes(readBinaryFile("ansi/Culture.utf8ans"), noattr))
            .put("170", bytes(readBinaryFile("ansi/Cittadini.utf8ans"), noattr))
            .put("180", bytes(readBinaryFile("ansi/Speciale.utf8ans"), noattr))
            .put("190", bytes(readBinaryFile("ansi/AtlanteCrisi.utf8ans"), noattr))
            .put("229", bytes(readBinaryFile("ansi/BreviCalcio.utf8ans"), noattr))
            .put("230", bytes(readBinaryFile("ansi/CalcioSquadre.utf8ans"), noattr))
            .put("260", bytes(readBinaryFile("ansi/AltriSport.utf8ans"), noattr))
            .put("299", bytes(readBinaryFile("ansi/Brevissime.utf8ans"), noattr))
            .put("810", bytes(readBinaryFile("ansi/Motori.utf8ans"), noattr))
            .build()
    );
}
