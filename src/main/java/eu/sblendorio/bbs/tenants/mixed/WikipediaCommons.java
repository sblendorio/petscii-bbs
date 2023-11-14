package eu.sblendorio.bbs.tenants.mixed;

import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.HtmlUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultString;

public class WikipediaCommons {
    private static final String URL_GET_BY_PAGEID = "https://${LANG}.wikipedia.org/w/api.php?format=json&action=parse&prop=text&pageid=${PAGEID}";
    private static final String URL_SEARCH_BY_KEYWORDS = "https://${LANG}.wikipedia.org/w/api.php?format=json&action=query&list=search&srsearch=${KEYWORDS}&sroffset=${OFFSET}&srlimit=${LIMIT}";

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

    public static class WikipediaItem {
        public String lang;
        public Long pageid;
        public String title;
        public Long size;
        public Long wordcount;
        public String snippet;
        public String timestamp;
        public Long ns;
    }

    public static String getTextContent(WikipediaItem item) throws IOException, ParseException {
        return getTextContent(item.lang, item.pageid);
    }

    public static String getTextContent(String lang, Long pageid) throws IOException, ParseException {
        String html = getHtmlContent(lang, pageid)
                .replaceAll("<span class=\"mw-editsection\">.*?</span></span>", "");
        String step1 = HtmlUtils.utilHtmlClean(html).replaceAll("(?is) +", " ")
                .replaceAll("(?is)<style>.*?</style>", EMPTY)
                .replaceAll("(?is)<script[ >].*?</script>", EMPTY)
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
        return (String) text.get("*");
    }

    public static List<WikipediaItem> search(String lang, String keywords) throws IOException, ParseException {
        return search(lang, keywords, false);
    }

    public static List<WikipediaItem> searchFirst(String lang, String keywords) throws IOException, ParseException {
        return search(lang, keywords, true);
    }

    public static List<WikipediaItem> search(String lang, String keywords, boolean onlyFirst) throws IOException, ParseException {
        Long offset = 0L;
        Long limit = onlyFirst ? 1L : 50L;
        List<WikipediaItem> result = new LinkedList<>();
        if (StringUtils.isBlank(keywords))
            return result;
        JSONObject continues;
        do {
            String url = getSearchUrl(lang, keywords, offset, limit);
            JSONObject root = (JSONObject) BbsThread.httpGetJson(url);
            continues = (JSONObject) root.get("continue");
            JSONObject query = (JSONObject) root.get("query");
            JSONArray search = (JSONArray) query.get("search");
            for (int i = 0; i < search.size(); i++) {
                JSONObject itemJ = (JSONObject) search.get(i);
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
            }
            if (continues != null) {
                offset = (Long) (continues.get("sroffset"));
            } else {
            }
        } while (continues != null && !onlyFirst);
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
        List<WikipediaItem> items = searchFirst("it", "Maccio  capatonda");
        System.out.println(items.size());
        items.forEach(item -> System.out.println(item.title));
        System.out.println("***********************");
        System.out.println("***********************");
        System.out.println("***********************");
        System.out.println(getTextContent(items.get(0)));
    }

    public static Set<String> langs = new HashSet<>(Arrays.asList(
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
}