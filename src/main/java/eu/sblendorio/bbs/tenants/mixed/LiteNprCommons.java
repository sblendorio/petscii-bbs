package eu.sblendorio.bbs.tenants.mixed;

import eu.sblendorio.bbs.core.BbsThread;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class LiteNprCommons extends LiteCommons {
    public LiteNprCommons(BbsThread bbs) {
        super(bbs);
    }

    public String baseUrl() { return "https://text.npr.org"; }

    public void drawLogo() throws Exception {
        bbs.cls();
        bbs.println("NPR.org");
        bbs.println();
    }

    public List<Article> getArticles() throws Exception {
        return Jsoup.parse(get(baseUrl()+"/1001")).select(".topic-title").stream()
                .map(el ->
                    new Article(baseUrl()+el.attr("href"), el.text(), null, null, null)
                ).toList();
    }

    public Article getArticle(Article item) throws Exception {
        String wholeText = get(item.url());
        Document doc = Jsoup.parse(wholeText);
        String author = doc.select(".story-head").select("p").get(0).text().replaceAll("(?is)^By ", "");
        String text = doc.select(".paragraphs-container").stream().map(Element::html).collect(Collectors.joining("<br><br>"));
        String datePublished = doc.select(".story-head").select("p").get(1).text()
                .replaceAll("(?is)\\s*•.*$", "");
        return new Article(item.url(), item.title(), datePublished, author, text);
    }

    public static void main(String[] args) throws Exception {
        var start = new LiteNprCommons(null);
        var articles = start.getArticles();
        // articles.forEach(x -> System.out.println(x));
        System.out.println(start.getArticle(articles.getFirst()));
    }
}
