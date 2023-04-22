package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.Hidden;
import static eu.sblendorio.bbs.core.PetsciiColors.BLUE;
import static eu.sblendorio.bbs.core.PetsciiColors.CYAN;
import static eu.sblendorio.bbs.core.PetsciiColors.GREEN;
import static eu.sblendorio.bbs.core.PetsciiColors.GREY1;
import static eu.sblendorio.bbs.core.PetsciiColors.GREY2;
import static eu.sblendorio.bbs.core.PetsciiColors.GREY3;
import static eu.sblendorio.bbs.core.PetsciiColors.LIGHT_BLUE;
import static eu.sblendorio.bbs.core.PetsciiColors.LIGHT_GREEN;
import static eu.sblendorio.bbs.core.PetsciiColors.LIGHT_RED;
import static eu.sblendorio.bbs.core.PetsciiColors.PURPLE;
import static eu.sblendorio.bbs.core.PetsciiColors.RED;
import static eu.sblendorio.bbs.core.PetsciiColors.WHITE;
import static eu.sblendorio.bbs.core.PetsciiColors.YELLOW;
import eu.sblendorio.bbs.core.Utils;
import static eu.sblendorio.bbs.core.Utils.bytes;
import eu.sblendorio.bbs.tenants.ascii.RssAscii;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

//@Hidden
public class CnnPetscii extends RssPetscii {

    protected Map<String, NewsSection> sections;

    public CnnPetscii() {
        this("rss.petscii.timeout", "50000");
    }

    public CnnPetscii(String property, String defaultValue) {
        super(property, defaultValue);
        sections = loadSections();
        timeout = toLong(System.getProperty(property, defaultValue));
        logoHeightNews = 4;
    }

    @Override
    public Map<String, NewsSection> sections() {
        return sections;
    }

    @Override
    public String prefix() {
        return "http://rss.cnn.com/rss/";
    }

    @Override
    public byte[] getLogo() {
        return bytes(readBinaryFile("petscii/cnn-news.seq"), 19, 13, 13, 13);
    }

    private Map<String, NewsSection> loadSections() {
        Map<String, NewsSection> result = new LinkedHashMap<>();
        byte[] cnn = bytes(readBinaryFile("petscii/cnn.seq"),  19, 13, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, WHITE);

        result.put("101", new NewsSection(WHITE, "Top Stories", prefix() + "edition.rss", bytes(cnn, "tOP sTORIES\r\r\r")));
        result.put("102", new NewsSection(CYAN, "World", prefix() + "edition_world.rss", bytes(cnn, "wORLD\r\r\r")));
        result.put("103", new NewsSection(RED, "Africa", prefix() + "edition_africa.rss", bytes(cnn, "aFRICA\r\r\r")));
        result.put("104", new NewsSection(GREEN, "Americas", prefix() + "edition_americas.rss", bytes(cnn, "aMERICAS\r\r\r")));
        // result.put("105", new NewsSection(BLUE, "Asia", prefix() + "edition_asia.rss", bytes(cnn, "aSIA\r\r\r")));
        // result.put("106", new NewsSection(GREY2, "Europe", prefix() + "edition_europe.rss", bytes(cnn, "eUROPE\r\r\r")));
        // result.put("107", new NewsSection(LIGHT_BLUE, "Middle East", prefix() + "edition_meast.rss", bytes(cnn, "mIDDLE eAST\r\r\r")));
        result.put("108", new NewsSection(LIGHT_RED, "U.S.A.", prefix() + "edition_us.rss", bytes(cnn, "u.s.a.\r\r\r")));
        // result.put("109", new NewsSection(PURPLE, "Technology", prefix() + "edition_technology.rss", bytes(cnn, "tECHNOLOGY\r\r\r")));
        // result.put("110", new NewsSection(GREY3, "Science-Space", prefix() + "edition_space.rss", bytes(cnn, "sCIENCE & sPACE\r\r\r")));
        // result.put("111", new NewsSection(LIGHT_RED, "Entertainment", prefix() + "edition_entertainment.rss", bytes(cnn, "eNTERTAINMENT\r\r\r")));
        // result.put("112", new NewsSection(LIGHT_GREEN, "Money", prefix() + "money_news_international.rss", bytes(cnn, "mONEY\r\r\r")));
        // result.put("113", new NewsSection(YELLOW, "World Sport", prefix() + "edition_sport.rss", bytes(cnn, "wORLD sPORT\r\r\r")));
        // result.put("114", new NewsSection(GREEN, "Football", prefix() + "edition_football.rss", bytes(cnn, "fOOTBALL\r\r\r")));
        // result.put("115", new NewsSection(GREY1, "Travel", prefix() + "edition_travel.rss", bytes(cnn, "tRAVEL\r\r\r")));
        result.put("116", new NewsSection(GREY2, "Most Recent", prefix() + "cnn_latest.rss", bytes(cnn, "mOST rECENT\r\r\r")));
        return result;
    }

}
