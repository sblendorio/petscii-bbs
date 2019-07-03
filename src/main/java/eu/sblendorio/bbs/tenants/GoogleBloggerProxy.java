package eu.sblendorio.bbs.tenants;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.blogger.Blogger;
import com.google.api.services.blogger.BloggerScopes;
import com.google.api.services.blogger.model.Post;
import com.google.api.services.blogger.model.PostList;
import eu.sblendorio.bbs.core.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static eu.sblendorio.bbs.core.Colors.GREY3;
import static eu.sblendorio.bbs.core.Colors.LIGHT_RED;
import static eu.sblendorio.bbs.core.Colors.WHITE;
import static eu.sblendorio.bbs.core.Keys.*;
import static eu.sblendorio.bbs.core.Utils.*;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.math.NumberUtils.*;

@Hidden
public class GoogleBloggerProxy extends PetsciiThread {

    static String HR_TOP = StringUtils.repeat(chr(163), 39);

    protected String blogUrl = "https://blogger.googleblog.com";
    protected byte[] logo = LOGO_BLOGGER;
    protected int pageSize = 10;
    protected int screenRows = 19;

    protected final String CRED_FILE_PATH = System.getProperty("user.home") + File.separator + "credentials.json";

    protected GoogleCredential credential;
    protected Blogger blogger;
    protected String blogId;

    protected Map<Integer, Post> posts = null;

    protected static class PageTokens {
        Stack<String> tokens = new Stack<>();

        String prev = null;
        String curr = null;
        String next = null;
        int page = 1;

        public void reset() {
            prev=null; curr=null; next=null; page=1; tokens.clear();
        }
    }

    protected PageTokens pageTokens = new PageTokens();

    private String originalBlogUrl;

    public GoogleBloggerProxy() {}

    public GoogleBloggerProxy(String blogUrl) {
        this.blogUrl = blogUrl;
    }

    public GoogleBloggerProxy(String blogUrl, byte[] logo) {
        this.blogUrl = blogUrl;
        this.logo = logo;
    }

    public void init() throws IOException {
        try {
            originalBlogUrl = blogUrl;
            cls();
            write(GREY3);
            waitOn();
            final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
            final JsonFactory JSON_FACTORY = new JacksonFactory();

            this.credential = GoogleCredential
                    .fromStream(new FileInputStream(CRED_FILE_PATH))
                    .createScoped(Arrays.asList(BloggerScopes.BLOGGER));

            this.blogger = new Blogger.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName("PETSCII BBS Builder - Blogger Proxy - " + this.getClass().getSimpleName())
                    .build();

            changeBlogIdByUrl(this.blogUrl);
            pageTokens.reset();
            waitOff();
        } catch (IOException e) {
            exitWithError();
        }
    }

    protected void changeBlogIdByUrl(String url) throws IOException {
        this.blogUrl = url;
        blogId = blogger.blogs().getByUrl(blogUrl).execute().getId();
    }

    @Override
    public void doLoop() throws Exception {
        init();
        write(LOWERCASE, CASE_LOCK);
        log("Blogger entering (" + blogUrl + ")");
        listPosts();
        while (true) {
            log("Blogger waiting for input");
            write(WHITE);print("#"); write(GREY3);
            print(", [");
            write(WHITE); print("+-"); write(GREY3);
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
            } else if ("+".equals(input)) {
                pageTokens.tokens.push(pageTokens.prev);
                pageTokens.prev = pageTokens.curr;
                pageTokens.curr = pageTokens.next;
                pageTokens.next = null;
                ++pageTokens.page;
                posts = null;
                listPosts();
                continue;
            } else if ("-".equals(input) && pageTokens.page > 1) {
                pageTokens.next = pageTokens.curr;
                pageTokens.curr = pageTokens.prev;
                pageTokens.prev = pageTokens.tokens.pop();
                --pageTokens.page;
                posts = null;
                listPosts();
                continue;
            } else if ("--".equals(input) && pageTokens.page > 1) {
                pageTokens.reset();
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
                    System.out.println("Sending '"+message+"' to #"+client);
                    int exitCode = send(client, message);
                    System.out.println("Message sent, exitCode="+exitCode+".");
                }
            } else if (substring(input,0,5).equalsIgnoreCase("name ")) {
                String newName = defaultString(input.replaceAll("^name ([^\\s]+).*$", "$1"));
                changeClientName(newName);
            } else if (substring(input, 0, 8).equalsIgnoreCase("connect ")) {
                final String oldBlogUrl = blogUrl;
                final byte[] oldLogo = logo;
                String newUrl = trim(input.replaceAll("^connect ([^\\s]+).*$", "$1"));
                if (newUrl.indexOf('.') == -1) newUrl += ".blogspot.com";
                if (!newUrl.matches("(?is)^http.*")) newUrl = "https://" + newUrl;
                log("new blogUrl: "+newUrl);
                try {
                    changeBlogIdByUrl(newUrl);
                    pageTokens.reset();
                    posts = null;
                    listPosts();
                } catch (Exception e) {
                    log("BLOGGER FAILED: " + e.getClass().getName() + ": " + e.getMessage());
                    logo = oldLogo;
                    changeBlogIdByUrl(oldBlogUrl);
                    pageTokens.reset();
                    posts = null;
                    listPosts();
                }
            }
        }
        flush();
    }

    protected Map<Integer, Post> getPosts() throws Exception {
        Map<Integer, Post> result = new LinkedHashMap<>();

        Blogger.Posts.List action = blogger.posts().list(blogId).setPageToken(pageTokens.curr);
        action.setFields("items(author/displayName,id,content,published,title,url),nextPageToken");
        action.setMaxResults(Long.valueOf(pageSize));
        PostList list = action.execute();

        for (int i=0; i<list.getItems().size(); ++i) {
            Post post = list.getItems().get(i);
            result.put(i+1+(pageSize*(pageTokens.page-1)), post);
        }

        pageTokens.next = list.getNextPageToken();
        return result;
    }

    protected void listPosts() throws Exception {
        cls();
        logo();
        if (posts == null) {
            waitOn();
            posts = getPosts();
            waitOff();
        }
        for (Map.Entry<Integer, Post> entry: posts.entrySet()) {
            int i = entry.getKey();
            Post post = entry.getValue();
            write(WHITE); print(i + "."); write(GREY3);
            final int iLen = 37-String.valueOf(i).length();
            String line = WordUtils.wrap(filterPrintable(HtmlUtils.htmlClean(post.getTitle())), iLen, "\r", true);
            println(line.replaceAll("\r", "\r " + repeat(" ", 37-iLen)));
        }
        newline();
    }

    protected List<String> wordWrap(String s) {
        String[] cleaned = filterPrintableWithNewline(HtmlUtils.htmlClean(s)).split("\n");
        List<String> result = new ArrayList<>();
        for (String item: cleaned) {
            String[] wrappedLine = WordUtils
                    .wrap(item, 39, "\n", true)
                    .split("\n");
            result.addAll(asList(wrappedLine));
        }
        return result;
    }

    protected void help() throws Exception {
        cls();
        logo();
        println();
        println();
        println("Press any key to go back to posts");
        readKey();
    }

    protected void displayPost(int n) throws Exception {
        cls();
        logo();
        waitOn();
        final Post p = posts.get(n);
        String content = p.getContent()
                .replaceAll("(?is)<style>.*</style>", EMPTY)
                .replaceAll("(?is)<script .*</script>", EMPTY)
                .replaceAll("(?is)^[\\s\\n\\r]+|^\\s*(/?<(br|div|figure|iframe|img|p|h[0-9])[^>]*>\\s*)+", EMPTY)
                .replaceAll("(?is)^(<[^>]+>(\\s|\n|\r)*)+", EMPTY);
        final String head = p.getTitle() +
                "<br>" +
                HR_TOP +
                "<br>";
        List<String> rows = wordWrap(head);
        List<String> article = wordWrap(
                p.getPublished().toStringRfc3339().replaceAll("^(\\d\\d\\d\\d).(\\d\\d).(\\d\\d).*","$3/$2/$1") +
                 " - " +
                 content);
        rows.addAll(article);
        waitOff();

        int page = 1;
        int j = 0;
        boolean forward = true;
        while (j < rows.size()) {
            if (j>0 && j % screenRows == 0 && forward) {
                println();
                write(WHITE);
                print("-PAGE " + page + "-  SPACE=NEXT  -=PREV  .=EXIT");
                write(GREY3);

                resetInput(); int ch = readKey();
                if (ch == '.') {
                    listPosts();
                    return;
                } else if (ch == '-' && page > 1) {
                    j -= (screenRows *2);
                    --page;
                    forward = false;
                    cls();
                    logo();
                    continue;
                } else {
                    ++page;
                }
                cls();
                logo();
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

    public final static byte[] LOGO_BLOGGER = new byte[] {
        18, -127, 32, -94, -94, 32, 32, -110, 32, 5, -84, -94, 32, -69, 13, 18,
        -127, 32, -110, 32, 18, -94, -110, -68, 18, 32, -110, 32, 18, 5, -95, -110,
        -94, -66, -95, 18, -65, -110, -65, -84, 18, -94, -110, -69, 18, -65, -110, -65,
        -84, 18, -69, -110, -69, 18, -68, -110, -66, 13, 18, -127, 32, -110, 32, 18,
        -94, -110, -65, 18, 32, -110, 32, 18, 5, -95, -110, -94, -66, -95, -65, 18,
        -65, -110, -68, -94, -95, -65, 18, -66, -110, -68, 18, -68, -110, 32, -95, 13,
        18, -127, -94, -94, -94, -94, -94, -110, 32, 32, 32, 32, 32, 32, 32, 32,
        5, -94, -66, -84, 18, -65, -110, 13
    };

    protected void logo() throws IOException {
        if (!equalsDomain(blogUrl, originalBlogUrl)) {
            final String normDomain = normalizeDomain(blogUrl);
            gotoXY(23,1); write(WHITE); print(substring(normDomain, 0, 16));
            if (normDomain.length() > 14) {
                gotoXY(23, 2); print(substring(normDomain, 16, 32));
            }
            gotoXY(0,0);
            write(LOGO_BLOGGER);
        } else {
            write(logo);
        }
        write(GREY3);
    }

    protected void listClients() throws Exception {
        cls();
        println("You are #" + getClientId() + ": "+getClientName() + " [" + getClientClass().getSimpleName() + "]");
        newline();
        for (Map.Entry<Long, PetsciiThread> entry: clients.entrySet())
            if (entry.getKey() != getClientId())
                println("#" + entry.getKey() +": "+entry.getValue().getClientName() + " [" + entry.getValue().getClientClass().getSimpleName() + "]");
        println();
    }

    protected void exitWithError() throws IOException {
        log("Missing file " + CRED_FILE_PATH + " on the server's filesystem");
        cls();
        logo();
        newline(); write(REVOFF);
        print(" "); write(LIGHT_RED, REVON); print("       "); write(WHITE, REVOFF); println(" Missing Google credentials on");
        print(" "); write(LIGHT_RED, REVON); print(" ERROR "); write(WHITE, REVOFF); println(" server's filesystem. Contact");
        print(" "); write(LIGHT_RED, REVON); print("       "); write(WHITE, REVOFF); println(" the system administrator.");
        newline();
        flush();
        throw new CbmIOException("Missing file " + CRED_FILE_PATH + " on the server's filesystem");
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
