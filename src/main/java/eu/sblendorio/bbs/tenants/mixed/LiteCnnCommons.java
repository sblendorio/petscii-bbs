package eu.sblendorio.bbs.tenants.mixed;

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
import java.util.List;
import java.util.stream.Collectors;

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

    public static void main(String[] args) throws Exception {
        var articles = getArticles();
        System.out.println(getArticle(articles.getFirst()));
    }

}
