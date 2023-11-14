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
                .replace("&#91;", "[")
                .replace("&#93;", "]")
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

                .replaceAll("ñ|&ntilde;?|&#xF1;?|&#241;", "n")
                .replaceAll("Ñ|&Ntilde;?|&#xD1;?|&#209;", "N")
                .replaceAll("[å]|&#xE5;|&#229|&aring;", "a")
                .replaceAll("[Å]|&#xC5;|&#197|&Aring;|&#8491;|&#x212B;", "A")

                .replaceAll("œ|&oelig;|&#339;|&#x153;", "oe")
                .replaceAll("Œ|&OElig;|&#338;|&#x152;", "OE")

                .replaceAll("&ccedil;|&#231;|&#xE7;", "ç")
                .replaceAll("&Ccedil;|&#199;|&#xC7;", "Ç")

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
                .replaceAll("″|“|”|&quot;|«|»|&#8220;|&#8221;|&laquo;|&raquo;|&#xAB;|&#xBB;|&#x201C;|&#x201D;|&#8243;", "\"")
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
