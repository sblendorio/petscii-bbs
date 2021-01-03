package eu.sblendorio.bbs.tenants.ascii;

import com.google.common.collect.ImmutableMap;
import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.core.Utils;
import static eu.sblendorio.bbs.core.Utils.bytes;
import java.nio.charset.StandardCharsets;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

@Hidden
public class TelevideoRaiAscii extends RssAscii {

    protected Map<String, NewsSection> sections;
    protected String type;

    public TelevideoRaiAscii() {
        this("rss.a1.timeout", "40000", "ascii");
    }

    public TelevideoRaiAscii(String property, String defaultValue, String interfaceType) {
        super(property, defaultValue);
        type = interfaceType;
        sections = loadSections();
        timeout = toLong(System.getProperty(property, defaultValue));
        logoHeightMenu = logoHeightsMenu.get(interfaceType);
        logoHeightNews = logoHeightsNews.get(interfaceType);
        hrDash = hrDashes.get(interfaceType);
    }

    @Override
    public byte[] getLogo() {
        return logo.get(type);
    }

    @Override
    public String prefix() {
        return "http://www.servizitelevideo.rai.it/televideo/pub/";
    }

    private Map<String, NewsSection> loadSections() {
        Map<String, NewsSection> result = new LinkedHashMap<>();
        result.put("101", new NewsSection("Ultim'Ora", prefix() + "rss101.xml", logos.get(type).get("101")));
        result.put("102", new NewsSection("24h No Stop", prefix() + "rss102.xml", logos.get(type).get("102")));
        result.put("110", new NewsSection("Primo Piano", prefix() + "rss110.xml", logos.get(type).get("110")));
        result.put("120", new NewsSection("Politica", prefix() + "rss120.xml", logos.get(type).get("120")));
        result.put("130", new NewsSection("Economia", prefix() + "rss130.xml", logos.get(type).get("130")));
        result.put("140", new NewsSection("Dall'Italia", prefix() + "rss140.xml", logos.get(type).get("140")));
        result.put("150", new NewsSection("Dal Mondo", prefix() + "rss150.xml", logos.get(type).get("150")));
        result.put("160", new NewsSection("Culture", prefix() + "rss160.xml", logos.get(type).get("160")));
        result.put("170", new NewsSection("Cittadini", prefix() + "rss170.xml", logos.get(type).get("170")));
        result.put("180", new NewsSection("Speciale", prefix() + "rss180.xml", logos.get(type).get("180")));
        result.put("190", new NewsSection("Atlante Crisi", prefix() + "rss190.xml", logos.get(type).get("190")));
        result.put("229", new NewsSection("Brevi Calcio", prefix() + "rss229.xml", logos.get(type).get("229")));
        result.put("230", new NewsSection("CalcioSquadre", prefix() + "rss230.xml", logos.get(type).get("230")));
        result.put("260", new NewsSection("Altri Sport", prefix() + "rss260.xml", logos.get(type).get("260")));
        result.put("299", new NewsSection("Brevissime", prefix() + "rss299.xml", logos.get(type).get("299")));
        result.put("810", new NewsSection("Motori", prefix() + "rss810.xml", logos.get(type).get("810")));
        return result;
    }

    @Override
    public Map<String, NewsSection> sections() {
        return sections;
    }

    public static byte[] line = new byte[] {13, 10, 13, 10};
    public static byte[] noattr = "\033[0m".getBytes(ISO_8859_1);

    public Map<String, byte[]> logo = ImmutableMap.of(
      "ascii", bytes("Televideo\r\n---------", line),
      "ansi", bytes(readBinaryFile("ansi/Televideo.ans"), noattr),
      "utf8", bytes(readBinaryFile("ansi/Televideo.utf8ans"), noattr)
    );

    public Map<String, Integer> logoHeightsMenu = ImmutableMap.of(
        "ascii", 3,
        "ansi", 4,
        "utf8", 4
    );

    public Map<String, Integer> logoHeightsNews = ImmutableMap.of(
        "ascii", 2,
        "ansi", 4,
        "utf8", 4
    );

    public Map<String, byte[]> hrDashes = ImmutableMap.of(
        "ascii", "-".getBytes(ISO_8859_1),
        "ansi", bytes(196),
        "utf8",  "\u2500".getBytes(UTF_8)
    );

    public Map<String, Map<String, byte[]>> logos = ImmutableMap.of(
        "ascii", ImmutableMap.<String, byte[]> builder()
            .put("101", bytes("Televideo - Ultim'ora", line))
            .put("102", bytes("Televideo - 24 ore", line))
            .put("110", bytes("Televideo-Primo piano", line))
            .put("120", bytes("Televideo - Politica", line))
            .put("130", bytes("Televideo - Economia", line))
            .put("140", bytes("Televideo-Dall'Italia", line))
            .put("150", bytes("Televideo - Dal mondo", line))
            .put("160", bytes("Televideo - Culture", line))
            .put("170", bytes("Televideo - Cittadini", line))
            .put("180", bytes("Televideo - Speciale", line))
            .put("190", bytes("Televideo-AtlanteCrisi", line))
            .put("229", bytes("Televideo-Brevi calcio", line))
            .put("230", bytes("Televideo - Squadre", line))
            .put("260", bytes("Televideo-Altri sport", line))
            .put("299", bytes("Televideo - Brevissime", line))
            .put("810", bytes("Televideo - Motori", line))
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
