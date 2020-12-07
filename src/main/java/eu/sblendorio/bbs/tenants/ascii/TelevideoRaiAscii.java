package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.Hidden;
import static eu.sblendorio.bbs.core.Utils.bytes;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

@Hidden
public class TelevideoRaiAscii extends RssAscii {

    protected Map<String, NewsSection> sections;

    public TelevideoRaiAscii() {
        super();
        sections = loadSections();
        this.pageRows = 19;
        timeout = toLong(System.getProperty("rss.a1.timeout", "40000"));
    }

    @Override
    public byte[] getLogo() {
        return bytes("Televideo");
    }

    @Override
    public String prefix() {
        return "http://www.servizitelevideo.rai.it/televideo/pub/";
    }

    private Map<String, NewsSection> loadSections() {
        Map<String, NewsSection> result = new LinkedHashMap<>();
        result.put("101", new NewsSection("Ultim'Ora", prefix() + "rss101.xml", bytes("Televideo - Ultim'ora")));
        result.put("102", new NewsSection("24h No Stop", prefix() + "rss102.xml", bytes("Televideo - No stop - 24 ore")));
        result.put("110", new NewsSection("Primo Piano", prefix() + "rss110.xml", bytes("Televideo - Primo piano")));
        result.put("120", new NewsSection("Politica", prefix() + "rss120.xml", bytes("Televideo - Politica")));
        result.put("130", new NewsSection("Economia", prefix() + "rss130.xml", bytes("Televideo - Economia")));
        result.put("140", new NewsSection("Dall'Italia", prefix() + "rss140.xml", bytes("Televideo - Dall'Italia")));
        result.put("150", new NewsSection("Dal Mondo", prefix() + "rss150.xml", bytes("Televideo - Dal mondo")));
        result.put("160", new NewsSection("Culture", prefix() + "rss160.xml", bytes("Televideo - Culture")));
        result.put("170", new NewsSection("Cittadini", prefix() + "rss170.xml", bytes("Televideo - Cittadini")));
        result.put("180", new NewsSection("Speciale", prefix() + "rss180.xml", bytes("Televideo - Speciale")));
        result.put("190", new NewsSection("Atlante Crisi", prefix() + "rss190.xml", bytes("Televideo - Atlante crisi")));
        result.put("229", new NewsSection("Brevi Calcio", prefix() + "rss229.xml", bytes("Televideo - Brevi calcio")));
        result.put("230", new NewsSection("CalcioSquadre", prefix() + "rss230.xml", bytes("Televideo - Calcio - squadre")));
        result.put("260", new NewsSection("Altri Sport", prefix() + "rss260.xml", bytes("Televideo - Altri sport")));
        result.put("299", new NewsSection("Brevissime", prefix() + "rss299.xml", bytes("Televideo - Sport - brevissime")));
        result.put("810", new NewsSection("Motori", prefix() + "rss810.xml", bytes("Televideo - Motori")));
        return result;
    }

    @Override
    public Map<String, NewsSection> sections() {
        return sections;
    }

}
