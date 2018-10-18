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
                .replaceAll("…|&#8230;", "...")
                .replaceAll("–|&#8211;|&#8212;|&mdash;", "-")
                .replaceAll("<br( [^>]*)?>", "\n")
                .replaceAll("<p( [^>]*)?>", "\n")
                .replaceAll("<div( [^>]*)?>", "\n")
                .replaceAll("<li( [^>]*)?>", "\n* ")
                .replaceAll("<h[1-6]( [^>]*)?>", "\n\n")
                .replaceAll("</h[1-6]( [^>]*)?>", "\n")
                .replaceAll("<[^>]*>", "")
                .replaceAll("[àá]|&agrave;?|&aacute;?", "a'")
                .replaceAll("[ÀÁ]|&Agrave;?|&Aacute;?", "A'")
                .replaceAll("[èé]|&egrave;?|&eacute;?", "e'")
                .replaceAll("[ÈÉ]|&Egrave;?|&Eacute;?", "E'")
                .replaceAll("[ìí]|&igrave;?|&iacute;?", "i'")
                .replaceAll("[ÌÍ]|&Igrave;?|&Iacute;?", "I'")
                .replaceAll("[òó]|&ograve;?|&oacute;?", "o'")
                .replaceAll("[ÒÓ]|&Ograve;?|&Oacute;?", "O'")
                .replaceAll("[ùú]|&ugrave;?|&uacute;?", "u'")
                .replaceAll("[ÙÚ]|&Ugrave;?|&Uacute;?", "U'")
                .replaceAll("&nbsp;?", " ")
                .replaceAll("’|‘|°|&rsquo;|&rsquor;|&lsquo;|&lsquor;|&sbquo;|&#8216;|&#8217;|&#039;|&#39;|\u0300|\u0301", "'")
                .replaceAll("“|”|&quot;|«|»|&#8220;|&#8221;", "\"")
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
