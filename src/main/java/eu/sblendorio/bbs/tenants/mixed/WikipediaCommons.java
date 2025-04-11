package eu.sblendorio.bbs.tenants.mixed;

import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.HtmlUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultString;

public class WikipediaCommons {
    private static Logger logger = LogManager.getLogger(WikipediaCommons.class);
    private static final int LIMIT_SEARCH = 100;
    private static final String URL_GET_BY_PAGEID = "https://${LANG}.wikipedia.org/w/api.php?format=json&action=parse&prop=text&pageid=${PAGEID}";
    private static final String URL_SEARCH_BY_KEYWORDS = "https://${LANG}.wikipedia.org/w/api.php?format=json&action=query&list=search&srsearch=${KEYWORDS}&sroffset=${OFFSET}&srlimit=${LIMIT}";
    private static final String URL_PICK_RANDOM = "https://${LANG}.wikipedia.org/w/api.php?action=query&format=json&list=random&rnnamespace=0";

    public static String getSearchUrl(String lang, String keywords, Long offset, Long limit) {
        try {
            return URL_SEARCH_BY_KEYWORDS.replace("${LANG}", lang.replaceAll("[^a-zA-Z0-9]", "")).replace("${KEYWORDS}", URLEncoder.encode(keywords, "UTF-8")).replace("${OFFSET}", String.valueOf(offset)).replace("${LIMIT}", String.valueOf(limit));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getPageIdUrl(String lang, Long pageId) {
        return URL_GET_BY_PAGEID.replace("${LANG}", lang.replaceAll("[^a-zA-Z0-9]", "").toLowerCase()).replace("${PAGEID}", String.valueOf(pageId));
    }

    public static String getRandomUrl(String lang) {
        return URL_PICK_RANDOM.replace("${LANG}", lang.replaceAll("[^a-zA-Z0-9]", "").toLowerCase());
    }

    public static class WikipediaItem {
        public String lang;
        public Long pageid;
        public String title;
        public Long size;
        public Long wordcount;
        public String snippet;
        public String timestamp;
        public Long ns;

        @Override
        public String toString() {
            return "[lang:"+lang+", pageid:"+pageid+", title:<"+title+">]";
        }
    }

    public static String getTextContent(WikipediaItem item) throws IOException, ParseException {
        logger.info("Wikipedia.getTextContent: " + item);
        return getTextContent(item.lang, item.pageid);
    }

    public static String getTextContent(String lang, Long pageid) throws IOException, ParseException {
        String html = getHtmlContent(lang, pageid);
        String step1 = HtmlUtils.utilHtmlClean(html).replaceAll("(?is) +", " ")
                .replaceAll("(?is)^[\\s\\n\\r]+|^\\s*(</?(br|div|figure|iframe|img|p|h[0-9])[^>]*>\\s*)+", EMPTY)
                .replaceAll("(?is)^(<[^>]+>(\\s|\n|\r|\u00a0)*)+", EMPTY)
                .replaceAll("(?is)^(<[^>]+>(\\s|\n|\r|\u00a0)*)+", EMPTY);
        return step1;
    }

    public static String getHtmlContent(WikipediaItem item) throws IOException, ParseException {
        return getHtmlContent(item.lang, item.pageid);
    }

    public static String getHtmlContent(String lang, Long pageid) throws IOException, ParseException {
        String url = getPageIdUrl(lang, pageid);
        JSONObject root = (JSONObject) BbsThread.httpGetJson(url);
        JSONObject parse = (JSONObject) root.get("parse");
        JSONObject text = (JSONObject) parse.get("text");
        String result = (String) text.get("*");

        System.out.println("RESULT: " + result);

        Document doc = Jsoup.parse(result);


        doc.select(".infobox").remove();
        doc.select(".mw-editsection").remove();
        doc.select(".hatnote").remove();
        doc.select(".catlinks").remove();
        doc.select(".noprint").remove();
        doc.select(".metadata").remove();
        doc.select(".mw-empty-elt").remove();
        doc.select(".toc").remove();
        doc.select("style").remove();
        doc.select("script").remove();
        doc.select("figure").remove();
        doc.select("*[style*=display:none]").remove();
        doc.select("span[data-ooui]").remove();
        removeComments(doc);

        return doc.html();
    }

    public static List<WikipediaItem> search(String lang, String keywords) throws IOException, ParseException {
        return search(lang, keywords, false);
    }

    public static List<WikipediaItem> searchFirst(String lang, String keywords) throws IOException, ParseException {
        return search(lang, keywords, true);
    }

    public static List<WikipediaItem> pickRandomPage(String lang) throws IOException, ParseException {
        String url = getRandomUrl(lang);
        JSONObject root = (JSONObject) BbsThread.httpGetJson(url);
        JSONObject query = (JSONObject) root.get("query");
        JSONArray random = (JSONArray) query.get("random");
        JSONObject first = (JSONObject) random.get(0);
        Long pageid = (Long) first.get("id");
        String title = (String) first.get("title");
        WikipediaItem item = new WikipediaItem();
        item.lang = lang;
        item.pageid = pageid;
        item.title = title;
        return asList(item);
    }
    public static List<WikipediaItem> search(String lang, String keywords, boolean onlyFirst) throws IOException, ParseException {
        logger.info("Wikipedia.{} search for '{}'", lang, keywords);
        Long offset = 0L;
        Long limit = onlyFirst ? 1L : 50L;
        List<WikipediaItem> result = new ArrayList<>();
        if (StringUtils.isBlank(keywords))
            return result;
        int count = 0;
        JSONObject continues;
        do {
            String url = getSearchUrl(lang, keywords, offset, limit);
            JSONObject root = (JSONObject) BbsThread.httpGetJson(url);
            continues = (JSONObject) root.get("continue");
            JSONObject query = (JSONObject) root.get("query");
            if (query == null) break;

            JSONArray search = (JSONArray) query.get("search");
            for (Object obj : search) {
                JSONObject itemJ = (JSONObject) obj;
                WikipediaItem item = new WikipediaItem();
                item.lang = defaultString(lang).replace("[^a-zA-Z0-9]", "").toLowerCase();
                item.ns = itemJ.get("ns") == null ? -1 : (Long) (itemJ.get("ns"));
                item.wordcount = itemJ.get("wordcount") == null ? -1 : (Long) (itemJ.get("wordcount"));
                item.size = itemJ.get("size") == null ? -1 : (Long) (itemJ.get("size"));
                item.pageid = itemJ.get("pageid") == null ? -1 : (Long) (itemJ.get("pageid"));
                item.title = defaultString((String) (itemJ.get("title")));
                item.snippet = defaultString((String) (itemJ.get("snippet")));
                item.timestamp = defaultString((String) (itemJ.get("timestamp")));
                result.add(item);
                count++;
                if (LIMIT_SEARCH > 0 && count >= LIMIT_SEARCH) break;
            }
            if (continues != null) {
                offset = (Long) (continues.get("sroffset"));
            }
        } while ((count < LIMIT_SEARCH || LIMIT_SEARCH == 0) && continues != null && !onlyFirst);
        return result;
    }

    /*

https://en.wikipedia.org/w/api.php?format=json&action=parse&prop=text&pageid=909036
https://it.wikipedia.org/w/api.php?format=json&action=parse&prop=text&pageid=8904051

https://en.wikipedia.org/w/api.php?format=json&action=query&list=search&srsearch=Maccio%20Capatonda
https://en.wikipedia.org/w/api.php?format=json&action=query&list=search&srsearch=Maccio%20Capatonda&sroffset=18
https://en.wikipedia.org/w/api.php?format=json&action=query&list=search&srsearch=Maccio%20Capatonda&sroffset=0&srlimit=2

https://it.wikipedia.org/w/api.php?format=json&action=parse&prop=text&page=Maccio_Capatonda

 */

    public static void main(String[] args) throws IOException, ParseException {
        System.out.println("INIZIO");
        WikipediaItem item = new WikipediaItem();
        item.pageid = 6006689L;
        item.lang = "it";
        String text = getHtmlContent(item);
        Document doc = Jsoup.parse(text);
        doc.select(".infobox").remove();
        doc.select(".mw-editsection").remove();
        doc.select(".hatnote").remove();
        doc.select(".catlinks").remove();
        doc.select(".noprint").remove();
        doc.select(".toc").remove();
        doc.select("style").remove();
        doc.select("figure").remove();
        doc.select("*[style*=display:none]").remove();
        removeComments(doc);
        System.out.println(doc.body().html());
    }

    private static void removeComments(Node node) {
        node.childNodes().stream().filter(n -> "#comment".equals(n.nodeName())).forEach(Node::remove);
        node.childNodes().forEach(WikipediaCommons::removeComments);
    }

    public static List<String> wordWrap(String s, BbsThread bbsThread) {
        String[] cleaned = bbsThread.filterPrintableWithNewline(s).split("\n");
        List<String> result = new ArrayList<>();
        for (String item: cleaned) {
            String[] wrappedLine = WordUtils
                    .wrap(item, bbsThread.getScreenColumns() - 1, "\n", true)
                    .split("\n");
            result.addAll(asList(wrappedLine));
        }
        return result;
    }

    public static Set<String> LATIN_ALPHAPET_LANGS = new HashSet<>(Arrays.asList(
            "en", "de", "fr", "es", "pt", "it", "pl", "nl", "id", "tr", "cs", "sv", "vi", "fi", "hu", "ca", "simple",
            "no", "ro", "da", "eu", "az", "ms", "sk", "et", "hr", "sl", "lt", "lv", "eo", "gl", "sq", "ha", "af", "sh",
            "bs", "nn", "is", "ig", "ceb", "tl", "sw", "la", "cy", "ast", "jv", "als", "ht", "br", "oc", "zh-min-nan",
            "lb", "sco", "yo", "an", "ga", "fy", "war", "mg", "io", "so", "min", "cv", "kcg", "bcl", "fo", "ban", "su",
            "szl", "qu", "kaa", "lmo", "ang", "scn", "bar", "mt", "ace", "hif", "mad", "pms", "tw", "vec", "dag", "nds",
            "ia", "rw", "co", "tn", "om", "gd", "ie", "diq", "zu", "crh", "wa", "kw", "ff", "hak", "li", "nap", "pcd",
            "sc", "tk", "vep", "lld", "frp", "gor", "ilo", "tum", "vo", "kab", "rm", "sn", "gn", "lij", "mwl", "vls",
            "bjn", "pam", "avk", "zea", "bat-smg", "ay", "kbp", "lfn", "hsb", "gv", "szy", "nds-nl", "lad", "ami",
            "frr", "tly", "ext", "map-bms", "cbk-zam", "cdo", "fon", "gur", "smn", "rn", "nov", "eml", "gpe", "csb",
            "olo", "nah", "st", "guc", "xh", "bi", "mi", "trv", "fiu-vro", "bm", "wo", "roa-rup", "tay", "chy", "ee",
            "gag", "haw", "ln", "nia", "pdc", "rmy", "pcm", "se", "ny", "jam", "pap", "ksh", "dsb", "pih", "sm",
            "roa-tara", "tpi", "ik", "kg", "nv", "to", "ch", "kl", "lg", "nso", "stq", "ty", "za", "fat", "fur", "ki",
            "ve", "din", "fj", "guw", "atj", "ltg", "jbo", "nrm", "ts", "pag", "sg", "ss", "tet", "gcr", "pwn", "srn",
            "pfl"));

    public static String[] WIKILOGO = new String[] {
            "00000000000000000000000000110000000011000000000000",
            "00000000000000000000000000110000000111100000000000",
            "00000000010000000000000001111000001111111000000000",
            "00000000110000000000011111111111101111111100000000",
            "00000001100000000000011111101111110011111110000000",
            "00000011000000000000011110010011111101111111000000",
            "00000110000000000000000110111011111101111110100000",
            "00000110000000000000000011110110111011111101100000",
            "00001011000000000000000011101100110111111101110000",
            "00011011110000000000000111011110110110011101111000",
            "00000011110000000000011111111111111001101101111000",
            "00111011000000000000011111100111111011110011111100",
            "00111110000000000000001111011011000011111110011100",
            "00111110000000000000001111011100111101111110101100",
            "01100000000000000000000000111111111101110110101110",
            "00011011111000000111101111111111111110110111001110",
            "01111011111000000111101101101011011110111011001110",
            "11111011111100001111101101101011011001111100011110",
            "11111011111111111111011110110110110111111111111111",
            "11100111111000011110111110101010110111111110011111",
            "11011111110111101110111111011101111001111101101100",
            "11011111110111101110111111011101111110111101110011",
            "11100111110111101111001111111111111110110011111111",
            "11111011111011011111110111110000111110001111111111",
            "11111011100011000111110111101110111000111110110111",
            "11111011111111111111110111101111000110111101010111",
            "00000000000011111000000011101111111110111110110111",
            "01111011111101110111111000011111110110111111110110",
            "01111011111101110111111011111011100111001100010110",
            "01111011111110001110111011111011010111110110110110",
            "01111011111110111101111011111011011011110110110100",
            "00111100011101011011011100011100111011001111110100",
            "00111111101110111000011111101101111011011111111100",
            "00011111101101011010111111101101111111011111111000",
            "00011110011100011000011110011111000111011111000000",
            "00001101111110111010111101111110111011101100110000",
            "00000101111010101000011101111110111011100011100000",
            "00000101111111111111111000000001111100001111100000",
            "00000000000000111110000111101111111111101111000000",
            "00000001101111011101111111101111000111101110000000",
            "00000000101111011101111111101111110111110100000000",
            "00000000001111100011111110011111110111110000000000",
            "00000000000111111110001101111111101111100000000000",
            "00000000000011111111101101111111101111000000000000",
            "00000000000000111100001110011111111100000000000000",
            "00000000000000000111111111101111100000000000000000",
            "00000000000000000000111111101100000000000000000000"
    };


    public static String[] WIKILOGO_SHADOW = new String[] {
            "......................",
            "..............**......",
            ".............****.....",
            ".............****.....",
            "..............**......",
            "...........*********..",
            ".........***********..",
            "........*************.",
            "............****......",
            "............****......",
            ".............**......."
    };

    public static final String[] WIKILOGO_2 = new String[] {
            "****..***..***..****",
            "****...*....*...****",
            ".**....**..**....**.",
            ".**....**..**....**.",
            "..**....****....**..",
            "..**....****....**..",
            "...**....**....**...",
            "...**....**....**...",
            "....**..****..**....",
            "....**..****..**....",
            ".....****..****.....",
            ".....****..****.....",
            "......**....**......"
    };

    public static final String[] WIKILOGO_3 = new String[] {
            "****..***..***..****",
            "****...*....*...****",
            ".**....**..**....**.",
            ".**....**..**....**.",
            "..**....****....**..",
            "..**....****....**..",
            "...**....**....**...",
            "...**....**....**...",
            "....**..****..**....",
            "....**..****..**....",
            ".....****..****.....",
            ".....****..****.....",
            "......**....**......",
            "....................",
            ".......******.......",
            ".........**.........",
            ".........**.........",
            ".........**.........",
            ".........**.........",
            ".......******.......",
            "....................",
            ".......***..**......",
            "........**.**.......",
            "........****........",
            "........**.**.......",
            "........**..**......",
            ".......***..**......",
            "....................",
            ".......******.......",
            ".........**.........",
            ".........**.........",
            ".........**.........",
            ".........**.........",
            ".......******.......",
            "....................",
            ".......******.......",
            "........**..**......",
            "........**..**......",
            "........*****.......",
            "........**..........",
            ".......****.........",
            "....................",
            ".......******.......",
            "........**..........",
            "........****........",
            "........**..........",
            "........**..........",
            ".......******.......",
            "....................",
            ".......*****........",
            "........**.**.......",
            "........**..**......",
            "........**..**......",
            "........**.**.......",
            ".......*****........",
            "....................",
            ".......******.......",
            ".........**.........",
            ".........**.........",
            ".........**.........",
            ".........**.........",
            ".......******.......",
            "....................",
            "........****........",
            ".......**..**.......",
            "......**....**......",
            "..... ********......",
            "......**....**......",
            "......**....**......",
            "......**....**......",

    };

    public static final String[] WIKI_VERTICAL = new String[] {
            "000000000000001",
            "000000000000011",
            "000000000001101",
            "000000000110100",
            "000000011000100",
            "000000000110100",
            "000000000001101",
            "000000000000011",
            "000000000000001",
            "000000000000000",
            "000000000010001",
            "000000000011111",
            "000000000010001",
            "000000000000000",
            "000000000001110",
            "000000000010001",
            "000000000010001",
            "000000000011111",
            "000000000010001",
            "000000000000000",
            "000000000010001",
            "000000000010101",
            "000100010010101",
            "000111110011111",
            "000100010010001",
            "000000000000000",
            "000100010001000",
            "000110110010100",
            "000001000010101",
            "000111110011111",
            "000100010010001",
            "000000000000000",
            "000100010000000",
            "000111110000000",
            "100100010000000",
            "110000000000000",
            "101100000000000",
            "000011000000000",
            "100000110000000",
            "110011000000000",
            "001100000000000",
            "110011000000000",
            "100000110000000",
            "000011000000000",
            "101100000000000",
            "110000000000000",
            "100000000000000"
    };

    public static String[] WIKI_VERTICAL_2 = new String[] {
            "000000000000001",
            "000000000000011",
            "000000000001101",
            "000000000110100",
            "000000011000100",
            "000000000110100",
            "000000000001101",
            "000000000000011",
            "000000000000001",
            "000000000000000",
            "000000000010001",
            "000000000011111",
            "000100010010001",
            "000111110000000",
            "000100010001110",
            "000000000010001",
            "000100010010001",
            "000110110011111",
            "000001000010001",
            "000111110000000",
            "000100010010001",
            "000000000010101",
            "000100010010101",
            "000111110011111",
            "100100010010001",
            "110000000000000",
            "101100000001000",
            "000011000010100",
            "100000110010101",
            "110011000011111",
            "001100000010001",
            "110011000000000",
            "100000110000000",
            "000011000000000",
            "101100000000000",
            "110000000000000",
            "100000000000000"
    };

    public static String[] WIKI_VERTICAL_3 = new String[] {
            "00000000000001",
            "00000000000011",
            "00000000001101",
            "00000000110100",
            "00000011000100",
            "00000000110100",
            "00000000001101",
            "00000000000011",
            "00000000000001",
            "00000000000000",
            "00000000010001",
            "00000000011111",
            "00000000010001",
            "00000000000000",
            "00000000001110",
            "00000000010001",
            "00000000010001",
            "00000000011111",
            "00000000010001",
            "00000000000000",
            "00000000010001",
            "00000000010101",
            "00000000010101",
            "00000000011111",
            "00000000010001",
            "00000000000000",
            "00000000001000",
            "00000000010100",
            "00000000010101",
            "00000000011111",
            "00000000010001",
            "00010001000000",
            "00011111000000",
            "00010001000000",
            "00000000000000",
            "00010001000000",
            "00011011000000",
            "00000100000000",
            "00011111000000",
            "00010001000000",
            "00000000000000",
            "00010001000000",
            "00011111000000",
            "10010001000000",
            "11000000000000",
            "10110000000000",
            "00001100000000",
            "10000011000000",
            "11001100000000",
            "00110000000000",
            "11001100000000",
            "10000011000000",
            "00001100000000",
            "10110000000000",
            "11000000000000",
            "10000000000000"
    };
}