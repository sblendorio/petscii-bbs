package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.core.HtmlUtils;
import static eu.sblendorio.bbs.core.PetsciiColors.GREY3;
import static eu.sblendorio.bbs.core.PetsciiColors.WHITE;
import static eu.sblendorio.bbs.core.PetsciiKeys.CASE_LOCK;
import static eu.sblendorio.bbs.core.PetsciiKeys.DEL;
import static eu.sblendorio.bbs.core.PetsciiKeys.LOWERCASE;
import eu.sblendorio.bbs.core.PetsciiThread;
import static eu.sblendorio.bbs.core.Utils.equalsDomain;
import static eu.sblendorio.bbs.core.Utils.normalizeDomain;
import java.io.IOException;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static org.apache.commons.collections4.MapUtils.isEmpty;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.apache.commons.lang3.StringUtils.substring;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.lang3.math.NumberUtils.toInt;
import static org.apache.commons.lang3.math.NumberUtils.toLong;
import org.apache.commons.text.WordUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Hidden
public class WordpressProxy extends PetsciiThread {

    private static final Logger logger = LoggerFactory.getLogger(WordpressProxy.class);
    String HR_TOP;

    @Override
    public void initBbs() throws Exception {
        HR_TOP = StringUtils.repeat(chr(163), getScreenColumns() - 1);
    }

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
    protected int pageSize = 10;
    protected int screenLines = 19;
    protected boolean showAuthor = false;
    protected String httpUserAgent = null;

    protected Map<Integer, Post> posts = emptyMap();
    protected int currentPage = 1;

    private String originalDomain;

    public WordpressProxy() {
        // Mandatory
    }

    public WordpressProxy(String domain) {
        this.domain = domain;
    }

    public WordpressProxy(String domain, byte[] logo) {
        this.domain = domain;
        this.logo = logo;
    }

    protected final String getApi() { return domain + "/wp-json/wp/v2/"; }

    @Override
    public void doLoop() throws Exception {
        originalDomain = domain;
        write(LOWERCASE, CASE_LOCK);
        log("Wordpress entering (" + domain + ")");
        listPosts();
        while (true) {
            log("Wordpress waiting for input");
            write(WHITE);print("#"); write(GREY3);
            print(" [");
            write(WHITE); print("N+-"); write(GREY3);
            print("]Page [");
            write(WHITE); print("H"); write(GREY3);
            print("]elp [");
            write(WHITE); print("R"); write(GREY3);
            print("]eload [");
            write(WHITE); print("."); write(GREY3);
            print("]");
            write(WHITE); print("Q"); write(GREY3);
            print("uit> ");
            resetInput();
            flush(); String inputRaw = readLine();
            String input = lowerCase(trim(inputRaw));
            if (".".equals(input) || "exit".equals(input) || "quit".equals(input) || "q".equals(input)) {
                break;
            } else if ("help".equals(input) || "h".equals(input)) {
                help();
                listPosts();
                continue;
            } else if ("+".equals(input) || "n".equals(input) || "n+".equals(input)) {
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
            } else if (posts.containsKey(toInt(input.replace("#", "")))) {
                displayPost(toInt(input.replace("#", "")));
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

    protected Map<Integer, Post> getPosts(int page, int perPage) throws Exception {
        if (page < 1 || perPage < 1) return null;
        Map<Integer, Post> result = new LinkedHashMap<>();
        JSONArray posts = (JSONArray) httpGetJson(getApi()
            + "posts?context=view"
            + (isBlank(categoriesId) ? EMPTY : "&categories=" + categoriesId)
            + "&page=" + page
            + "&per_page=" + perPage, httpUserAgent);
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
            waitOn();
            posts = getPosts(currentPage, pageSize);
            waitOff();
        }
        for (Map.Entry<Integer, Post> entry: posts.entrySet()) {
            int i = entry.getKey();
            Post post = entry.getValue();
            write(WHITE); print(i + "."); write(GREY3);
            final int nCols = getScreenColumns() - 3;
            final int iLen = nCols-String.valueOf(i).length();
            String line = WordUtils.wrap(filterPrintable(htmlClean(post.title)), iLen, "\r", true);
            println(line.replaceAll("\r", newlineString() + " " + repeat(" ", nCols-iLen)));
        }
        newline();
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

    protected void displayPost(int n) throws Exception {
        cls();
        drawLogo();
        waitOn();

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
        final String content = downstreamTransform(
                p.content
                    .replaceAll("(?is)[\n\r ]+", " ")
                    .replaceAll("(?is)<style>.*?</style>", EMPTY)
                    .replaceAll("(?is)<script[ >].*?</script>", EMPTY)
                    .replaceAll("(?is)^[\\s\\n\\r]+|^\\s*(</?(br|div|figure|iframe|img|p|h[0-9])[^>]*>\\s*)+", EMPTY)
                )
                .replaceAll("(?is)^(<[^>]+>(\\s|\n|\r|\u00a0)*)+", EMPTY)
                ;
        final String head = p.title + (isNotBlank(author) ? " - di " + author : EMPTY) + "<br>" + HR_TOP ;
        List<String> rows = wordWrap(head);

        List<String> article = wordWrap(p.date.replaceAll("^(\\d\\d\\d\\d).(\\d\\d).(\\d\\d).*","$3/$2/$1") +
                " - " + content
        );
        rows.addAll(article);
        waitOff();
        int page = 1;
        int j = 0;
        boolean forward = true;
        while (j < rows.size()) {
            if (j>0 && j % screenLines == 0 && forward) {
                println();
                write(WHITE);
                print("-PAGE " + page + "-  SPACE=NEXT  -=PREV  .=EXIT");
                write(GREY3);

                resetInput(); int ch = readKey();
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

    protected void waitOn() {
        print("PLEASE WAIT...");
        flush();
    }

    protected void waitOff() {
        for (int i=0; i<14; ++i) write(DEL);
        flush();
    }

    protected static final byte[] LOGO_WORDPRESS = new byte[] {
        -104, -84, 32, 32, -84, 32, 32, 32, 32, 32, 32, 32, 32, -84, -94, 13,
        -68, -69, 32, 18, -65, -110, -84, 18, -94, -110, -65, 18, -95, -94, -110, -69,
        18, -84, -110, -65, 18, -95, -110, 32, -95, 18, -84, -110, -65, 18, -95, -94,
        -110, -84, 18, -94, -110, -66, 18, -65, -94, -110, 13, 32, -65, -65, -66, 18,
        -95, -110, 32, 18, -95, -95, -94, -110, -69, -95, 18, -95, -95, -94, -110, 32,
        18, -84, -110, -65, 18, -95, -110, -66, 32, 18, -94, -110, -69, -68, -65, 13,
        32, -68, -68, 32, 32, 18, -94, -110, -66, -68, 32, -66, 18, -94, -110, -66,
        -68, 32, 32, -66, -68, -68, 18, -94, -110, -68, 18, -94, -110, 32, 18, -94,
        -110, -66, 13
    };

    protected void drawLogo() {
        if (!equalsDomain(domain, originalDomain)) {
            final String normDomain = normalizeDomain(domain);
            gotoXY(25,1); write(WHITE); print(substring(normDomain, 0, 14));
            if (normDomain.length() > 14) {
                gotoXY(25, 2); print(substring(normDomain, 14, 28));
            }
            gotoXY(0,0);
            write(LOGO_WORDPRESS);
        } else {
            write(logo);
        }
        write(GREY3);
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
