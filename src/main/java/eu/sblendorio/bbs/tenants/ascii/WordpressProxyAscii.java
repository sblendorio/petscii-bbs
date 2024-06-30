package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.Hidden;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static eu.sblendorio.bbs.core.Utils.*;
import static eu.sblendorio.bbs.core.Utils.STR_ALPHANUMERIC;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static org.apache.commons.collections4.MapUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.math.NumberUtils.toInt;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

@Hidden
public class WordpressProxyAscii extends AsciiThread {

    public String CHROME_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36";

    protected String HR_TOP;

    @Override
    public void initBbs() throws Exception {
        HR_TOP = StringUtils.repeat('-', getScreenColumns() - 1);
    }

    private static Logger logger = LogManager.getLogger(WordpressProxyAscii.class);

    static class Post {
        long id;
        String title;
        String date;
        String content;
        String excerpt;
        Long authorId;
    }

    protected String domain = "https://wordpress.org/news";
    protected String categoriesId = null;
    protected byte[] logo = LOGO_WORDPRESS;
    protected byte[] secondaryLogo = null;
    protected int pageSize = 8;
    protected int screenLines;
    protected int mainLogoSize = 1;
    protected Integer secondaryLogoSize = null;
    protected boolean showAuthor = false;
    protected String httpUserAgent = null;

    protected Map<Integer, Post> posts = emptyMap();
    protected int currentPage = 1;

    private String originalDomain;

    public WordpressProxyAscii() {
        super();
    }

    public WordpressProxyAscii(String domain) {
        this();
        this.domain = domain;
    }

    public String by() { return "di"; }

    public WordpressProxyAscii(String domain, byte[] logo) {
        this();
        this.domain = domain;
        this.logo = logo;
    }

    public boolean resizeable() { return true; }

    protected final String getApi() { return domain + "/wp-json/wp/v2/"; }

    @Override
    public void doLoop() throws Exception {
        screenLines = getScreenRows() - 3 - (secondaryLogoSize == null ? mainLogoSize : secondaryLogoSize);
        originalDomain = domain;
        log("Wordpress entering (" + domain + ")");
        listPosts();
        while (true) {
            log("Wordpress waiting for input");
            print(getScreenColumns() >= 40
                ? "(N+-)Page (H)elp (R)eload (.)Quit> "
                : "(N+-)Page (.)Quit> "
            );
            resetInput();
            flush(); String inputRaw = readLine(setOfChars(STR_ALPHANUMERIC, ".:,;_ []()<>@+-*/^='?!$%&#"));
            String input = lowerCase(trim(inputRaw));
            if (".".equals(input) || "exit".equals(input) || "quit".equals(input) || "q".equals(input)) {
                break;
            } else if ("help".equals(input) || "h".equals(input)) {
                help();
                listPosts();
                continue;
            } else if ("n".equals(input) || "n+".equals(input) || "+".equals(input)) {
                ++currentPage;
                posts = null;
                try {
                    listPosts();
                } catch (NullPointerException e) {
                    --currentPage;
                    posts = null;
                    listPosts();
                    continue;
                }
                continue;
            } else if (("-".equals(input) || "n-".equals(input)) && currentPage > 1) {
                --currentPage;
                posts = null;
                listPosts();
                continue;
            } else if ("--".equals(input) && currentPage > 1) {
                currentPage = 1;
                posts = null;
                listPosts();
                continue;
            } else if ("r".equals(input) || "reload".equals(input) || "refresh".equals(input)) {
                posts = null;
                listPosts();
                continue;
            } else if (posts.containsKey(toInt(input))) {
                displayPost(toInt(input));
            } else if ("".equals(input)) {
                listPosts();
                continue;
            } else if ("clients".equals(input)) {
                listClients();
                continue;
            } else if (substring(input,0,5).equalsIgnoreCase("send ")) {
                long client = toLong(input.replaceAll("^send ([0-9]+).*$", "$1"));
                String message = input.replaceAll("^send [0-9]+ (.*)$", "$1");
                if (getClients().containsKey(client) && isNotBlank(message)) {
                    log("Sending '"+message+"' to #"+client);
                    int exitCode = send(client, message);
                    log("Message sent, exitCode="+exitCode+".");
                }
            } else if (substring(input,0,5).equalsIgnoreCase("name ")) {
                String newName = defaultString(input.replaceAll("^name ([^\\s]+).*$", "$1"));
                changeClientName(newName);
            } else if (substring (input, 0, 8).equalsIgnoreCase("connect ")) {
                final String oldDomain = domain;
                final byte[] oldLogo = logo;
                domain = defaultString(input.replaceAll("^connect ([^\\s]+).*$", "$1"));
                if (!domain.matches("(?is)^http.*"))
                    domain = "https://" + domain;
                log("new API: "+getApi());
                posts = null;
                currentPage = 1;
                try {
                    listPosts();
                } catch (Exception e) {
                    log("WORDPRESS FAILED: " + e.getClass().getName() + ": " + e.getMessage());
                    logo = oldLogo;
                    domain = oldDomain;
                    posts = null;
                    listPosts();
                }
            }
        }
        flush();
    }

    public int getPageSize() {
        return this.pageSize;
    }
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    protected Map<Integer, Post> getPosts(int page, int perPage) throws Exception {
        if (page < 1 || perPage < 1) return null;
        Map<Integer, Post> result = new LinkedHashMap<>();
        String uri = "posts?context=view"
                + (isBlank(categoriesId) ? EMPTY : "&categories=" + categoriesId)
                + "&page=" + page
                + "&per_page=" + perPage;
        if (domain.contains("?")) {
            uri = uri.replace("?", "%3F").replace("&", "%26");
        }
        log("getPosts. uri="+getApi()+uri);
        JSONArray posts = (JSONArray) httpGetJson(getApi()+uri, httpUserAgent);
        if (posts == null) return result;
        for (int i=0; i<posts.size(); ++i) {
            Post post = new Post();
            JSONObject postJ = (JSONObject) posts.get(i);
            post.id = (Long) postJ.get("id");
            post.content = defaultString((String) ((JSONObject) postJ.get("content")).get("rendered")).replaceAll("(?is)(\\[/?vc_[^]]*\\])*", EMPTY);
            post.title = defaultString((String) ((JSONObject) postJ.get("title")).get("rendered")).replaceAll(" +", " ");
            post.date = defaultString((String) postJ.get("date")).replace("T", SPACE).replaceAll(":\\d\\d\\s*$", EMPTY);
            post.excerpt = (String) ((JSONObject) postJ.get("excerpt")).get("rendered");
            post.authorId = toLong(postJ.get("author").toString());
            result.put(i+1+(perPage*(page-1)), post);
        }
        return result;
    }

    protected void listPosts() throws Exception {
        cls();
        drawLogo();
        if (isEmpty(posts)) {
            posts = getPosts(currentPage, pageSize);
        }

        long totalRows = 0;
        for (Map.Entry<Integer, Post> entry: posts.entrySet()) {
            int i = entry.getKey();
            Post post = entry.getValue();
            print(i + ".");
            final int nCols = getScreenColumns() - 3;
            final int iLen = nCols-String.valueOf(i).length();
            String line = WordUtils.wrap(filterPrintable(htmlClean(post.title)), iLen, "\r", true);
            totalRows += 1 + line.chars().filter(ch -> ch == '\r').count();
            println(line.replaceAll("\r", newlineString() + " " + repeat(" ", nCols-iLen)));
        }
        for (int i = 0; i < (getScreenRows() - totalRows - mainLogoSize - 2); ++i) newline();
    }

    protected List<String> wordWrap(String s) {
        String[] cleaned = filterPrintableWithNewline(htmlClean(s)).split("\n");
        List<String> result = new ArrayList<>();
        for (String item: cleaned) {
            String[] wrappedLine = WordUtils
                .wrap(item, getScreenColumns() - 1, "\n", true)
                .split("\n");
            result.addAll(asList(wrappedLine));
        }
        return result;
    }

    protected void help() throws Exception {
        cls();
        drawLogo();
        println();
        println();
        println("Press any key to go back to posts");
        readKey();
    }

    protected String downstreamTransform(String s) {
        return s;
    }

    protected void emptyRow() {
        println();
    }

    protected void displayPost(int n) throws Exception {
        cls();
        drawSecondaryLogo();

        String author = null;
        final Post p = posts.get(n);

        try {
            if (showAuthor) {
                JSONObject authorJ = (JSONObject) httpGetJson(getApi() + "users/" + p.authorId, httpUserAgent);
                author = authorJ.get("name").toString();
            }
        } catch (Exception e) {
            log("Error during retrieving author");
            logger.error("Error during retrieving author", e);
        }
        final String content = downstreamTransform(p.content
                    .replaceAll("(?is)[\n\r ]+", " ")
                    .replaceAll("(?is)<style>.*?</style>", EMPTY)
                    .replaceAll("(?is)<script[ >].*?</script>", EMPTY)
                    .replaceAll("(?is)^[\\s\\n\\r]+|^\\s*(</?(br|div|figure|iframe|img|p|h[0-9])[^>]*>\\s*)+", EMPTY)
                    .replaceAll("(?is)^(<[^>]+>(\\s|\n|\r)*)+", EMPTY)
                )
                .replaceAll("(?is)^(<[^>]+>(\\s|\n|\r|\u00a0)*)+", EMPTY)
                ;
        final String head = p.title + (isNotBlank(author) ? " - "+by()+" " + author : EMPTY) + "<br>" + HR_TOP ;
        List<String> rows = wordWrap(head);

        List<String> article = wordWrap(p.date.replaceAll("^(\\d\\d\\d\\d).(\\d\\d).(\\d\\d).*","$3/$2/$1") +
            " - " + content
        );
        rows.addAll(article);
        int page = 1;
        int j = 0;
        boolean forward = true;
        while (j < rows.size()) {
            if (j>0 && j % screenLines == 0 && forward) {
                emptyRow();
                print(getScreenColumns() >= 40
                    ? "-PAGE " + page + "-  SPACE=NEXT  -=PREV  .=EXIT"
                    :  "(" + page + ") SPC -PREV .EXIT"
                );

                flush();
                int ch;
                do {
                    resetInput();
                    ch = readKey();
                } while (ch == 27 || ch == 0);
                
                if (ch == '.') {
                    listPosts();
                    return;
                }
                if (ch == '-' && page > 1) {
                    j -= (screenLines *2);
                    --page;
                    forward = false;
                    cls();
                    drawLogo();
                    continue;
                } else {
                    ++page;
                }
                cls();
                drawLogo();
            }
            String row = rows.get(j);
            println(row);
            forward = true;
            ++j;
        }
        println();
    }

    protected static final byte[] LOGO_WORDPRESS = "Wordpress".getBytes(StandardCharsets.ISO_8859_1);

    protected void drawLogo() {
        if (!equalsDomain(domain, originalDomain)) {
            final String normDomain = normalizeDomain(domain);
            print(normDomain);
        } else {
            write(logo);
        }
        newline();
        newline();
    }

    protected void drawSecondaryLogo() {
        if (!equalsDomain(domain, originalDomain)) {
            final String normDomain = normalizeDomain(domain);
            print(normDomain);
        } else {
            write(secondaryLogo == null ? logo : secondaryLogo);
        }
        newline();
        newline();
    }

    protected void listClients() {
        cls();
        println("You are #" + getClientId() + ": "+getClientName() + " [" + getClientClass().getSimpleName() + "]");
        newline();
        for (Map.Entry<Long, BbsThread> entry: clients.entrySet())
            if (entry.getKey() != getClientId())
                println("#" + entry.getKey() +": "+entry.getValue().getClientName() + " [" + entry.getValue().getClientClass().getSimpleName() + "]");
        println();
    }

    @Override
    public void receive(long sender, Object message) {
        log("--------------------------------");
        log("From "+getClients().get(sender).getClientName()+": " +message);
        log("--------------------------------");
        println();
        println("--------------------------------");
        println("From "+getClients().get(sender).getClientName()+": " +message);
        println("--------------------------------");
    }

}