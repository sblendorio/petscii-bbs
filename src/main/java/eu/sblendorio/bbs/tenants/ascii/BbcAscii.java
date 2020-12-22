package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.Hidden;
import static eu.sblendorio.bbs.core.Utils.bytes;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

@Hidden
public class BbcAscii extends RssAscii {

    protected Map<String, NewsSection> sections;

    public BbcAscii() {
        this("rss.a1.timeout", "40000");
    }

    public BbcAscii(String property, String defaultValue) {
        super(property, defaultValue);
        sections = loadSections();
        timeout = toLong(System.getProperty(property, defaultValue));
    }

    public static byte[] line = new byte[] {13, 10, 13, 10};

    @Override
    public byte[] getLogo() {
        return bytes("BBC News\r\n--------", line);
    }

    @Override
    public String prefix() {
        return "http://feeds.bbci.co.uk/news/";
    }

    private Map<String, NewsSection> loadSections() {
        Map<String, NewsSection> result = new LinkedHashMap<>();
        result.put("201", new NewsSection("Top Stories", prefix() + "rss.xml", bytes("BBC - Top Stories", line)));
        result.put("202", new NewsSection("World", prefix() + "world/rss.xml", bytes("BBC - World", line)));
        result.put("203", new NewsSection("U.K.", prefix() + "uk/rss.xml", bytes("BBC - United Kingdom", line)));
        result.put("204", new NewsSection("Business", prefix() + "business/rss.xml", bytes("BBC - Business", line)));
        result.put("205", new NewsSection("Politics", prefix() + "politics/rss.xml", bytes("BBC - Politics", line)));
        result.put("206", new NewsSection("Health", prefix() + "health/rss.xml", bytes("BBC - Health", line)));
        result.put("207", new NewsSection("Education", prefix() + "education/rss.xml", bytes("BBC - Education", line)));
        result.put("208", new NewsSection("Science", prefix() + "science_and_environment/rss.xml", bytes("BBC - Science & Environment", line)));
        result.put("209", new NewsSection("Technology", prefix() + "technology/rss.xml", bytes("BBC - Technology", line)));
        result.put("210", new NewsSection("Entertain.", prefix() + "entertainment_and_arts/rss.xml", bytes("BBC - Entertainment & Arts", line)));
        result.put("211", new NewsSection("Africa", prefix() + "world/africa/rss.xml", bytes("BBC - Africa", line)));
        result.put("212", new NewsSection("Asia", prefix() + "world/asia/rss.xml", bytes("BBC - Asia", line)));
        result.put("213", new NewsSection("Europe", prefix() + "world/europe/rss.xml", bytes("BBC - Europe", line)));
        result.put("214", new NewsSection("Middle East", prefix() + "world/middle_east/rss.xml", bytes("BBC - Middle East", line)));
        result.put("215", new NewsSection("US & Canada", prefix() + "world/us_and_canada/rss.xml", bytes("BBC - US & Canada", line)));
        result.put("216", new NewsSection("Latin America", prefix() + "world/latin_america/rss.xml", bytes("BBC - Latin America", line)));
        result.put("217", new NewsSection("England", prefix() + "england/rss.xml", bytes("BBC - England", line)));
        result.put("218", new NewsSection("North. Ireland", prefix() + "northern_ireland/rss.xml", bytes("BBC - Northern Ireland", line)));
        result.put("219", new NewsSection("Scotland", prefix() + "scotland/rss.xml", bytes("BBC - Scotland", line)));
        result.put("220", new NewsSection("Wales", prefix() + "wales/rss.xml", bytes("BBC - Wales", line)));
        return result;
    }

    @Override
    public Map<String, NewsSection> sections() {
        return sections;
    }

}
