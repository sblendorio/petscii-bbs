package eu.sblendorio.bbs.tenants;

import com.google.common.collect.ImmutableMap;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import eu.sblendorio.bbs.core.HtmlUtils;
import eu.sblendorio.bbs.core.PetsciiThread;
import org.apache.commons.text.WordUtils;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static eu.sblendorio.bbs.core.Colors.*;
import static eu.sblendorio.bbs.core.Keys.*;
import static eu.sblendorio.bbs.core.Utils.filterPrintableWithNewline;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.*;

public class OpenOnline extends PetsciiThread {


    static String PREFIX = "https://www.open.online/";
    protected int screenRows = 19;

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

        public NewsFeed(Date publishedDate, String title, String description, String uri) {
            this.publishedDate = publishedDate; this.title = title; this.description = description; this.uri = uri;
        }

        public String toString() {
            return "Title: "+title+"\nDate:"+publishedDate+"\nDescription:"+description+"\n";
        }
    }


    private Map<String, NewsSection> sections = new LinkedHashMap<>();

    private void printChannelList() {
        gotoXY(0, 5);
        List<String> keys = new LinkedList<>(sections.keySet());
        Collections.sort(keys);
        for (int i=0; i<8; ++i) {
            int even = i;
            if (even >= keys.size()) break;
            String key = keys.get(even);
            NewsSection value = sections.get(key);
            write(RIGHT, REVON, SPACE_CHAR);
            print(key); write(SPACE_CHAR, REVOFF, SPACE_CHAR);
            String title = substring(value.title + "                    ", 0, 12);
            print(title);
            print(" ");

            int odd = even+8;
            if (odd < keys.size()) {
                key = keys.get(odd);
                value = sections.get(key);
                write(REVON, SPACE_CHAR);
                print(key);
                write(SPACE_CHAR, REVOFF, SPACE_CHAR);
                print(value.title);
            } else {
                write(WHITE, REVON, SPACE_CHAR);
                print(" . ");
                write(SPACE_CHAR, REVOFF, SPACE_CHAR);
                print("Fine");
            }
            newline();
            newline();

        }
        write(RIGHT, WHITE, REVON, SPACE_CHAR);
        print(" . ");
        write(SPACE_CHAR, REVOFF, SPACE_CHAR);
        print("Fine");
        write(GREY3, RETURN, RETURN);
        flush();
    }

    public static List<NewsFeed> getFeeds(String urlString) throws Exception {
        URL url = new URL(urlString);
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(url));
        List<NewsFeed> result = new LinkedList<>();
        List<SyndEntry> entries = feed.getEntries();
        for (SyndEntry e : entries)
            result.add(new NewsFeed(e.getPublishedDate(), e.getTitle(), e.getDescription().getValue(), e.getUri()));
        return result;
    }

    @Override
    public void doLoop() throws Exception {
        String regex = "<li class=\"tag ?([a-z\\-]+)?\"><a href=\"(https://www.open.online/[a-z\\-]+/)\">([A-Z\\s\\-\\']+)</a></li>";
        String indexHtml = httpGet("https://www.open.online/");
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(indexHtml);
        while (m.find()) {
            String line = m.group(0);
            Matcher matcher = p.matcher(line);
            matcher.matches();
            String url = matcher.group(2) + "rss.xml";
            String name = matcher.group(3);
            System.out.println(name + " = " + url);
        }


        if (1==1) return;
        log("Entered OpenOnline");
        while (true) {
            cls();
            logo();
            printChannelList();
            String command = null;
            NewsSection choice;
            boolean inputFail;
            do {
                choice = null;
                print(" > ");
                flush();
                resetInput();
                command = readLine(3);
                choice = sections.get(command);
                inputFail = (choice == null && !trim(command).equals("."));
                if (inputFail) {
                    write(UP); println("        "); write(UP);
                }
            } while (inputFail);
            if (trim(command).equals(".")) break;
            log("Televideo choice = " + command + " " + choice.title);
            view(choice);
        }
        log("Televideo-EXIT");
    }

    private void view(NewsSection section) throws Exception {
        cls();
        waitOn();
        List<NewsFeed> feeds = getFeeds(section.url);
        String text = EMPTY;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        for (NewsFeed feed: feeds) {
            text += "---------------------------------------" + "<br>";
            text += feed.title + "<br>" + "---------------------------------------" + "<br>";
            text += dateFormat.format(feed.publishedDate) + " " + feed.description + "<br>" + "<br>";
        }
        waitOff();

        boolean interruptByUser = displayText(text, screenRows);
        if (!interruptByUser) {
            gotoXY(0, 24); write(WHITE); print(" ENTER = MAIN MENU                    ");
            flush(); resetInput(); readKey();
        }
    }

    protected boolean displayText(String text, int screenRows) throws Exception {
        cls();
        write(LOGO_OPEN);
        write(GREY3);

        String[] rows = wordWrap(text);
        int page = 1;
        int j = 0;
        boolean forward = true;
        while (j < rows.length) {
            if (j>0 && j % screenRows == 0 && forward) {
                println();
                write(WHITE);
                print("-PAGE " + page + "-  SPACE=NEXT  -=PREV  .=EXIT");
                write(GREY3);

                resetInput(); int ch = readKey();
                if (ch == '.') {
                    return true;
                } else if (ch == '-' && page > 1) {
                    j -= (screenRows * 2);
                    --page;
                    forward = false;
                    cls();
                    write(LOGO_OPEN);
                    write(GREY3);
                    continue;
                } else {
                    ++page;
                }
                cls();
                write(LOGO_OPEN);
                write(GREY3);
            }
            String row = rows[j];
            println(row);
            forward = true;
            ++j;
        }
        println();
        return false;
    }

    protected String[] wordWrap(String s) {
        String[] cleaned = filterPrintableWithNewline(HtmlUtils.htmlClean(s)).replaceAll(" +", " ").split("\n");
        List<String> result = new LinkedList<>();
        for (String item: cleaned) {
            String[] wrappedLine = WordUtils
                    .wrap(item, 39, "\n", true)
                    .split("\n");
            result.addAll(asList(wrappedLine));
        }
        return Arrays.copyOf(result.toArray(), result.size(), String[].class);
    }

    private void logo() throws IOException {
        write(LOGO_OPEN);
        write(GREY3);
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

    protected void waitOn() {
        print("WAIT PLEASE...");
        flush();
    }

    protected void waitOff() {
        for (int i=0; i<14; ++i) write(DEL);
        flush();
    }

}
