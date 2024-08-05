package eu.sblendorio.bbs.tenants.ascii;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.blogger.Blogger;
import com.google.api.services.blogger.BloggerScopes;
import com.google.api.services.blogger.model.Post;
import com.google.api.services.blogger.model.PostList;
import com.rometools.utils.IO;
import eu.sblendorio.bbs.core.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static eu.sblendorio.bbs.core.Utils.*;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.math.NumberUtils.*;

@Hidden
public class GoogleBloggerProxyAscii extends AsciiThread {

    protected String HR_TOP;

    @Override
    public void initBbs() throws Exception {
        HR_TOP = StringUtils.repeat('-', getScreenColumns() - 1);
    }

    protected String blogUrl = "https://blogger.googleblog.com";
    protected String labels = null;
    protected boolean showTimestamp = true;
    protected byte[] logo = LOGO_BLOGGER;
    protected int logoSize = 1;
    protected int pageSize = 8;
    protected int screenLines;

    protected static final String CRED_FILE_PATH = System.getProperty("user.home") + File.separator + "credentials.json";

    protected GoogleCredential credential;
    protected Blogger blogger;
    protected String blogId;

    protected Map<Integer, Post> posts = null;

    public String disclaimer() {
        return null;
    }

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

    public boolean resizeable() { return true; }

    protected PageTokens pageTokens = new PageTokens();

    private String originalBlogUrl;

    public GoogleBloggerProxyAscii() {
        super();
    }

    public GoogleBloggerProxyAscii(String blogUrl) {
        this();
        this.blogUrl = blogUrl;
    }

    public GoogleBloggerProxyAscii(String blogUrl, byte[] logo) {
        this();
        this.blogUrl = blogUrl;
        this.logo = logo;
    }

    public void init() throws IOException {
        try {
            originalBlogUrl = blogUrl;
            cls();

            this.credential = GoogleCredential
                .fromStream(new FileInputStream(CRED_FILE_PATH))
                .createScoped(Arrays.asList(BloggerScopes.BLOGGER));

            this.blogger = new Blogger.Builder(new NetHttpTransport(), new JacksonFactory(), credential)
                .setApplicationName("Apple-1 BBS Builder - Blogger Proxy - " + this.getClass().getSimpleName())
                .build();

            changeBlogIdByUrl(this.blogUrl);
            pageTokens.reset();
        } catch (IOException e) {
            exitWithError();
        }
    }

    protected void changeBlogIdByUrl(String url) throws IOException {
        this.blogUrl = url;
        blogId = blogger.blogs().getByUrl(blogUrl).execute().getId();
    }

    public int getPageSize() {
        return this.pageSize;
    }
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public void doLoop() throws Exception {
        screenLines = getScreenRows() - 3 - logoSize;
        init();
        log("Blogger entering (" + blogUrl + ")");
        listPosts();
        while (true) {
            log("Blogger waiting for input");
            print(getScreenColumns() >= 40
                ? "(N+-)Page (H)elp (R)eload (.)Quit> "
                : "(N+-)Page (.)Quit> "
            );
            resetInput();
            flush(); String inputRaw = readLine();
            String input = lowerCase(trim(inputRaw));
            if (".".equals(input) || "exit".equals(input) || "quit".equals(input) || "q".equals(input)) {
                break;
            } else if ("help".equals(input) || "h".equals(input)) {
                help();
                listPosts();
                continue;
            } else if ("n".equals(input) || "n+".equals(input) || "+".equals(input)) {
                pageTokens.tokens.push(pageTokens.prev);
                pageTokens.prev = pageTokens.curr;
                pageTokens.curr = pageTokens.next;
                pageTokens.next = null;
                ++pageTokens.page;
                posts = null;
                listPosts();
                continue;
            } else if (("-".equals(input) || "n-".equals(input)) && pageTokens.page > 1) {
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
                    log("Sending '"+message+"' to #"+client);
                    int exitCode = send(client, message);
                    log("Message sent, exitCode="+exitCode+".");
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

    protected Map<Integer, Post> getPosts() throws IOException {
        Map<Integer, Post> result = new LinkedHashMap<>();

        Blogger.Posts.List action = isNotBlank(labels)
            ? blogger.posts().list(blogId).setLabels(labels).setPageToken(pageTokens.curr)
            : blogger.posts().list(blogId).setPageToken(pageTokens.curr);

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

    protected void listPosts() throws IOException {
        cls();
        drawLogo();
        if (posts == null) {
            posts = getPosts();
        }
        long totalRows = 0;
        for (Map.Entry<Integer, Post> entry: posts.entrySet()) {
            int i = entry.getKey();
            Post post = entry.getValue();
            print(i + ".");
            final int nCols = getScreenColumns() - 3;
            final int iLen = nCols-String.valueOf(i).length();
            String line = WordUtils.wrap(filterPrintable(htmlClean(post.getTitle())), iLen, "\r", true);
            totalRows += 1 + line.chars().filter(ch -> ch == '\r').count();
            println(line.replaceAll("\r", newlineString() + " " + repeat(" ", nCols-iLen)));
        }
        for (int i = 0; i < (getScreenRows() - totalRows  - logoSize - 2); ++i) newline();
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

    protected void help() throws IOException {
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

    protected void displayPost(int n) throws IOException {
        cls();
        drawLogo();
        final Post p = posts.get(n);
        String content = downstreamTransform(p.getContent()
                .replaceAll("(?is)[\n\r ]+", " ")
                .replaceAll("(?is)<style>.*?</style>", EMPTY)
                .replaceAll("(?is)<script[ >].*?</script>", EMPTY)
                .replaceAll("(?is)^[\\s\\n\\r]+|^\\s*(/?<(br|div|figure|iframe|img|p|h[0-9])[^>]*>\\s*)+", EMPTY)
                .replaceAll("(?is)^(\\s|\n|\r|\u00a0|&nbsp;)*", EMPTY)
                .replaceAll("(?is)^(<[^>]+>(\\s|\n|\r|\u00a0|&nbsp;)*)+", EMPTY)
                + (disclaimer()==null?"":"<br><br>"+disclaimer())
        ).replaceAll("(?is)<!--.*?-->","");

        final String head = p.getTitle() +
            "<br>" +
            HR_TOP +
            "<br>";
        List<String> rows = wordWrap(head);
        final String timestamp = showTimestamp
            ? p.getPublished().toString().replaceAll("^(\\d\\d\\d\\d).(\\d\\d).(\\d\\d).*","$3/$2/$1") + " - "
            : "";
        List<String> article = wordWrap(timestamp + content);
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

    protected static final byte[] LOGO_BLOGGER = "Blogger".getBytes(StandardCharsets.ISO_8859_1);

    protected void drawLogo() {
        if (!equalsDomain(blogUrl, originalBlogUrl)) {
            final String normDomain = normalizeDomain(blogUrl);
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

    protected void exitWithError() throws IOException {
        log("Missing file " + CRED_FILE_PATH + " on the server's filesystem");
        cls();
        drawLogo();
        newline();
        print(" "); print("       "); println(" Missing Google credentials on");
        print(" "); print(" ERROR "); println(" server's filesystem. Contact");
        print(" "); print("       "); println(" the system administrator.");
        newline();
        flush();
        throw new BbsIOException("Missing file " + CRED_FILE_PATH + " on the server's filesystem");
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
