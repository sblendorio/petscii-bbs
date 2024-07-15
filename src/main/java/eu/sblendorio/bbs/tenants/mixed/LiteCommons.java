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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static eu.sblendorio.bbs.core.PetsciiColors.GREY3;
import static eu.sblendorio.bbs.core.PetsciiColors.WHITE;
import static java.lang.Math.min;
import static java.util.Arrays.asList;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

public class LiteCommons {
    public String AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36";

    public record ArticleItem(String url, String title) {}
    public record Article(String title, String date, String author, String text) {}

    protected BbsThread bbs;

    public LiteCommons(BbsThread bbs) {
        this.bbs = bbs;
    }

    public int pageSize = 10;
    public int currentPage = 1;
    public int gap = 4;
    public boolean alwaysRefreshFeed = false;


    List<LiteCommons.ArticleItem> posts = Collections.emptyList();

    public String baseUrl() { return "https://lite.cnn.com/"; }

    public List<ArticleItem> getArticles() throws Exception {
        return Jsoup.parse(get(baseUrl())).select(".card--lite").stream()
                .map(element -> {
                    Elements li = Jsoup.parse(element.html()).select("a");
                    return new ArticleItem(li.attr("href"), li.text());
                }).toList();
    }

    public Article getArticle(ArticleItem item) throws Exception {
        String wholeText = get(baseUrl() + item.url());
        Document doc = Jsoup.parse(wholeText);
        String author = doc.select(".byline--lite").text().replaceAll("(?is)^By ", "");
        String text = doc.select(".paragraph--lite").stream().map(Element::text).collect(Collectors.joining("<br><br>"));
        String metadata = doc.select("script").select("script[type$=json]").html();
        String datePublished = metadata.replaceAll("(?is).*?\"datePublished\".*?:.*?\"(....-..-..).*$", "$1");
        return new Article(item.title(), datePublished, author, text);
    }

    public void printListStatusLine() {
        bbs.print(bbs.getScreenColumns() >= 40
                ? "#, (N+-)Page (R)eload (.)Quit> "
                : "(N+-)Page (.)Quit> "
        );
    }

    public void printArticleStatusLine(int page) {
        bbs.print(bbs.getScreenColumns() >= 40
                ? "-PAGE " + page + "-  SPACE=NEXT  -=PREV  .=EXIT"
                : "(" + page + ") SPC -PREV .EXIT");

    }

    public static void printArticleStatusLinePetscii(BbsThread bbs, int page) {
        bbs.write(WHITE); bbs.print("-PAGE " + page + "-  SPACE=NEXT  -=PREV  .=EXIT"); bbs.write(GREY3);
    }

    public static void printListStatusLinePetscii(BbsThread bbs) {
        bbs.write(WHITE);bbs.print("#"); bbs.write(GREY3);
        bbs.print(" [");
        bbs.write(WHITE); bbs.print("N+-"); bbs.write(GREY3);
        bbs.print("]Page [");
        bbs.write(WHITE); bbs.print("R"); bbs.write(GREY3);
        bbs.print("]eload [");
        bbs.write(WHITE); bbs.print("."); bbs.write(GREY3);
        bbs.print("]");
        bbs.write(WHITE); bbs.print("Q"); bbs.write(GREY3);
        bbs.print("uit> ");
    }


    public String get(String url) throws Exception {
        String result;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).headers("User-Agent", AGENT).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println(response.body());
                throw new IllegalStateException("BAD HTTP REQUEST");
            }
            result = response.body();
        }
        return result;
    }

    public void doLoop() throws Exception {
        boolean keepGoing = listPosts();
        if (!keepGoing) return;

        while (true) {
            bbs.log("RssReader waiting for input");
            printListStatusLine();
            bbs.resetInput();
            bbs.flush();
            String inputRaw = bbs.readLine();
            String input = lowerCase(trim(inputRaw));
            if (".".equals(input) || "exit".equals(input) || "quit".equals(input) || "q".equals(input)) {
                break;
            } else if (("+".equals(input) || "n".equals(input) || "n+".equals(input)) && currentPage*pageSize<posts.size()) {
                ++currentPage;
                if (alwaysRefreshFeed) posts = null;
                try {
                    listPosts();
                } catch (NullPointerException e) {
                    --currentPage;
                    if (alwaysRefreshFeed) posts = null;
                    listPosts();
                }
            } else if (("-".equals(input) || "n-".equals(input)) && currentPage > 1) {
                --currentPage;
                if (alwaysRefreshFeed) posts = null;
                listPosts();
            } else if ("--".equals(input)) {
                currentPage = 1;
                if (alwaysRefreshFeed) posts = null;
                listPosts();
            } else if ("r".equals(input) || "reload".equals(input) || "refresh".equals(input)) {
                posts = null;
                listPosts();
            } else if (toInt(input) >= 1 && toInt(input) <= posts.size()) {
                boolean exitByUser = displayPost(posts.get(toInt(input) - 1));
                if (exitByUser) listPosts();
            } else if ("".equals(input)) {
                listPosts();
            }
        }
    }

    public  List<String> feedToText(LiteCommons.Article feed) {
        String author = (StringUtils.isBlank(StringUtils.trim(feed.author()))) ? "" : (" - by " + StringUtils.trim(feed.author()));
        String head = StringUtils.trim(feed.title()) + author + "<br>" + hrTop() + "<br>";
        List<String> rows = wordWrap(head);
        List<String> article = wordWrap((
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

    public  List<String> wordWrap(String s) {
        String[] cleaned = bbs.io().filterPrintableWithNewline(bbs.io().htmlClean(s)).split("\n");
        List<String> result = new ArrayList<>();
        for (String item : cleaned) {
            String[] wrappedLine = WordUtils
                    .wrap(item, bbs.getScreenColumns() - 1, "\n", true)
                    .split("\n");
            result.addAll(asList(wrappedLine));
        }
        return result;
    }

    public char hrChar() {
        return '-';
    }

    public String hrTop() {
        return StringUtils.repeat(hrChar(), bbs.getScreenColumns() - 1);
    }

    public void emptyRow() {
        bbs.println();
    }

    protected boolean displayPost(LiteCommons.ArticleItem item) throws Exception {
        drawLogo();
        LiteCommons.Article article = getArticle(item);
        List<String> rows = feedToText(article);

        int page = 1;
        int j = 0;
        boolean forward = true;
        while (j < rows.size()) {
            if (j > 0 && j % (bbs.getScreenRows() - gap) == 0 && forward) {
                emptyRow();
                printArticleStatusLine(page);
                bbs.flush();
                int ch;
                do {
                    bbs.resetInput();
                    ch = bbs.readKey();
                } while (ch == 27 || ch == 0);

                if (ch == '.') {
                    return true;
                } else if (ch == '-' && page > 1) {
                    j -= 2*(bbs.getScreenRows() - gap);
                    --page;
                    forward = false;
                    drawLogo();
                    continue;
                } else {
                    ++page;
                }
                drawLogo();
            }
            String row = rows.get(j);
            bbs.println(row);
            forward = true;
            ++j;
        }
        bbs.println();
        return false;
    }


    public void drawLogo() throws Exception {
        bbs.cls();
        bbs.println("CNN News");
        bbs.println();
    }

    public boolean listPosts() throws Exception {
        drawLogo();
        if (isEmpty(posts)) {
            posts = getArticles();
        }
        final int start = pageSize * (currentPage-1);
        final int end = min(pageSize + start, posts.size());

        long totalRows = 0;
        for (int i = start; i < end; ++i) {
            LiteCommons.ArticleItem post = posts.get(i);
            highlight(true);
            bbs.print((i+1) + ".");
            highlight(false);
            final int iLen = (bbs.getScreenColumns()-3)-String.valueOf(i+1).length();
            String line = WordUtils.wrap(bbs.filterPrintable(bbs.htmlClean(post.title())), iLen, "\r", true);
            totalRows += 1 + line.chars().filter(ch -> ch == '\r').count();
            bbs.println(line.replaceAll("\r", bbs.newlineString() +" " + repeat(" ", (bbs.getScreenColumns()-3)-iLen)));
        }
        for (int i = 0; i <= (bbs.getScreenRows() - totalRows - gap); ++i) bbs.newline();
        bbs.flush();
        return true;
    }

    public void highlight(boolean on) {
    }

    public void main(String[] args) throws Exception {
        var articles = getArticles();
        System.out.println(getArticle(articles.getFirst()));
    }
}
