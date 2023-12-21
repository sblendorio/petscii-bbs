package eu.sblendorio.bbs.tenants.petscii;

import static eu.sblendorio.bbs.core.PetsciiColors.BLUE;
import static eu.sblendorio.bbs.core.PetsciiColors.CYAN;
import static eu.sblendorio.bbs.core.PetsciiColors.GREEN;
import static eu.sblendorio.bbs.core.PetsciiColors.GREY2;
import static eu.sblendorio.bbs.core.PetsciiColors.GREY3;
import static eu.sblendorio.bbs.core.PetsciiColors.LIGHT_BLUE;
import static eu.sblendorio.bbs.core.PetsciiColors.LIGHT_GREEN;
import static eu.sblendorio.bbs.core.PetsciiColors.LIGHT_RED;
import static eu.sblendorio.bbs.core.PetsciiColors.PURPLE;
import static eu.sblendorio.bbs.core.PetsciiColors.RED;
import static eu.sblendorio.bbs.core.PetsciiColors.WHITE;
import static eu.sblendorio.bbs.core.PetsciiColors.YELLOW;
import static eu.sblendorio.bbs.core.Utils.bytes;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

//@Hidden
public class BbcPetscii extends RssPetscii {

    protected Map<String, NewsSection> sections;

    public BbcPetscii() {
        this("rss.petscii.timeout", "50000");
    }

    public BbcPetscii(String property, String defaultValue) {
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
        return "https://feeds.bbci.co.uk/news/";
    }

    @Override
    public byte[] getLogo() {
        return bytes(readBinaryFile("petscii/bbc-news.seq"), 19, 13, 13, 13);
    }

    private Map<String, NewsSection> loadSections() {
        Map<String, NewsSection> result = new LinkedHashMap<>();
        byte[] cnn = bytes(readBinaryFile("petscii/bbc.seq"),  19, 13, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, GREY2);

        result.put("201", new NewsSection(WHITE, "Top Stories", prefix() + "rss.xml", bytes(cnn, "tOP sTORIES\r\r\r")));
        result.put("202", new NewsSection(CYAN, "World", prefix() + "world/rss.xml", bytes(cnn, "wORLD\r\r\r")));
        result.put("203", new NewsSection(RED, "U.K.", prefix() + "uk/rss.xml", bytes(cnn, "u.k.\r\r\r")));
        result.put("204", new NewsSection(GREEN, "Business", prefix() + "business/rss.xml", bytes(cnn, "bUSINESS\r\r\r")));
        result.put("205", new NewsSection(BLUE, "Politics", prefix() + "politics/rss.xml", bytes(cnn, "pOLITICS\r\r\r")));
        result.put("206", new NewsSection(GREY2, "Health", prefix() + "health/rss.xml", bytes(cnn, "hEALTH\r\r\r")));
        result.put("207", new NewsSection(LIGHT_BLUE, "Education", prefix() + "education/rss.xml", bytes(cnn, "eDUCATION\r\r\r")));
        result.put("208", new NewsSection(LIGHT_RED, "Science", prefix() + "science_and_environment/rss.xml", bytes(cnn, "sCIENCE\r\r\r")));
        result.put("209", new NewsSection(PURPLE, "Technology", prefix() + "technology/rss.xml", bytes(cnn, "tECHNOLOGY\r\r\r")));
        result.put("210", new NewsSection(GREY3, "Entertain.", prefix() + "entertainment_and_arts/rss.xml", bytes(cnn, "eNTERTAINMENT\r\r\r")));
        result.put("211", new NewsSection(LIGHT_RED, "Africa", prefix() + "world/africa/rss.xml", bytes(cnn, "aFRICA\r\r\r")));
        result.put("212", new NewsSection(LIGHT_GREEN, "Asia", prefix() + "world/asia/rss.xml", bytes(cnn, "aSIA\r\r\r")));
        result.put("213", new NewsSection(YELLOW, "Europe", prefix() + "world/europe/rss.xml", bytes(cnn, "eUROPE\r\r\r")));
        result.put("214", new NewsSection(GREEN, "Middle East", prefix() + "world/middle_east/rss.xml", bytes(cnn, "mIDDLE eAST\r\r\r")));
        result.put("215", new NewsSection(GREY2, "US & Canada", prefix() + "world/us_and_canada/rss.xml", bytes(cnn, "us & cANADA\r\r\r")));
        result.put("216", new NewsSection(WHITE, "Latin America", prefix() + "world/latin_america/rss.xml", bytes(cnn, "lATIN aMERICA\r\r\r")));
        result.put("217", new NewsSection(CYAN, "England", prefix() + "england/rss.xml", bytes(cnn, "eNGLAND\r\r\r")));
        result.put("218", new NewsSection(RED, "North.Ireland", prefix() + "northern_ireland/rss.xml", bytes(cnn, "nORTHERN iRELAND\r\r\r")));
        result.put("219", new NewsSection(GREEN, "Scotland", prefix() + "scotland/rss.xml", bytes(cnn, "sCOTLAND\r\r\r")));
        result.put("220", new NewsSection(BLUE, "Wales", prefix() + "wales/rss.xml", bytes(cnn, "wALES\r\r\r")));
        return result;
    }

}
