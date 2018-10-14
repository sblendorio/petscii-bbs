package eu.sblendorio.bbs.tenants;

import com.google.common.collect.ImmutableMap;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import eu.sblendorio.bbs.core.PetsciiThread;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static eu.sblendorio.bbs.core.Colors.*;
import static eu.sblendorio.bbs.core.Keys.*;
import static org.apache.commons.lang3.StringUtils.*;

public class TelevideoRai extends PetsciiThread {

    static String PREFIX = "http://www.servizitelevideo.rai.it/televideo/pub/";

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

        public String toString() {
            return "Title: "+title+"\nDate:"+publishedDate+"\nDescription:"+description+"\n";
        }
    }

    static Map<String, NewsSection> sections = new ImmutableMap.Builder<String, NewsSection>()
        .put("101", new NewsSection(WHITE, "Ultim'Ora", PREFIX + "rss101.xml", null))
        .put("102", new NewsSection(CYAN, "24h No Stop", PREFIX + "rss102.xml", null))
        .put("110", new NewsSection(RED, "Primo Piano", PREFIX + "rss110.xml", null))
        .put("120", new NewsSection(GREEN, "Politica", PREFIX + "rss120.xml", null))
        .put("130", new NewsSection(BLUE, "Economia", PREFIX + "rss130.xml", null))
        .put("140", new NewsSection(GREY2, "Dall'Italia", PREFIX + "rss140.xml", null))
        .put("150", new NewsSection(LIGHT_BLUE, "Dal Mondo", PREFIX + "rss150.xml", null))
        .put("160", new NewsSection(LIGHT_RED, "Culture", PREFIX + "rss160.xml", null))
        .put("170", new NewsSection(PURPLE, "Cittadini", PREFIX + "rss170.xml", null))
        .put("180", new NewsSection(GREY3, "Speciale", PREFIX + "rss180.xml", null))
        .put("190", new NewsSection(BROWN, "Atlante Crisi", PREFIX + "rss190.xml", null))
        .put("201", new NewsSection(ORANGE, "Calcio", PREFIX + "rss201.xml", null))
        .put("229", new NewsSection(LIGHT_GREEN, "Brevi Calcio", PREFIX + "rss229.xml", null))
        .put("230", new NewsSection(YELLOW, "CalcioSquadre", PREFIX + "rss230.xml", null))
        .put("260", new NewsSection(GREEN, "Altri Sport", PREFIX + "rss260.xml", null))
        .put("299", new NewsSection(GREY1, "Brevissime", PREFIX + "rss299.xml", null))
        .put("810", new NewsSection(GREY2, "Motori", PREFIX + "rss810.xml", null))
        .build();


    private void printChannelList() {
        gotoXY(0, 5);
        List<String> keys = new LinkedList<String>(sections.keySet());
        Collections.sort(keys);
        for (int i=0; i<=8; ++i) {
            int even = i;
            if (even >= keys.size()) break;
            String key = keys.get(even);
            NewsSection value = sections.get(key);
            write(RIGHT, value.color, REVON, SPACE_CHAR);
            print(key); write(SPACE_CHAR, REVOFF, SPACE_CHAR);
            String title = substring(value.title + "                    ", 0, 12);
            print(title);
            print(" ");

            int odd = even+9;
            if (odd < keys.size()) {
                key = keys.get(odd);
                value = sections.get(key);
                write(value.color, REVON, SPACE_CHAR);
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
        write(GREY3);
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
            view(choice);
        }
    }

    private void view(NewsSection section) throws Exception {
        cls();
        println(section.title);
        println("----");
        waitOn();
        List<NewsFeed> feeds = getFeeds(section.url);
        waitOff();
        for (NewsFeed feed: feeds) {
            println("*"+feed.title);
//            println("----");
//            println(feed.description);
//            println("---------------------------");
        }
        flush();
        resetInput(); readKey();
    }

    private void logo() throws IOException {
        write(LOGO_TELEVIDEO);
        write(GREY3);
    }

    public final static byte[] LOGO_TELEVIDEO = new byte[] {
        32, 32, 32, 32, 32, 18, -98, 32, 32, 32, -95, 32, 32, -110, -95, 18,
        32, -110, 32, 32, 18, -95, 32, 32, -110, -95, 18, 32, -110, -95, 18, -95,
        -110, -95, 18, 32, -95, 32, -68, -110, 32, 18, 32, 32, 32, -110, -84, 18,
        32, 32, -110, -69, 13, 32, 32, 32, 32, 32, 32, 18, 32, -110, 32, 18,
        -95, -68, -110, -94, 32, 18, 32, -110, 32, 32, 18, -95, -68, -110, -94, 32,
        18, -95, -110, -95, 18, -95, -110, -95, 18, 32, -95, -110, -95, 18, -95, -110,
        -95, 18, 32, -110, -94, -69, 18, -95, -110, -95, 18, -95, -110, -95, 13, 32,
        32, 32, 32, 32, 32, 18, 32, -110, 32, 18, -95, -68, -110, -94, -69, 18,
        32, -110, -94, -94, 18, -95, -68, -110, -94, -69, -68, 18, 32, -84, -110, 32,
        18, 32, -95, -68, -66, -110, -95, 18, 32, -110, -94, -94, 18, -95, -68, -66,
        -110, -95, 13, 32, 32, 32, 32, 32, 32, 18, -94, -110, 32, -68, 18, -94,
        -94, -110, -66, 18, -94, -94, -94, -110, -68, 18, -94, -94, -110, -66, 32, 18,
        -94, -110, 32, 32, 18, -94, -110, -68, 18, -94, -94, -110, 32, 18, -94, -94,
        -94, -110, 32, 18, -94, -94, -110, 13
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
