package eu.sblendorio.bbs.tenants;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import eu.sblendorio.bbs.core.HtmlUtils;
import eu.sblendorio.bbs.core.PetsciiThread;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static eu.sblendorio.bbs.core.Colors.*;
import static eu.sblendorio.bbs.core.Keys.*;
import static eu.sblendorio.bbs.core.Utils.filterPrintable;
import static eu.sblendorio.bbs.core.Utils.filterPrintableWithNewline;
import static java.lang.Math.min;
import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

public class OpenOnline extends PetsciiThread {

    static String HR_TOP = StringUtils.repeat(chr(163), 39);

    protected int screenRows = 18;
    protected int pageSize = 6;

    protected List<NewsFeed> posts = emptyList();
    protected int currentPage = 1;

    protected boolean alwaysRefreshFeed = false;

    static class NewsSection {
        final String title;
        final String url;

        public NewsSection(String title, String url) {
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

    private static Pattern feedsPattern = Pattern.compile("<li class=\"tag ?([a-z\\-]+)?\"><a href=\"(https://www.open.online/[a-z\\-]+/)\">([A-Z\\s\\-\\']+)</a></li>");
    private Map<String, NewsSection> sections;

    private void readSections() throws Exception {
        sections = new LinkedHashMap<>();
        sections.put("0", new NewsSection("IN EVIDENZA", "https://www.open.online/rss.xml"));
        String indexHtml = httpGet("https://www.open.online/");
        Matcher m = feedsPattern.matcher(indexHtml);
        int i = 1;
        while (m.find()) {
            String line = m.group(0);
            Matcher matcher = feedsPattern.matcher(line);
            matcher.matches();
            String url = matcher.group(2) + "rss.xml";
            String title = matcher.group(3);
            sections.put(valueOf(i), new NewsSection(title, url));
            ++i;
        }
    }

    private void printChannelList() {
        gotoXY(0, 6);
        final String SPACES = "          ";
        for (Map.Entry<String, NewsSection> entry: sections.entrySet()) {
            print(SPACES); write(REVON); print(" " + entry.getKey()+ " ");
            write(REVOFF); println(" " + entry.getValue().title);
            newline();
        }
        print(SPACES); write(REVON); print(" . "); write(REVOFF); print(" ESCI ");
        flush();
    }

    public static List<NewsFeed> getFeeds(String urlString) throws Exception {
        URL url = new URL(urlString);
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(url));
        List<NewsFeed> result = new ArrayList<>();
        List<SyndEntry> entries = feed.getEntries();
        for (SyndEntry e : entries)
            result.add(new NewsFeed(e.getPublishedDate(),
                    e.getTitle().replace("\u00a0", " "),
                    e.getDescription().getValue(),
                    e.getUri(),
                    e.getAuthor().replace("\u00a0", " ")
            ));
        return result;
    }

    @Override
    public void doLoop() throws Exception {
        cls();
        write(GREY3);
        waitOn();
        readSections();
        waitOff();
        while (true) {
            write(WHITE, CLR, LOWERCASE, CASE_LOCK);
            write(LOGO_OPEN);
            posts = null;
            currentPage = 1;
            printChannelList();
            boolean isValidKey;
            int key;
            int input;
            do {
                resetInput();
                key = readKey();
                input = Character.getNumericValue(key);
                isValidKey = (input >= 0 && input < sections.keySet().size()) || key == '.';
            } while (!isValidKey);
            if (key == '.') break;
            NewsSection section = sections.get(valueOf(input));
            enterSection(section);
        }
    }

    private void enterSection(NewsSection section) throws Exception {
        listPosts(section);

        while (true) {
            log("OpenOnline waiting for input");
            write(WHITE); print("#"); write(GREY3); print(", ["); write(WHITE); print("+-"); write(GREY3); print("]Page [");
            write(WHITE); print("R"); write(GREY3); print("]eload [");
            write(WHITE); print("."); write(GREY3); print("]"); write(WHITE); print("Q"); write(GREY3); print("uit> ");
            resetInput();
            flush();
            String inputRaw = readLine();
            String input = lowerCase(trim(inputRaw));
            if (".".equals(input) || "exit".equals(input) || "quit".equals(input) || "q".equals(input)) {
                break;
            } else if ("+".equals(input) && currentPage*pageSize<posts.size()) {
                ++currentPage;
                if (alwaysRefreshFeed) posts = null;
                try {
                    listPosts(section);
                } catch (NullPointerException e) {
                    --currentPage;
                    if (alwaysRefreshFeed) posts = null;
                    listPosts(section);
                }
            } else if ("-".equals(input) && currentPage > 1) {
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

    protected void listPosts(NewsSection section) throws Exception {
        cls();
        gotoXY(23,2); write(WHITE); print(section.title);
        write(HOME); write(LOGO_SECTION);
        write(GREY3);
        if (isEmpty(posts)) {
            waitOn();
            posts = getFeeds(section.url);
            waitOff();
        }

        final int start = pageSize * (currentPage-1);
        final int end = min(pageSize + start, posts.size());

        for (int i = start; i < end; ++i) {
            NewsFeed post = posts.get(i);
            write(WHITE); print((i+1) + "."); write(GREY3);
            final int iLen = 37-String.valueOf(i+1).length();
            String line = WordUtils.wrap(filterPrintable(HtmlUtils.htmlClean(post.title)), iLen, "\r", true);
            println(line.replaceAll("\r", "\r " + repeat(" ", 37-iLen)));
        }
        newline();
        flush();
    }

    private boolean displayPost(NewsFeed feed, NewsSection section) throws Exception {
        logo(section);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        final String author = isBlank(trim(feed.author)) ? EMPTY : " - di " + trim(feed.author);
        final String head = trim(feed.title) + author + "<br>" + HR_TOP + "<br>";
        List<String> rows = wordWrap(head);
        List<String> article = wordWrap(dateFormat.format(feed.publishedDate) + " - " + feed.description.replaceAll("^[\\s\\n\\r]+|^(<(br|p)[^>]*>)+", EMPTY));
        rows.addAll(article);

        int page = 1;
        int j = 0;
        boolean forward = true;
        while (j < rows.size()) {
            if (j>0 && j % screenRows == 0 && forward) {
                println();
                write(WHITE);
                print("-PAGE " + page + "-  SPACE=NEXT  -=PREV  .=EXIT");
                write(GREY3);
                flush(); resetInput(); int ch = readKey();
                if (ch == '.') {
                    return true;
                } else if (ch == '-' && page > 1) {
                    j -= (screenRows *2);
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

    private void logo(NewsSection section) throws Exception {
        cls();
        gotoXY(23,2);
        write(WHITE);
        print(section.title);
        write(HOME);
        write(LOGO_SECTION);
        write(GREY3);
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

    public final static byte[] LOGO_OPEN = new byte[] {
            32, 32, 32, 32, 32, 32, 32, 32, 32, 18, 5, 32, 32, 32, 32, -94,
            -94, 32, 32, -94, -94, -69, 32, -94, -94, -94, 32, -94, 32, -84, -69, 32,
            32, 32, -110, 13, 32, 32, 32, 32, 32, 32, 32, 32, 32, 18, -95, 32,
            32, -110, -66, 18, -66, -68, -110, -68, 18, 32, -110, 32, 18, 32, -110, 32,
            18, 32, -110, 32, 18, 32, 32, 32, -110, 32, -68, -95, 18, -95, 32, 32,
            -110, -95, 13, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 18, 32, 32,
            -110, 32, 18, 32, 32, -110, 32, 18, 32, -110, 32, -94, 18, -66, 32, -110,
            32, -94, 18, -66, 32, -110, 32, 18, -68, -110, 32, 18, -95, 32, 32, -110,
            13, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 18, -95, 32, 32, -94,
            -110, -66, 18, -66, 32, -110, 32, 18, 32, 32, 32, -110, 32, 18, -94, -94,
            32, -110, 32, 18, 32, -110, -95, 18, -95, 32, -110, -95, 13, 32, 32, 32,
            32, 32, 32, 32, 32, 32, 32, 32, 18, -94, -94, -94, -94, -94, -94, -94,
            -94, -94, -94, -94, -94, -94, -94, -94, -94, -94, -94, -94, -110, 13
    };

    public final static byte[] LOGO_SECTION = new byte[] {
            18, 5, 32, 32, 32, 32, -94, -94, 32, 32, -94, -94, -69, 32, -94, -94,
            -94, 32, -94, 32, -84, -69, 32, 32, 32, -110, 13, 18, -95, 32, 32, -110,
            -66, 18, -66, -68, -110, -68, 18, 32, -110, 32, 18, 32, -110, 32, 18, 32,
            -110, 32, 18, 32, 32, 32, -110, 32, -68, -95, 18, -95, 32, 32, -110, -95,
            13, 32, 18, 32, 32, -110, 32, 18, 32, 32, -110, 32, 18, 32, -110, 32,
            -94, 18, -66, 32, -110, 32, -94, 18, -66, 32, -110, 32, 18, -68, -110, 32,
            18, -95, 32, 32, -110, 13, 32, 18, -95, 32, 32, -94, -110, -66, 18, -66,
            32, -110, 32, 18, 32, 32, 32, -110, 32, 18, -94, -94, 32, -110, 32, 18,
            32, -110, -95, 18, -95, 32, -110, -95, 13, 32, 32, 18, -94, -94, -94, -94,
            -94, -94, -94, -94, -94, -94, -94, -94, -94, -94, -94, -94, -94, -94, -94, -110, 13
    };

    protected void waitOn() {
        print("WAIT PLEASE...");
        flush();
    }

    protected void waitOff() {
        for (int i=0; i<14; ++i) write(DEL);
        flush();
    }

}
