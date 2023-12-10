package eu.sblendorio.bbs.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.apache.commons.lang3.StringUtils.defaultString;

public class HtmlUtils {

    private static Pattern p = Pattern.compile("(?is)<pre[^>]*>(([^\n]+)\n?|\n)(.*?)</pre[^>]*>");

    private static String replacePreTags(String s) {
        Matcher m = p.matcher(s);
        while (m.find()) {
            s = m.replaceAll("<br>$1<pre>$3</pre>").replace("<pre></pre>", "<br>");
            m = p.matcher(s);
        }
        return s;
    }

    public static String utilHtmlClean(String s) {
        return replacePreTags(defaultString(s))
                .replace("\u200b", "")
                .replace("\r", "")
                .replace("©","(C)")
                .replace("®", "(R)")
                .replace("\n", " ")
                .replace("&#215;", "x")
                .replace("&#32;", " ")
                .replace("&#x20;", " ")
                .replace("&#91;", "[")
                .replace("&#93;", "]")
                .replace("º", "'")
                .replaceAll("<script(\\s|>).*?</script\\s*>", "")
                .replace((char) 160, ' ')
                .replaceAll("(?is)<script(\\s|>).*?</script\\s*>", "")
                .replaceAll("(?is)<style(\\s|>).*?</style\\s*>", "")
                .replaceAll("(?is)<amp-user-notification(\\s|>).*?</amp-user-notification\\s*>", "")
                .replaceAll("<script.*?>", "")
                .replaceAll("…|&#8230;|&hellip;", "...")
                .replaceAll("–|&#8211;|&#8212;|&mdash;", "-")
                .replaceAll("<br( [^>]*)?>", "\n")
                .replaceAll("<p( [^>]*)?>", "\n")
                .replaceAll("<div( [^>]*)?>", "\n")
                .replaceAll("<li( [^>]*)?>", "\n* ")
                .replaceAll("<h[1-6]( [^>]*)?>", "\n\n")
                .replaceAll("</h[1-6]( [^>]*)?>", "\n")
                .replaceAll("<[^>]*>", "")

                .replaceAll("&#x200b;", "")
                .replaceAll("&#176;", "'")
                .replaceAll("ñ|&ntilde;?|&#xF1;?|&#241;", "n")
                .replaceAll("Ñ|&Ntilde;?|&#xD1;?|&#209;", "N")
                .replaceAll("[å]|&#xE5;|&#229|&aring;", "a")
                .replaceAll("[Å]|&#xC5;|&#197|&Aring;|&#8491;|&#x212B;", "A")

                .replaceAll("œ|&oelig;|&#339;|&#x153;", "oe")
                .replaceAll("Œ|&OElig;|&#338;|&#x152;", "OE")

                .replaceAll("&ccedil;|&#231;|&#xE7;", "ç")
                .replaceAll("&Ccedil;|&#199;|&#xC7;", "Ç")

                .replaceAll("([0-9])(€|&euro;?|&#x20AC|&#x20ac|&#8364;)", "$1 euro")
                .replaceAll("(€|&euro;?|&#x20AC|&#x20ac|&#8364;)([0-9])", "euro $2")
                .replaceAll("€|&euro;?|&#x20AC|&#x20ac|&#8364;", "euro")
                .replaceAll("Ł|&Lstrok;?|&#x0141;|&#321;", "L")
                .replaceAll("ł|&lstrok;?|&#x0142;|&#322;", "l")
                .replaceAll("ő|&odblac;|&#x151;|&#x0151;|&#337;", "o")
                .replaceAll("Ő|&Odblac;|&#x150;|&#x0150;|&#336;", "O")
                .replaceAll("ű|&udblac;|&#x171;|&#x0171;|&#369;", "u")
                .replaceAll("Ű|&Udblac;|&#x170;|&#x0170;|&#368;", "U")
                .replaceAll("Ą|&Aogon;?|&#x0104;|&#260;", "A")
                .replaceAll("ą|&aogon;?|&#x0105;|&#261;", "a")
                .replaceAll("Ę|&Eogon;?|&#x0118;|&#280;", "E")
                .replaceAll("ę|&eogon;?|&#x0119;|&#281;", "e")
                .replaceAll("Ń|&Nacute;?|&#x0143;|&#323;", "N")
                .replaceAll("ń|&nacute;?|&#x0144;|&#324;", "n")
                .replaceAll("Ś|&Sacute;?|&#x015A;|&#x015a;|&#346;", "S")
                .replaceAll("ś|&sacute;?|&#x015B;|&#x015b;|&#347;", "s")
                .replaceAll("Š|&Scaron;?|&#x0160;|&#352;", "S")
                .replaceAll("š|&scaron;?|&#x0161;|&#353;", "s")
                .replaceAll("Ć|&Cacute;?|&#x0106;|&#262;", "C")
                .replaceAll("ć|&cacute;?|&#x0107;|&#263;", "c")
                .replaceAll("Č|&Ccaron;?|&#x010C;|&#x010c;|&#268;", "C")
                .replaceAll("č|&ccaron;?|&#x010D;|&#x010d;|&#269;", "c")
                .replaceAll("Ź|&Zacute;?|&#x0179;|&#377;", "Z")
                .replaceAll("ź|&zacute;?|&#x017A;|&#x017a;|&#378;", "z")
                .replaceAll("Ż|&Zdot;?|&#x017B;|&#x017b;|&#379;", "Z")
                .replaceAll("ż|&zdot;?|&#x017C;|&#x017c;|&#380;", "z")
                .replaceAll("&agrave;?|&#xE0;", "à")
                .replaceAll("&aacute;?|&#xE1;", "á")
                .replaceAll("&Agrave;?|&#xC0;", "À")
                .replaceAll("&Aacute;?|&#xC1;", "Á")
                .replaceAll("&egrave;?|&#xE8;", "è")
                .replaceAll("&eacute;?|&#xE9;", "é")
                .replaceAll("&Egrave;?|&#xC8;", "È")
                .replaceAll("&Eacute;?|&#xC9;", "É")
                .replaceAll("&igrave;?|&#xEC;", "ì")
                .replaceAll("&iacute;?|&#xED;", "í")
                .replaceAll("&Igrave;?|&#xCC;", "Ì")
                .replaceAll("&Iacute;?|&#xCD;", "Í")
                .replaceAll("&ograve;?|&#xF2;", "ò")
                .replaceAll("&oacute;?|&#xF3;", "ó")
                .replaceAll("&Ograve;?|&#xD2;", "Ò")
                .replaceAll("&Oacute;?|&#xD3;", "Ó")
                .replaceAll("&ugrave;?|&#xF9;", "ù")
                .replaceAll("&uacute;?|&#xFA;", "ú")
                .replaceAll("&Ugrave;?|&#xD9;", "Ù")
                .replaceAll("&Uacute;?|&#xDA;", "Ú")
                .replaceAll("æ|&#xE6;|&aelig;", "ae")
                .replaceAll("Æ|&#xC6;|&AElig;", "AE")

                .replaceAll("&#160;|&#xA0;|&nbsp;?", " ")
                .replaceAll("’|‘|°|&apos;|&rsquo;|&rsquor;|&lsquo;|&lsquor;|&sbquo;|&#x2019;|&#8216;|&#8217;|&#039;|&#39;|\u0300|\u0301", "'")
                .replaceAll("″|“|”|„|&quot;|«|»|&#8220;|&#8221;|&laquo;|&raquo;|&#xAB;|&#xBB;|&#x201C;|&#x201D;|&#8243;", "\"")
                .replaceAll("&amp;?", "&")
                .replace("&#47;", "/")
                .replace("&gt;", ">")
                .replace("&lt;", "<")
                .replace("&#038;", "&")
                .replace("&#38;", "&")
                .replaceAll("\n(\\s*\n)+", "\n\n")
                .replaceAll("^(\n|\r|\\s)*", "")
                ;
    }

    public static String utilHtmlDiacriticsToAscii(String s) {
        return s
            .replaceAll("[àá]", "a'")
            .replaceAll("[ÀÁ]", "A'")
            .replaceAll("[èé]", "e'")
            .replaceAll("[ÈÉ]", "E'")
            .replaceAll("[ìí]", "i'")
            .replaceAll("[ÌÍ]", "I'")
            .replaceAll("[òó]", "o'")
            .replaceAll("[ÒÓ]", "O'")
            .replaceAll("[ùú]", "u'")
            .replaceAll("[ÙÚ]", "U'")
            .replaceAll("Ü", "U")
            .replaceAll("ü", "u")
            .replaceAll("Ë", "E")
            .replaceAll("ë", "e")
            .replaceAll("ê", "e")
            .replaceAll("Ê", "E")
            .replaceAll("ï", "i")
            .replaceAll("Ï", "I")
            .replaceAll("ô", "o")
            .replaceAll("Ô", "O")
            .replaceAll("â", "a")
            .replaceAll("Â", "A")
            .replaceAll("ç", "c")
            .replaceAll("Ç", "C")
            .replaceAll("¿", "")
            .replaceAll("¡", "")
        ;
    }

    private HtmlUtils() {
        throw new IllegalStateException("Utility class");
    }

}
