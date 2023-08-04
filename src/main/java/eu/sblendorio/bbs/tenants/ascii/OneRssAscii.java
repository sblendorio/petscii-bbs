package eu.sblendorio.bbs.tenants.ascii;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.BbsThread;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.text.WordUtils;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static eu.sblendorio.bbs.core.Utils.readTxt;
import static java.lang.Math.min;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

public class OneRssAscii extends AsciiThread {

    String HR_TOP;

    protected int screenRows;
    protected int pageSize = 10;

    protected boolean showAuthor = false;
    protected boolean newlineAfterDate = true;
    protected boolean twoColumns = true;

    protected List<NewsFeed> posts = emptyList();
    protected int currentPage = 1;

    protected boolean alwaysRefreshFeed = false;

    static class NewsSection {
        final String title;
        final Object url;

        public NewsSection(String title, Object url) {
            this.title = title; this.url = url;
        }
    }

    static class NewsFeed {
        final Date publishedDate;
        final String title;
        final String description;
        final String uri;
        final String author;

        public NewsFeed(Date publishedDate, String title, String description, String uri, String author) {
            this.publishedDate = publishedDate; this.title = title; this.description = description; this.uri = uri; this.author = author;
        }

        public String toString() {
            return "Title: "+title+"\nDate:"+publishedDate+"\nDescription:"+description+"\n";
        }
    }

    protected Map<String, NewsSection> sections;

    @Override
    public void initBbs() throws Exception {
        HR_TOP = StringUtils.repeat('-', getScreenColumns() - 1);
        screenRows = getScreenRows() - 4;
    }

    public int getPageSize() {
        return this.pageSize;
    }
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    protected void readSections() throws Exception {
        final String filename = System.getProperty("MENUMES", "/data/a.txt");
        List<String> secTxt = readTxt(filename);
        sections = new LinkedHashMap<>();
        Map<String, String> config = secTxt.stream()
            .filter(row -> isNotBlank(trim(row)))
            .map(StringUtils::trim)
            .filter(row -> !row.startsWith(";"))
            .map(row -> row.replaceAll("\\s*#\\s*", "#"))
            .map(row -> row.split("#"))
            .collect(toMap(rows -> rows[0], rows -> rows[1], (a,b) -> b, LinkedHashMap::new));
        final String commands = "123456789abcdefghijklmnopqrstuvwxyz";
        int count = 0;
        for (Map.Entry<String, String> row : config.entrySet()) {
            ++count;
            sections.put(commands.substring(count - 1, count), new NewsSection(row.getKey(), row.getValue()));
        }
        sections.put(commands.substring(count, ++count), new NewsSection("Televideo", new TelevideoRaiAscii()));
        //sections.put(commands.substring(count, ++count), new NewsSection("Connect 4", new ConnectFour()));

        final String filenameConfig = System.getProperty("CONFIGMES", "/data/c.txt");
        Map<String, String> conf = readTxt(filenameConfig).stream()
            .filter(row -> isNotBlank(trim(row)))
            .filter(row -> row.contains("="))
            .map(StringUtils::trim)
            .filter(row -> !row.startsWith(";"))
            .map(row -> row.replaceAll("\\s*=\\s*", "="))
            .map(row -> row.split("="))
            .collect(toMap(row -> row[0], row -> row[1], (a, b) -> a));

        if (conf.get("rss.pagesize") != null) {
            pageSize = NumberUtils.toInt(conf.get("rss.pagesize"));
        }
    }

    private void printChannelListOneColumn() {
        int maxLen = sections.values().stream().map(x -> x.title).map(String::length).mapToInt(v -> v+4).max().orElse(0);
        String spaces = StringUtils.repeat(" ", (getScreenColumns() - maxLen) / 2);
        for (Map.Entry<String, NewsSection> entry: sections.entrySet()) {
            print(spaces); print(" " + entry.getKey().toUpperCase() + " ");
            println(" " + entry.getValue().title);
            if (getScreenRows() > 20) newline();
        }
        print(spaces); print(" . "); println(" Exit ");
        newline();
        flush();
    }

    private void printChannelListTwoColumns() {
        List<String> keys = new LinkedList<>(sections.keySet());
        int size = sections.size() / 2;
        if (size * 2 < sections.size())
            ++size;
        for (int i=0; i<size; ++i) {
            int even = i;
            if (even >= keys.size()) break;
            String key = keys.get(even);
            NewsSection value = sections.get(key);
            print("  ");
            print(key.toUpperCase());
            print("  ");
            String title = substring(value.title + "                    ", 0, 15);
            print(title);
            print(" ");

            int odd = even + size;
            if (odd < keys.size()) {
                key = keys.get(odd);
                value = sections.get(key);
                print(" ");
                print(key.toUpperCase());
                print("  ");
                print(value.title);
            }
            if (i != size -1) {
                newline();
                if (size <= 8) newline();
            } else if (sections.size() % 2 == 0) {
                newline();
                if (size <= 8) newline();
            }

        }
        if (sections.size() % 2 == 0) write(' ');
        print(" ");
        print(".");
        print("  ");
        print("Exit");
        println();
        println();
        flush();
    }

    public List<NewsFeed> getFeeds(String urlString) throws Exception {
        URL url = new URL(urlString);
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(url));
        List<NewsFeed> result = new ArrayList<>();
        List<SyndEntry> entries = feed.getEntries();
        for (SyndEntry e : entries)
            result.add(new NewsFeed(e.getPublishedDate(),
                e.getTitle().replace("\u00a0", " "),
                getArticleBody(e),
                e.getUri(),
                getAuthor(e)
            ));
        return result;
    }


    public String getAuthor(SyndEntry e) {
        return e.getAuthor().replace("\u00a0", " ");
    }

    public String getArticleBody(SyndEntry e) {
        return e.getDescription().getValue();
    }

    @Override
    public void doLoop() throws Exception {
        cls();
        readSections();
        if (sections.size() == 1) {
            enterSection(sections.values().stream().findFirst().get());
            return;
        }
        while (true) {
            cls();
            write(LOGO_MENU);
            println();
            println();
            posts = null;
            currentPage = 1;
            if (twoColumns)
                printChannelListTwoColumns();
            else
                printChannelListOneColumn();
            flush();
            boolean isValidKey;
            int key;
            String input;
            do {
                resetInput();
                key = readKey();
                if (key >= 193 && key <= 218) key -= 128;
                input = chr(key) + "";
                isValidKey = (sections.keySet().stream().map(String::toLowerCase).collect(toSet()).contains(input.toLowerCase())) || key == '.';
            } while (!isValidKey);
            if (key == '.') break;
            NewsSection section = sections.get(input.toLowerCase());
            enterSection(section);
        }
        cls();
    }

    private void enterSection(NewsSection section) throws Exception {
        boolean keepGoing = listPosts(section);
        if (!keepGoing) return;
        if (section.url instanceof BbsThread)
            return;

        while (true) {
            log("RssReader waiting for input");
            print(getScreenColumns() >= 40
                ? "#, (N+-)Page (R)eload (.)Quit> "
                : "(N+-)Page (.)Quit> "
            );
            resetInput();
            flush();
            String inputRaw = readLine();
            String input = lowerCase(trim(inputRaw));
            if (".".equals(input) || "exit".equals(input) || "quit".equals(input) || "q".equals(input)) {
                break;
            } else if (("+".equals(input) || "n".equals(input) || "n+".equals(input)) && currentPage*pageSize<posts.size()) {
                ++currentPage;
                if (alwaysRefreshFeed) posts = null;
                try {
                    listPosts(section);
                } catch (NullPointerException e) {
                    --currentPage;
                    if (alwaysRefreshFeed) posts = null;
                    listPosts(section);
                }
            } else if (("-".equals(input) || "n-".equals(input)) && currentPage > 1) {
                --currentPage;
                if (alwaysRefreshFeed) posts = null;
                listPosts(section);
            } else if ("--".equals(input)) {
                currentPage = 1;
                if (alwaysRefreshFeed) posts = null;
                listPosts(section);
            } else if ("r".equals(input) || "reload".equals(input) || "refresh".equals(input)) {
                posts = null;
                listPosts(section);
            } else if (toInt(input) >= 1 && toInt(input) <= posts.size()) {
                boolean exitByUser = displayPost(posts.get(toInt(input) - 1), section);
                if (exitByUser) listPosts(section);
            } else if ("".equals(input)) {
                listPosts(section);
            }
        }
    }

    protected boolean listPosts(NewsSection section) throws Exception {
        final int mainLogoSize = 2;
        if (section.url instanceof BbsThread) {
            launch((BbsThread) section.url);
            return true;
        }
        cls();
        write(LOGO_SECTION);
        if (isNotBlank(section.title)) {
            print(" - ");
            print(section.title);
        }
        println();
        println();
        if (isEmpty(posts)) {
            posts = getFeeds(section.url.toString());
        }
        if (posts != null && posts.size() == 1) {
            displayPost(posts.get(0), section);
            return false;
        }
        final int start = pageSize * (currentPage-1);
        final int end = min(pageSize + start, posts.size());

        long totalRows = 0;
        for (int i = start; i < end; ++i) {
            System.out.println("i = "+i);
            NewsFeed post = posts.get(i);
            print((i+1) + ".");
            final int iLen = (getScreenColumns()-3)-String.valueOf(i+1).length();
            String line = WordUtils.wrap(filterPrintable(htmlClean(post.title)), iLen, "\r", true);
            totalRows += 1 + line.chars().filter(ch -> ch == '\r').count();
            println(line.replaceAll("\r", newlineString() +" " + repeat(" ", (getScreenColumns()-3)-iLen)));
        }
        for (int i = 0; i <= (getScreenRows() - totalRows - mainLogoSize - 2); ++i) newline();
        flush();
        return true;
    }

    private boolean displayPost(NewsFeed feed, NewsSection section) throws Exception {
        logo(section);
        List<String> rows = feedToText(feed);

        int page = 1;
        int j = 0;
        boolean forward = true;
        while (j < rows.size()) {
            if (j > 0 && j % screenRows == 0 && forward) {
                println();
                print(getScreenColumns() >= 40
                        ? "-PAGE " + page + "-  SPACE=NEXT  -=PREV  .=EXIT"
                        : "(" + page + ") SPC -PREV .EXIT");
                flush();
                resetInput();
                int ch = readKey();
                if (ch == '.') {
                    return true;
                } else if (ch == '-' && page > 1) {
                    j -= (screenRows * 2);
                    --page;
                    forward = false;
                    logo(section);
                    continue;
                } else {
                    ++page;
                }
                logo(section);
            }
            String row = rows.get(j);
            println(row);
            forward = true;
            ++j;
        }
        println();
        return false;
    }

    private List<String> feedToText(NewsFeed feed) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String author = (!this.showAuthor || StringUtils.isBlank(StringUtils.trim(feed.author))) ? "" : (" - by " + StringUtils.trim(feed.author));
        String head = StringUtils.trim(feed.title) + author + "<br>" + this.HR_TOP + "<br>";
        List<String> rows = wordWrap(head);
        List<String> article = wordWrap((
            (feed.publishedDate == null) ? "" : (
                dateFormat.format(feed.publishedDate) + " - " + (this.newlineAfterDate ? "<br>" : ""))) + feed.description
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

    private void logo(NewsSection section) throws Exception {
        cls();
        write(LOGO_SECTION);
        if (isNotBlank(section.title)) {
            print(" - ");
            print(section.title);
        }
        println();
        println();
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

    public byte[] LOGO_MENU = "LOGO_MENU".getBytes(ISO_8859_1);

    public byte[] LOGO_SECTION = "LOGO_SECTION".getBytes(ISO_8859_1);

}
