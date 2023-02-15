package eu.sblendorio.bbs.tenants.petscii;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.core.HtmlUtils;
import static eu.sblendorio.bbs.core.PetsciiColors.GREY3;
import static eu.sblendorio.bbs.core.PetsciiColors.WHITE;
import static eu.sblendorio.bbs.core.PetsciiKeys.DEL;
import static eu.sblendorio.bbs.core.PetsciiKeys.RETURN;
import static eu.sblendorio.bbs.core.PetsciiKeys.REVOFF;
import static eu.sblendorio.bbs.core.PetsciiKeys.REVON;
import static eu.sblendorio.bbs.core.PetsciiKeys.RIGHT;
import static eu.sblendorio.bbs.core.PetsciiKeys.SPACE_CHAR;
import static eu.sblendorio.bbs.core.PetsciiKeys.UP;
import eu.sblendorio.bbs.core.PetsciiThread;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import static java.util.Arrays.asList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.substring;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.lang3.math.NumberUtils.toLong;
import org.apache.commons.text.WordUtils;

@Hidden
public abstract class RssPetscii extends PetsciiThread {
    static final String HR_TOP = StringUtils.repeat(chr(163), 39);

    private String timeOutProperty = "rss.petscii.timeout";
    private String timeOutPropertyDefault = "50000";

    protected int pageRows;
    protected long timeout;

    protected int logoHeightNews = 2;


    public RssPetscii() {
        super();
    }

    public RssPetscii(String timeOutProperty, String timeOutPropertyDefault) {
        this();
        this.timeOutProperty = timeOutProperty;
        this.timeOutPropertyDefault = timeOutPropertyDefault;
        this.timeout = toLong(System.getProperty(timeOutProperty, timeOutPropertyDefault));
    }

    static class NewsSection {
        final int color;
        final String title;
        final String url;
        final byte[] logo;

        public NewsSection(int color, String title, String url, byte[] logo) {
            this.color = color; this.title = title; this.url = url; this.logo = logo;
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

    public abstract Map<String, RssPetscii.NewsSection> sections();

    public abstract String prefix();

    public abstract byte[] getLogo();

    private void printChannelList() {
        gotoXY(0, 5);
        List<String> keys = new LinkedList<>(sections().keySet());
        Collections.sort(keys);
        int size = sections().size() / 2;
        if (size * 2 < sections().size())
            ++size;
        for (int i=0; i<size; ++i) {
            int even = i;
            if (even >= keys.size()) break;
            String key = keys.get(even);
            NewsSection value = sections().get(key);
            write(RIGHT, value.color, REVON, SPACE_CHAR);
            print(String.format("%3s", key)); write(SPACE_CHAR, REVOFF, SPACE_CHAR);
            String title = substring(value.title + "                    ", 0, 12);
            print(title);
            print(" ");

            int odd = even + size;
            if (odd < keys.size()) {
                key = keys.get(odd);
                value = sections().get(key);
                write(value.color, REVON, SPACE_CHAR);
                print(String.format("%3s", key));
                write(SPACE_CHAR, REVOFF, SPACE_CHAR);
                print(value.title);
            } else {
                // write(WHITE, REVON, SPACE_CHAR);
                // print(" . ");
                // write(SPACE_CHAR, REVOFF, SPACE_CHAR);
                // print("Exit");
            }
            newline();
            if (size <= 8) newline();

        }
        write(RIGHT, WHITE, REVON, SPACE_CHAR);
        print(" . ");
        write(SPACE_CHAR, REVOFF, SPACE_CHAR);
        print("Exit");
        write(GREY3, RETURN, RETURN);
        flush();
    }

    public static List<NewsFeed> getFeeds(String urlString) throws IOException, FeedException {
        URL url = new URL(urlString);
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(url));
        List<NewsFeed> result = new LinkedList<>();
        List<SyndEntry> entries = feed.getEntries();
        for (SyndEntry e : entries)
            result.add(new NewsFeed(
                e.getPublishedDate(),
                e.getTitle(),
                ofNullable(e.getDescription()).map(SyndContent::getValue).orElse(""),
                e.getUri()
            ));
        return result;
    }

    @Override
    public void doLoop() throws Exception {
        pageRows = getScreenRows() - logoHeightNews - 2;
        log("Entered Rss-Petscii " + this.getClass().getSimpleName());
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
                    write(UP); println("        "); write(UP);
                }
            } while (inputFail);
            if (trim(command).equals(".")) {
                break;
            }
            log("RssPetscii "+this.getClass().getSimpleName()+" choice = " + command + " " + (choice == null ? EMPTY : choice.title));
            view(choice);
        }
        log("RssPetscii-EXIT");
    }

    private void view(NewsSection section) throws IOException, FeedException {
        if (section == null) {
            return;
        }

        boolean interruptByUser;
        do {
            cls();
            waitOn();
            List<NewsFeed> feeds = getFeeds(section.url);
            String text = EMPTY;
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            for (NewsFeed feed : feeds) {
                String description = htmlClean(feed.description).trim();
                description = StringUtils.isBlank(description) ? "&c64nbsp;" : description;

                String post = EMPTY;
                post += feed.title + "<br>" + HR_TOP + "<br>";
                post += feed.publishedDate == null ? "" : (dateFormat.format(feed.publishedDate) + " ");
                post += description + "<br>";
                int lineFeeds = (pageRows - (wordWrap(post).length % pageRows)) % pageRows;

                post += StringUtils.repeat("&c64nbsp;<br>", lineFeeds);
                text += post;
            }
            waitOff();

            interruptByUser = displayText(text, pageRows, section.logo);
            if (!interruptByUser) {
                gotoXY(0, 24); write(WHITE); print(" ENTER = MAIN MENU                    ");
                flush(); resetInput();
                int finalKey = keyPressed(timeout);
                interruptByUser = finalKey != -1;
            }
        } while (!interruptByUser);
    }

    protected boolean displayText(String text, int pageRows, byte[] logo) throws IOException {
        cls();
        write(defaultIfNull(logo, getLogo()));
        write(GREY3);

        String[] rows = wordWrap(text);
        int page = 1;
        int j = 0;
        boolean forward = true;
        while (j < rows.length) {
            if (j>0 && j % pageRows == 0 && forward) {
                println();
                write(WHITE); print("-PAGE " + page + "-  SPACE=NEXT  -=PREV  .=EXIT"); write(GREY3);
                resetInput();
                int ch = keyPressed(timeout);
                if (ch == '.') {
                    return true;
                }
                if (ch == '-' && page > 1) {
                    j -= (pageRows * 2);
                    --page;
                    forward = false;
                    cls();
                    write(logo == null ? getLogo() : logo);
                    write(GREY3);
                    continue;
                } else {
                    ++page;
                }
                cls();
                write(logo == null ? getLogo() : logo);
                write(GREY3);
            }
            String row = rows[j];
            println(row.replace("&c64nbsp;", EMPTY));
            forward = true;
            ++j;
        }
        println();
        return false;
    }

    protected String[] wordWrap(String s) {
        String[] cleaned = filterPrintableWithNewline(htmlClean(s)).replaceAll(" +", " ").split("\n");
        List<String> result = new LinkedList<>();
        for (String item: cleaned) {
            String[] wrappedLine = WordUtils
                    .wrap(item, 39, "\n", true)
                    .split("\n");
            result.addAll(asList(wrappedLine));
        }
        return Arrays.copyOf(result.toArray(), result.size(), String[].class);
    }

    private void drawLogo() {
        write(getLogo());
        write(GREY3);
    }

    protected void waitOn() {
        print("PLEASE WAIT...");
        flush();
    }

    protected void waitOff() {
        for (int i=0; i<14; ++i) write(DEL);
        flush();
    }

}
