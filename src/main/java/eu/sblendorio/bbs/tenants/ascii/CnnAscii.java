package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.Hidden;
import static eu.sblendorio.bbs.core.Utils.bytes;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

@Hidden
public class CnnAscii extends RssAscii {

    protected Map<String, NewsSection> sections;

    public CnnAscii() {
        this("rss.a1.timeout", "40000");
    }

    public CnnAscii(String property, String defaultValue) {
        super(property, defaultValue);
        sections = loadSections();
        timeout = toLong(System.getProperty(property, defaultValue));
    }

    @Override
    public byte[] getLogo() {
        return bytes("CNN News\r\n--------\r\n\r\n");
    }

    @Override
    public String prefix() {
        return "http://rss.cnn.com/rss/";
    }

    private Map<String, NewsSection> loadSections() {
        Map<String, NewsSection> result = new LinkedHashMap<>();
        result.put("101", new NewsSection("Top Stories", prefix() + "edition.rss", bytes("CNN - Top Stories\r\n\r\n")));
        result.put("102", new NewsSection("World", prefix() + "edition_world.rss", bytes("CNN - World\r\n\r\n")));
        result.put("103", new NewsSection("Africa", prefix() + "edition_africa.rss", bytes("CNN - Africa\r\n\r\n")));
        result.put("104", new NewsSection("Americas", prefix() + "edition_americas.rss", bytes("CNN - Americas\r\n\r\n")));
        result.put("105", new NewsSection("Asia", prefix() + "edition_asia.rss", bytes("CNN - Asia\r\n\r\n")));
        result.put("106", new NewsSection("Europe", prefix() + "edition_europe.rss", bytes("CNN - Europe\r\n\r\n")));
        result.put("107", new NewsSection("Middle East", prefix() + "edition_meast.rss", bytes("CNN - Middle East\r\n\r\n")));
        result.put("108", new NewsSection("U.S.A.", prefix() + "edition_us.rss", bytes("CNN - U.S.A.\r\n\r\n")));
        result.put("109", new NewsSection("Technology", prefix() + "edition_technology.rss", bytes("CNN - Technology\r\n\r\n")));
        result.put("110", new NewsSection("Science-Space", prefix() + "edition_space.rss", bytes("CNN - Science & Space\r\n\r\n")));
        result.put("111", new NewsSection("Entertainment", prefix() + "edition_entertainment.rss", bytes("CNN - Entertainment\r\n\r\n")));
        result.put("112", new NewsSection("Money", prefix() + "money_news_international.rss", bytes("CNN - Money\r\n\r\n")));
        result.put("113", new NewsSection("World Sport", prefix() + "edition_sport.rss", bytes("CNN - World Sport\r\n\r\n")));
        result.put("114", new NewsSection("Football", prefix() + "edition_football.rss", bytes("CNN - Football\r\n\r\n")));
        result.put("115", new NewsSection("Travel", prefix() + "edition_travel.rss", bytes("CNN - Travel\r\n\r\n")));
        result.put("116", new NewsSection("Most Recent", prefix() + "cnn_latest.rss", bytes("CNN - Most Recent\r\n\r\n")));
        return result;
    }

    @Override
    public Map<String, NewsSection> sections() {
        return sections;
    }

}
