package eu.sblendorio.bbs.tenants.mixed;

import eu.sblendorio.bbs.core.BbsThread;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public class LiteCnnCommons {
    public record ArticleItem(String url, String title) {}
    public record Article(String title, String date, String author, String text) {}

    public final static String BASE_URL = "https://lite.cnn.com/";
    public final static String AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36";

    public static String get(String url) throws Exception {
        HttpResponse response;
        String result;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).headers("User-Agent", AGENT).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println(response.body());
                throw new IllegalStateException("BAD HTTP REQUEST");
            }
            result = response.body().toString();
        }
        return result;
    }

    public static List<ArticleItem> getArticles() throws Exception {
        return Jsoup.parse(get(BASE_URL)).select(".card--lite").stream()
                .map(element -> {
                    Elements li = Jsoup.parse(element.html()).select("a");
                    return new ArticleItem(li.attr("href"), li.text());
                }).toList();
    }

    public static Article getArticle(ArticleItem item) throws Exception {
        final String wholeText = get(BASE_URL + item.url());
        final Document doc = Jsoup.parse(wholeText);
        final String author = doc.select(".byline--lite").text().replaceAll("(?is)^By ", "");
        final String text = doc.select(".paragraph--lite").stream().map(Element::text).collect(Collectors.joining("<br><br>"));
        final String metadata = doc.select("script").select("script[type$=json]").html();
        final String datePublished = metadata.replaceAll("(?is).*?\"datePublished\".*?:.*?\"(....-..-..).*$", "$1");
        return new Article(item.title(), datePublished, author, text);
    }

    public static List<String> feedToText(BbsThread bbs, String hrTop, LiteCnnCommons.Article feed) {
        String author = (StringUtils.isBlank(StringUtils.trim(feed.author()))) ? "" : (" - by " + StringUtils.trim(feed.author()));
        String head = StringUtils.trim(feed.title()) + author + "<br>" + hrTop + "<br>";
        List<String> rows = wordWrap(bbs, head);
        List<String> article = wordWrap(bbs, (
                (feed.date() == null) ? "" : (
                        feed.date() + " - ")) + feed.text()
                // .replaceAll("^([\\s\\n\\r]+|(<(br|p|img|div|/)[^>]*>))+", "")
                .replaceAll("(?is)[\n\r ]+", " ")
                .replaceAll("(?is)<style>.*?</style>", EMPTY)
                .replaceAll("(?is)<script[ >].*?</script>", EMPTY)
                .replaceAll("(?is)^[\\s\\n\\r]+|^\\s*(</?(br|div|figure|iframe|img|p|h[0-9])[^>]*>\\s*)+", EMPTY)
                .replaceAll("(?is)^(<[^>]+>(\\s|\n|\r|\u00a0)*)+", EMPTY)
        );
        rows.addAll(article);
        return rows;
    }

    public static List<String> wordWrap(BbsThread bbs, String s) {
        String[] cleaned = bbs.filterPrintableWithNewline(bbs.htmlClean(s)).split("\n");
        List<String> result = new ArrayList<>();
        for (String item: cleaned) {
            String[] wrappedLine = WordUtils
                    .wrap(item, bbs.getScreenColumns() - 1, "\n", true)
                    .split("\n");
            result.addAll(asList(wrappedLine));
        }
        return result;
    }




    public static void main(String[] args) throws Exception {
        var articles = getArticles();
        System.out.println(getArticle(articles.getFirst()));
    }
}
