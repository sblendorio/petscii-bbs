package eu.sblendorio.bbs.tenants.ascii;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.core.HtmlUtils;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import static java.util.Arrays.asList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.apache.commons.lang3.StringUtils.substring;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.lang3.math.NumberUtils.toLong;
import org.apache.commons.text.WordUtils;

@Hidden
public abstract class RssAscii extends AsciiThread {

    private String timeOutProperty = "rss.a1.timeout";
    private String timeOutPropertyDefault = "40000";

    protected int logoHeightMenu = 3;
    protected int logoHeightNews = 2;

    public RssAscii() {
        super();
    }

    public RssAscii(String timeOutProperty, String timeOutPropertyDefault) {
        this();
        this.timeOutProperty = timeOutProperty;
        this.timeOutPropertyDefault = timeOutPropertyDefault;
        this.timeout = toLong(System.getProperty(timeOutProperty, timeOutPropertyDefault));
    }

    public abstract byte[] getLogo();

    public abstract String prefix();

    public abstract Map<String, NewsSection> sections();
    
    protected int pageRows;
    protected long timeout;

    String HR_TOP;

    public byte[] hrDash = "|".getBytes(ISO_8859_1);


    @Override
    public void initBbs() throws Exception {
        HR_TOP = repeat('|', this.getScreenColumns() - 1);
    }

    static class NewsSection {
        final String title;
        final String url;
        final byte[] logo;

        public NewsSection(String title, String url, byte[] logo) {
            this.title = title; this.url = url; this.logo = logo;
        }
    }

    static class NewsFeed {
        final Date publishedDate;
        final String title;
        final String description;
        final String uri;

        public NewsFeed(Date publishedDate, String title, String description, String uri) {
            this.publishedDate = publishedDate; this.title = title; this.description = description; this.uri = uri;
        }

        @Override
        public String toString() {
            return "Title: "+title+"\nDate:"+publishedDate+"\nDescription:"+description+"\n";
        }
    }

    private void printChannelList() {
        List<String> keys = new LinkedList<>(sections().keySet());
        int size = sections().size() / 2;
        if (size * 2 < sections().size())
            ++size;
        for (int i=0; i<size; ++i) {
            int even = i;
            if (even >= keys.size()) break;
            String key = keys.get(even);
            NewsSection value = sections().get(key);
            write(' ');
            print(String.format("%3s", key)); write(' ', ' ');
            String title = substring(value.title + "                    ", 0, 12);
            print(title);
            print(" ");
            if (getScreenColumns() > 40)
                print("                    ");
            else if (getScreenColumns() < 40)
                println();

            int odd = even + size;
            if (odd < keys.size()) {
                key = keys.get(odd);
                value = sections().get(key);
                write(' '); print(String.format("%3s", key)); write(' ', ' '); print(value.title);
            } else {
                //write(' '); print(" . "); write(' ', ' '); print("Exit");
            }
            newline();
        }
        write(' '); print(" . "); write(' ', ' '); print("Exit");
        for (int i=0; i<getScreenRows()-size*(getScreenColumns() < 40 ? 2 : 1)-logoHeightMenu-2; ++i) newline();
        newline();
        flush();
    }

    public static List<NewsFeed> getFeeds(String urlString) throws IOException, FeedException {
        URL url = new URL(urlString);
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(url));
        List<NewsFeed> result = new LinkedList<>();
        List<SyndEntry> entries = feed.getEntries();
        for (SyndEntry e : entries) {
            result.add(new NewsFeed(
                e.getPublishedDate(),
                e.getTitle(),
                ofNullable(e.getDescription()).map(SyndContent::getValue).orElse(""),
                e.getUri()
            ));
        }
        return result;
    }

    @Override
    public void doLoop() throws Exception {
        pageRows = getScreenRows() - logoHeightNews - 2;
        log("Entered Rss-Ascii: " + this.getClass().getSimpleName());
        while (true) {
            cls();
            drawLogo();
            printChannelList();
            String command = null;
            NewsSection choice;
            boolean inputFail;
            do {
                print(" > ");
                flush();
                resetInput();
                command = readLine(3);
                choice = sections().get(command);
                inputFail = (choice == null && !trim(command).equals("."));
                if (inputFail) {
                    // input wrong
                }
            } while (inputFail);
            if (trim(command).equals(".")) {
                break;
            }
            log("Rss(" + this.getClass().getSimpleName() + ") choice = " + command + " " + (choice == null ? EMPTY : choice.title));
            view(choice);
        }
        log("Rss-Ascii-EXIT: " + this.getClass().getSimpleName());
    }

    private void view(NewsSection section) throws IOException, FeedException {
        if (section == null) {
            return;
        }

        boolean interruptByUser;
        do {
            cls();
            List<NewsFeed> feeds = getFeeds(section.url);
            String text = EMPTY;
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            for (NewsFeed feed : feeds) {
                String description = HtmlUtils.htmlClean(feed.description).trim();
                description = StringUtils.isBlank(description) ? "&c64nbsp;" : description;

                String post = EMPTY;
                post += ""+feed.title + "<br>" + HR_TOP + "<br>";
                post += (feed.publishedDate == null ? "" : dateFormat.format(feed.publishedDate) + " ");
                post += description + "<br>";
                int lineFeeds = (pageRows - (wordWrap(post).length % pageRows)) % pageRows;

                post += repeat("&c64nbsp;<br>", lineFeeds);
                text += post;
            }

            interruptByUser = displayText(text, pageRows, section.logo);
            if (!interruptByUser) {
                print(" ENTER = MAIN MENU                    ");
                flush(); resetInput();
                int finalKey = keyPressed(timeout);
                interruptByUser = finalKey != -1;
            }
        } while (!interruptByUser);
    }

    protected boolean displayText(String text, int screenRows, byte[] logo) throws IOException {
        cls();
        write(defaultIfNull(logo, getLogo()));

        String[] rows = wordWrap(text);
        int page = 1;
        int j = 0;
        boolean forward = true;
        while (j < rows.length) {
            if (j>0 && j % screenRows == 0 && forward) {
                println();
                print(getScreenColumns() >= 40
                    ? "-PAGE " + page + "-  SPACE=NEXT  -=PREV  .=EXIT"
                    : "(" + page + ") SPC -PREV .EXIT"
                );
                resetInput();
                int ch = keyPressed(timeout);
                if (getLocalEcho() && isPrintableChar(ch)) write(ch);
                if (ch == '.') {
                    return true;
                }
                if (ch == '-' && page > 1) {
                    j -= (screenRows * 2);
                    --page;
                    forward = false;
                    cls();
                    write(logo == null ? getLogo() : logo);
                    continue;
                } else {
                    ++page;
                }
                cls();
                write(logo == null ? getLogo() : logo);
            }
            String row = rows[j];
            rssPrintln(row.replace("&c64nbsp;", EMPTY));
            forward = true;
            ++j;
        }
        println();
        return false;
    }

    void rssPrintln(String msg) {
        msg = msg == null ? "" : msg;
        for (byte ch: msg.getBytes(StandardCharsets.ISO_8859_1)) {
            if (ch == '|')
                write(hrDash);
            else
                write(ch);
        }
        newline();
    }

    protected String[] wordWrap(String s) {
        String[] cleaned = filterPrintableWithNewline(HtmlUtils.htmlClean(s).trim()).replaceAll(" +", " ").split("\n");
        List<String> result = new LinkedList<>();
        for (String item: cleaned) {
            String[] wrappedLine = WordUtils
                .wrap(item, getScreenColumns() - 1, "\n", true)
                .split("\n");
            result.addAll(asList(wrappedLine));
        }
        return Arrays.copyOf(result.toArray(), result.size(), String[].class);
    }

    private void drawLogo() {
        write(getLogo());
    }

}
