package eu.sblendorio.bbs.tenants.mixed;

import eu.sblendorio.bbs.core.BbsThread;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.stream.Collectors;

import static eu.sblendorio.bbs.core.PetsciiColors.GREY3;
import static eu.sblendorio.bbs.core.PetsciiColors.WHITE;
import static java.lang.Math.min;
import static java.util.Arrays.asList;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

public class LiteFanpageCommons extends LiteCommons {
    public LiteFanpageCommons(BbsThread bbs) {
        super(bbs);
    }

    public String baseUrl() { return "https://www.fanpage.it/"; }

    public void drawLogo() throws Exception {
        bbs.cls();
        bbs.println("Fanpage");
        bbs.println();
    }

    public String by() { return "di"; }

    public List<Article> getArticles() throws Exception {
        return Jsoup.parse(BbsThread.httpGet(baseUrl()+"feed/")).select("item").stream()
                .map(element -> {
                    var title = element.select("title").text().replaceAll("<!\\[CDATA\\[|\\]\\]>", "");
                    var link = element.html().replaceAll("(?is)^.*<link>(.*?)<.*$", "$1").replace("\n", "");
                    var dateFull = element.select("pubDate").html();
                    var author = element.html().replaceAll("(?is)^.*<dc:creator><!\\[CDATA\\[(.*?)\\]\\]>.*$", "$1").replace("\n", "");

                    SimpleDateFormat from = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
                    SimpleDateFormat to = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
                    String date = dateFull;
                    try {
                        date = to.format(from.parse(dateFull));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return new Article(link.toString(), title, date, author, null);
                }).toList();
    }

    public Article getArticle(Article item) throws Exception {
        String wholeText = BbsThread.httpGet(item.url());
        Document doc = Jsoup.parse(wholeText);
        String text = doc.select("p").stream().map(Element::text).collect(Collectors.joining("<br><br>"));
        return new Article(item.url(), item.title(), item.date(), item.author(), text);
    }

    public static void main(String[] args) throws Exception {
        var start = new LiteFanpageCommons(null);
        var articles = start.getArticles();
        System.out.println(start.getArticle(articles.getFirst()));
    }
}
