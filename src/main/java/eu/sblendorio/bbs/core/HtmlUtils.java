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

    public static String htmlClean(String s) {
        final String result = replacePreTags(defaultString(s))
                .replace("\r", "")
                .replace("©","(C)")
                .replace("\n", " ")
                .replaceAll("<script(\\s|>).*?</script\\s*>", "")
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
                .replaceAll("[àá]|&agrave;?|&aacute;?|&#xE0;|&#xE1;", "a'")
                .replaceAll("[ÀÁ]|&Agrave;?|&Aacute;?|&#xC0;|&#xC1;", "A'")
                .replaceAll("[èé]|&egrave;?|&eacute;?|&#xE8;|&#xE9;", "e'")
                .replaceAll("[ÈÉ]|&Egrave;?|&Eacute;?|&#xC8;|&#xC9;", "E'")
                .replaceAll("[ìí]|&igrave;?|&iacute;?|&#xEC;|&#xED;", "i'")
                .replaceAll("[ÌÍ]|&Igrave;?|&Iacute;?|&#xCC;|&#xCD;", "I'")
                .replaceAll("[òó]|&ograve;?|&oacute;?|&#xF2;|&#xF3;", "o'")
                .replaceAll("[ÒÓ]|&Ograve;?|&Oacute;?|&#xD2;|&#xD3;", "O'")
                .replaceAll("[ùú]|&ugrave;?|&uacute;?|&#xF9;|&#xFA;", "u'")
                .replaceAll("[ÙÚ]|&Ugrave;?|&Uacute;?|&#xD9;|&#xDA;", "U'")
                .replaceAll("&#xA0;|&nbsp;?", " ")
                .replaceAll("’|‘|°|&apos;|&rsquo;|&rsquor;|&lsquo;|&lsquor;|&sbquo;|&#x2019;|&#8216;|&#8217;|&#039;|&#39;|\u0300|\u0301", "'")
                .replaceAll("“|”|&quot;|«|»|&#8220;|&#8221;|&laquo;|&raquo;|&#xAB;|&#xBB;|&#x201C;|&#x201D;", "\"")
                .replaceAll("&amp;?", "&")
                .replaceAll("&gt;", ">")
                .replaceAll("&lt;", "<")
                .replace("&#038;", "&")
                .replace("&#38;", "&")
                .replaceAll("\n(\\s*\n)+", "\n\n")
                .replaceAll("^(\n|\r|\\s)*", "")
                ;
        return result;
    }

}
