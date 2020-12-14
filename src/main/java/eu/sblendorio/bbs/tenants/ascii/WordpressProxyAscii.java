package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.BbsThread;
import static eu.sblendorio.bbs.core.Utils.equalsDomain;
import static eu.sblendorio.bbs.core.Utils.normalizeDomain;
import eu.sblendorio.bbs.tenants.petscii.WordpressProxy;
import java.io.IOException;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static org.apache.commons.collections4.MapUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.apache.commons.lang3.StringUtils.substring;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.lang3.math.NumberUtils.toInt;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.core.HtmlUtils;
import eu.sblendorio.bbs.core.AsciiThread;

@Hidden
public class WordpressProxyAscii extends AsciiThread {

    String HR_TOP;

    @Override
    public void initBbs() throws Exception {
        HR_TOP = StringUtils.repeat('-', getScreenColumns() - 1);
    }

    private static final Logger logger = LoggerFactory.getLogger(WordpressProxy.class);

    static class Post {
        long id;
        String title;
        String date;
        String content;
        String excerpt;
        Long authorId;
    }

    protected String domain = "https://wordpress.org/news";
    protected byte[] logo = LOGO_WORDPRESS;
    protected int pageSize = 8;
    protected int screenLines;
    protected boolean showAuthor = false;

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

    public WordpressProxyAscii(String domain, byte[] logo) {
        this();
        this.domain = domain;
        this.logo = logo;
    }

    protected final String getApi() { return domain + "/wp-json/wp/v2/"; }

    @Override
    public void doLoop() throws Exception {
        screenLines = getScreenRows() - 4;
        originalDomain = domain;
        log("Wordpress entering (" + domain + ")");
        listPosts();
        while (true) {
            log("Wordpress waiting for input");
            print("(");
            print("N+-");
            print(")Page (");
            print("H");
            print(")elp (");
            print("R");
            print(")eload (");
            print(".");
            print(")");
            print("Q");
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
            } else if ("n".equals(input) || "N".equals(input) || "+".equals(input)) {
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
            } else if ("-".equals(input) && currentPage > 1) {
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

    protected Map<Integer, Post> getPosts(int page, int perPage) throws Exception {
        if (page < 1 || perPage < 1) return null;
        Map<Integer, Post> result = new LinkedHashMap<>();
        JSONArray posts = (JSONArray) httpGetJson(getApi() + "posts?context=view&page="+page+"&per_page="+perPage);
        for (int i=0; i<posts.size(); ++i) {
            Post post = new Post();
            JSONObject postJ = (JSONObject) posts.get(i);
            post.id = (Long) postJ.get("id");
            post.content = ((String) ((JSONObject) postJ.get("content")).get("rendered")).replaceAll("(?is)(\\[/?vc_[^]]*\\])*", EMPTY);
            post.title = (String) ((JSONObject) postJ.get("title")).get("rendered");
            post.date = ((String) postJ.get("date")).replace("T", SPACE).replaceAll(":\\d\\d\\s*$", EMPTY);
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

        for (Map.Entry<Integer, Post> entry: posts.entrySet()) {
            int i = entry.getKey();
            Post post = entry.getValue();
            print(i + ".");
            final int nCols = getScreenColumns() - 3;
            final int iLen = nCols-String.valueOf(i).length();
            String line = WordUtils.wrap(filterPrintable(HtmlUtils.htmlClean(post.title)), iLen, "\r", true);
            println(line.replaceAll("\r", newlineString() + " " + repeat(" ", nCols-iLen)));
        }
        newline();
    }

    protected List<String> wordWrap(String s) {
        String[] cleaned = filterPrintableWithNewline(HtmlUtils.htmlClean(s)).split("\n");
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

    protected void displayPost(int n) throws Exception {
        cls();
        drawLogo();

        String author = null;
        final Post p = posts.get(n);

        try {
            if (showAuthor) {
                JSONObject authorJ = (JSONObject) httpGetJson(getApi() + "users/" + p.authorId);
                author = authorJ.get("name").toString();
            }
        } catch (Exception e) {
            log("Error during retrieving author");
            logger.error("Error during retrieving author", e);
        }
        final String content = p.content
            .replaceAll("(?is)<style>.*</style>", EMPTY)
            .replaceAll("(?is)<script .*</script>", EMPTY)
            .replaceAll("(?is)^[\\s\\n\\r]+|^\\s*(</?(br|div|figure|iframe|img|p|h[0-9])[^>]*>\\s*)+", EMPTY)
            .replaceAll("(?is)^(<[^>]+>(\\s|\n|\r)*)+", EMPTY);
        final String head = p.title + (isNotBlank(author) ? " - di " + author : EMPTY) + "<br>" + HR_TOP ;
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
                println();
                print("-PAGE " + page + "-  SPACE=NEXT  -=PREV  .=EXIT");

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