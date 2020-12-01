package eu.sblendorio.bbs.tenants.ascii;

import com.google.common.collect.ImmutableMap;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import static java.util.Arrays.asList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.substring;
import static org.apache.commons.lang3.StringUtils.trim;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.text.WordUtils;

@Hidden
public class TelevideoRaiAscii extends AsciiThread {

    public TelevideoRaiAscii() {
        setLocalEcho(false);
    }

    static final long TIMEOUT = NumberUtils.toLong(System.getProperty("televideo_a1_timeout", "5000"));
    static final String HR_TOP = StringUtils.repeat('-', 39);

    static final String PREFIX = "http://www.servizitelevideo.rai.it/televideo/pub/";
    protected int screenRows = 19;

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

    static Map<String, NewsSection> sections = new ImmutableMap.Builder<String, NewsSection>()
        .put("101", new NewsSection("Ultim'Ora", PREFIX + "rss101.xml", Logos.LOGO_ULTIMORA))
        .put("102", new NewsSection("24h No Stop", PREFIX + "rss102.xml", Logos.LOGO_NOSTOP24H))
        .put("110", new NewsSection("Primo Piano", PREFIX + "rss110.xml", Logos.LOGO_PRIMOPIANO))
        .put("120", new NewsSection("Politica", PREFIX + "rss120.xml", Logos.LOGO_POLITICA))
        .put("130", new NewsSection("Economia", PREFIX + "rss130.xml", Logos.LOGO_ECONOMIA))
        .put("140", new NewsSection("Dall'Italia", PREFIX + "rss140.xml", Logos.LOGO_DALLITALIA))
        .put("150", new NewsSection("Dal Mondo", PREFIX + "rss150.xml", Logos.LOGO_DALMONDO))
        .put("160", new NewsSection("Culture", PREFIX + "rss160.xml", Logos.LOGO_CULTURE))
        .put("170", new NewsSection("Cittadini", PREFIX + "rss170.xml", Logos.LOGO_CITTADINI))
        .put("180", new NewsSection("Speciale", PREFIX + "rss180.xml", Logos.LOGO_SPECIALE))
        .put("190", new NewsSection("Atlante Crisi", PREFIX + "rss190.xml", Logos.LOGO_ATLANTECRISI))
        .put("229", new NewsSection("Brevi Calcio", PREFIX + "rss229.xml", Logos.LOGO_BREVICALCIO))
        .put("230", new NewsSection("CalcioSquadre", PREFIX + "rss230.xml", Logos.LOGO_CALCIOSQUADRE))
        .put("260", new NewsSection("Altri Sport", PREFIX + "rss260.xml", Logos.LOGO_ALTRISPORT))
        .put("299", new NewsSection("Brevissime", PREFIX + "rss299.xml", Logos.LOGO_SPORTBREVISSIME))
        .put("810", new NewsSection("Motori", PREFIX + "rss810.xml", Logos.LOGO_MOTORI))
        .build();

    private void printChannelList() {
        //gotoXY(0, 5);
        List<String> keys = new LinkedList<>(sections.keySet());
        Collections.sort(keys);
        for (int i=0; i<8; ++i) {
            int even = i;
            if (even >= keys.size()) break;
            String key = keys.get(even);
            NewsSection value = sections.get(key);
            write(' ');
            print(key); write(' ', ' ');
            String title = substring(value.title + "                    ", 0, 12);
            print(title);
            print(" ");

            int odd = even+8;
            if (odd < keys.size()) {
                key = keys.get(odd);
                value = sections.get(key);
                write(' '); print(key); write(' ', ' '); print(value.title);
            } else {
                write(' '); print(" . "); write(' ', ' '); print("Fine");
            }
            newline();
            newline();

        }
        write(' '); print(" . "); write(' ', ' '); print("Fine");
        newline();
        newline();
        flush();
    }

    public static List<NewsFeed> getFeeds(String urlString) throws IOException, FeedException {
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
        log("Entered TelevideoRai");
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
                choice = sections.get(command);
                inputFail = (choice == null && !trim(command).equals("."));
                if (inputFail) {
                    // input wrong
                }
            } while (inputFail);
            if (trim(command).equals(".")) {
                break;
            }
            log("Televideo choice = " + command + " " + (choice == null ? EMPTY : choice.title));
            view(choice);
        }
        log("Televideo-EXIT");
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
                String post = EMPTY;
                post += feed.title + "<br>" + HR_TOP + "<br>";
                post += dateFormat.format(feed.publishedDate) + " " + feed.description + "<br>";
                int lineFeeds = (screenRows - (wordWrap(post).length % screenRows)) % screenRows;

                post += StringUtils.repeat("&c64nbsp;<br>", lineFeeds);
                text += post;
            }

            interruptByUser = displayText(text, screenRows, section.logo);
            if (!interruptByUser) {
                println(); print(" ENTER = MAIN MENU                    ");
                flush(); resetInput();
                Integer finalKey = keyPressed(TIMEOUT);
                interruptByUser = finalKey != null;
            }
        } while (!interruptByUser);
    }

    protected boolean displayText(String text, int screenRows, byte[] logo) throws IOException {
        cls();
        write(defaultIfNull(logo, LOGO_TELEVIDEO));
        println();
        println();

        String[] rows = wordWrap(text);
        int page = 1;
        int j = 0;
        boolean forward = true;
        while (j < rows.length) {
            if (j>0 && j % screenRows == 0 && forward) {
                println();
                print("-PAGE " + page + "-  SPACE=NEXT  -=PREV  .=EXIT");

                Integer ch = keyPressed(TIMEOUT);
                if (ch == null) ch = 1;

                resetInput();
                if (ch == '.') {
                    return true;
                }
                if (ch == '-' && page > 1) {
                    j -= (screenRows * 2);
                    --page;
                    forward = false;
                    cls();
                    write(logo == null ? LOGO_TELEVIDEO : logo);
                    println();
                    println();
                    continue;
                } else {
                    ++page;
                }
                cls();
                write(logo == null ? LOGO_TELEVIDEO : logo);
                println();
                println();
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

    private void drawLogo() {
        write(LOGO_TELEVIDEO);
        println();
        println("---------");
        println();
    }

    private static final byte[] LOGO_TELEVIDEO = "Televideo".getBytes(StandardCharsets.ISO_8859_1);

    static class Logos {
        static final byte[] LOGO_ULTIMORA = "Televideo - Ultim'ora".getBytes(StandardCharsets.ISO_8859_1);
        static final byte[] LOGO_POLITICA = "Televideo - Politica".getBytes(StandardCharsets.ISO_8859_1);
        static final byte[] LOGO_ECONOMIA = "Televideo - Economia".getBytes(StandardCharsets.ISO_8859_1);
        static final byte[] LOGO_DALLITALIA = "Televideo - Dall'Italia".getBytes(StandardCharsets.ISO_8859_1);
        static final byte[] LOGO_DALMONDO = "Televideo - Dal mondo".getBytes(StandardCharsets.ISO_8859_1);
        static final byte[] LOGO_CULTURE = "Televideo - Culture".getBytes(StandardCharsets.ISO_8859_1);
        static final byte[] LOGO_BREVICALCIO = "Televideo - Brevi calcio".getBytes(StandardCharsets.ISO_8859_1);
        static final byte[] LOGO_CALCIOSQUADRE = "Televideo - Calcio - squadre".getBytes(StandardCharsets.ISO_8859_1);
        static final byte[] LOGO_ALTRISPORT = "Televideo - Altri sport".getBytes(StandardCharsets.ISO_8859_1);
        static final byte[] LOGO_MOTORI = "Televideo - Motori".getBytes(StandardCharsets.ISO_8859_1);
        static final byte[] LOGO_SPORTBREVISSIME = "Televideo - Sport - brevissime".getBytes(StandardCharsets.ISO_8859_1);
        static final byte[] LOGO_PRIMOPIANO = "Televideo - Primo piano".getBytes(StandardCharsets.ISO_8859_1);
        static final byte[] LOGO_NOSTOP24H = "Televideo - No stop - 24 ore".getBytes(StandardCharsets.ISO_8859_1);
        static final byte[] LOGO_SPECIALE = "Televideo - Speciale".getBytes(StandardCharsets.ISO_8859_1);
        static final byte[] LOGO_ATLANTECRISI = "Televideo - Atlante crisi".getBytes(StandardCharsets.ISO_8859_1);
        static final byte[] LOGO_CITTADINI = "Televideo - Cittadini".getBytes(StandardCharsets.ISO_8859_1);
    }
}
