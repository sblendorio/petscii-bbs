package eu.sblendorio.bbs.tenants;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import droid64.addons.DiskUtilities;
import eu.sblendorio.bbs.core.HtmlUtils;
import eu.sblendorio.bbs.core.PetsciiThread;
import eu.sblendorio.bbs.core.XModem;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static eu.sblendorio.bbs.core.Colors.*;
import static eu.sblendorio.bbs.core.Keys.*;
import static eu.sblendorio.bbs.core.Utils.filterPrintable;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.MapUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

public class CsdbLatestReleases extends PetsciiThread {

    private static final String RSS_LATESTRELEASES = "https://csdb.dk/rss/latestreleases.php";
    private static final String RSS_LATESTADDITIONS = "https://csdb.dk/rss/latestadditions.php?type=release";

    public static final String URL_TEMPLATE = "https://csdb.dk/search/?seinsel=releases&all=1&search=";

    private int currentPage = 1;
    protected int pageSize = 10;

    static class NewsFeed {
        final Date publishedDate;
        final String title;
        final String description;
        final String uri;

        NewsFeed(Date publishedDate, String title, String description, String downloadUri) {
            this.publishedDate = publishedDate; this.title = title; this.description = description; this.uri = downloadUri;
        }

        public String toString() {
            return "Title: "+title+"\nDate:"+publishedDate+"\nDescription:"+description+"\n";
        }
    }

    static class ReleaseEntry {
        final String id;
        final String releaseUri;
        final String type;
        final Date publishedDate;
        final String title;
        final String releasedBy;
        final List<String> links;

        ReleaseEntry(String id, String releaseUri, String type, Date publishedDate, String title, String releasedBy, List<String> links) {
            this.id = id; this.releaseUri = releaseUri; this.type = type;
            this.publishedDate = publishedDate; this.title = title; this.releasedBy = releasedBy; this.links = links;
        }

        public String toString() {
            return "Title: "+title+"\nDate:"+publishedDate+"\nreleasedBy:"+releasedBy+"\nlinks:"+links+"\n";
        }

    }

    private Map<Integer, ReleaseEntry> posts = emptyMap();
    private List<NewsFeed> entries = emptyList();

    @Override
    public void doLoop() throws Exception {
        {
            do {
                currentPage = 1;
                logo();
                println();
                write(WHITE); print("R"); write(GREY2); println(" for latest releases");
                write(WHITE); print("A"); write(GREY2); println(" for latest additions");
                write(WHITE); print("."); write(GREY2); println(" to go back");
                println();
                write(GREY3);
                println(repeat(' ',9) + "Enter search criteria ");
                println();
                println(repeat(' ',9) + repeat(chr(163), 21));
                write(UP, UP);
                print(repeat(' ',9));
                flush();
                resetInput();
                final String search = readLine();
                final String nsearch = defaultString(search).trim();
                if (nsearch.equals(".") || isBlank(search)) {
                    return;
                } else if ("r".equalsIgnoreCase(nsearch)) {
                    entries = emptyList();
                    browseLatestReleases(RSS_LATESTRELEASES);
                } else if ("a".equalsIgnoreCase(nsearch)) {
                    entries = emptyList();
                    browseLatestReleases(RSS_LATESTADDITIONS);
                } else {
                    println();
                    println();
                    waitOn();
                    /*
                    entries = getUrls(URL_TEMPLATE + URLEncoder.encode(search, "UTF-8"));
                    waitOff();
                    if (CollectionUtils.isEmpty(entries)) {
                        write(RED); println("Zero result page - press any key");
                        flush(); resetInput(); readKey();
                        continue;
                    }
                    displaySearchResults(entries);
                    */
                }
            } while (true);
        }
    }

    public void browseLatestReleases(String rssUrl) throws Exception {
        posts = null;
        currentPage = 1;
        listPosts(rssUrl);
        while (true) {
            log("CSDb waiting for input");
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
                listPosts(rssUrl);
            } else if ("+".equals(input)) {
                ++currentPage;
                posts = null;
                try {
                    listPosts(rssUrl);
                } catch (NullPointerException e) {
                    --currentPage;
                    posts = null;
                    listPosts(rssUrl);
                }
            } else if ("-".equals(input) && currentPage > 1) {
                --currentPage;
                posts = null;
                listPosts(rssUrl);
            } else if ("--".equals(input) && currentPage > 1) {
                currentPage = 1;
                posts = null;
                listPosts(rssUrl);
            } else if ("r".equals(input) || "reload".equals(input) || "refresh".equals(input)) {
                posts = null;
                listPosts(rssUrl);
            } else if (posts.containsKey(toInt(input))) {
                displayPost(toInt(input));
                listPosts(rssUrl);
            } else if ("".equals(input)) {
                listPosts(rssUrl);
            }
        }
        flush();
    }

    private void displayPost(int n) throws Exception {
        int i = 3;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        cls();
        logo();

        waitOn();
        final ReleaseEntry p = posts.get(n);
        String strDate;
        try {
            strDate = dateFormat.format(p.publishedDate);
        } catch (Exception e) {
            strDate = EMPTY;
        }
        final String releasedBy = p.releasedBy;
        final String url = p.links.get(0);
        final String title = p.title;
        final String type = p.type;
        final String releaseUri = p.releaseUri;
        byte[] content = DiskUtilities.getPrgContent(url);
        waitOff();

        write(WHITE); println(title);
        write(GREY3); print("From: ");
        write(WHITE); print(releasedBy);
        println();
        write(GREY3); print("Type: ");
        write(WHITE); print(type);
        println();
        write(GREY3); print("Date: ");
        write(WHITE); println(strDate);
        if (content != null) {
            write(GREY3); print("Size: ");
            write(WHITE); println(content.length + " bytes");
        }
        println();
        write(GREY3); println("URL:");
        write(WHITE); println(releaseUri);
        println();
        if (content == null) {
            log("Can't download " + releaseUri);
            write(RED, REVON); println("      ");
            write(RED, REVON); print(" WARN "); write(WHITE, REVOFF); println(" Can't handle this. Use browser.");
            write(RED, REVON); println("      "); write(WHITE, REVOFF);
            write(CYAN); println();
            print("SORRY - press any key to go back ");
            readKey();
            resetInput();
        } else {
            write(GREY3);
            println("Press any key to prepare to download");
            println("Or press \".\" to abort it");
            resetInput();
            int ch = readKey();
            if (ch == '.') return;
            println();
            write(REVON, LIGHT_GREEN);
            write(REVON); println("                              ");
            write(REVON); println(" Please start XMODEM transfer ");
            write(REVON); println("                              ");
            write(REVOFF, WHITE);
            log("Downloading " + title + " - " + releaseUri);
            XModem xm = new XModem(cbm, cbm.out());
            xm.send(content);
            println();
            write(CYAN);
            print("DONE - press any key to go back ");
            readKey();
            resetInput();
        }
    }

    private void listPosts(String rssUrl) throws Exception {
        cls();
        logo();
        if (isEmpty(posts)) {
            waitOn();
            posts = getPosts(rssUrl, currentPage, pageSize);
            waitOff();
        }
        for (Map.Entry<Integer, ReleaseEntry> entry: posts.entrySet()) {
            int i = entry.getKey();
            ReleaseEntry post = entry.getValue();
            write(WHITE); print(i + "."); write(GREY3);
            final int iLen = 37-String.valueOf(i).length();
            String title = post.title + " (" + post.releasedBy+")";
            String line = WordUtils.wrap(filterPrintable(HtmlUtils.htmlClean(title)), iLen, "\r", true);
            println(line.replaceAll("\r", "\r " + repeat(" ", 37-iLen)));
        }
        newline();
    }

    private List<ReleaseEntry> getReleases() throws Exception {
        Pattern p = Pattern.compile("(?is)<a href=['\\\"]([^'\\\"]*?)['\\\"] title=['\\\"][^'\\\"]*?\\.(p00|prg|zip|t64|d64|d71|d81|d82|d64\\.gz|d71\\.gz|d81\\.gz|d82\\.gz|t64\\.gz)['\\\"]>");
        List<ReleaseEntry> list = new LinkedList<>();
        for (NewsFeed item: entries) {
            if (item.description.matches("(?is).*=\\s*[\\\"'][^\\\"']*\\.(p00|prg|zip|t64|d64|d71|d81|d82|d64\\.gz|d71\\.gz|d81\\.gz|d82\\.gz|t64\\.gz)[^\\\"']*[\\\"'].*")) {
                String releaseUri = item.uri;
                String id = item.uri.replaceAll("(?is).*id=([0-9a-zA-Z_\\-]+).*$", "$1"); // https://csdb.dk/release/?id=178862&rs
                String releasedBy = item.description.replaceAll("(?is).*Released by:\\s*[^>]*>(.*?)<.*", "$1");
                String type = item.description.replaceAll("(?is).*Type:\\s*[^>]*>(.*?)<.*", "$1");
                Matcher m = p.matcher(item.description);
                List<String> urls = new ArrayList<>();
                while (m.find()) urls.add(m.group(1));
                list.add(new ReleaseEntry(id, releaseUri, type, item.publishedDate, item.title, releasedBy, urls));
            }
        }
        return list;
    }


    private Map<Integer, ReleaseEntry> getPosts(String rssURL, int page, int perPage) throws Exception {
        if (page < 1 || perPage < 1) return null;

        if (isEmpty(entries)) entries = getFeeds(rssURL);
        List<ReleaseEntry> list = getReleases();

        Map<Integer, ReleaseEntry> result = new LinkedHashMap<>();
        for (int i=(page-1)*perPage; i<page*perPage; ++i)
            if (i<list.size()) result.put(i+1, list.get(i));
        return result;
    }


    private static List<NewsFeed> getFeeds(String urlString) throws Exception {
        URL url = new URL(urlString);
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(url));
        List<CsdbLatestReleases.NewsFeed> result = new LinkedList<>();
        List<SyndEntry> entries = feed.getEntries();
        for (SyndEntry e : entries)
            result.add(new CsdbLatestReleases.NewsFeed(
                    e.getPublishedDate(),
                    e.getTitle().replaceAll("(?is) by .*?$", EMPTY),
                    e.getDescription().getValue(),
                    e.getUri()));
        return result;
    }

    private void help() throws Exception {
        cls();
        logo();
        println();
        println();
        println("Press any key to go back");
        readKey();
    }

    private void logo() throws Exception {
        write(CLR, LOWERCASE, CASE_LOCK);
        write(LOGO);
        write(CYAN); gotoXY(15,3); print("Latest .PRG releases");
        write(GREY3); gotoXY(0,5);

    }

    private void waitOn() {
        print("PLEASE WAIT...");
        flush();
    }

    private void waitOff() {
        for (int i=0; i<14; ++i) write(DEL);
        flush();
    }

    private static final byte[] LOGO = new byte[] {
        32, 18, 5, -66, -69, -110, -69, 18, -66, -69, -110, -69, 18, 32, -69, -110,
        -69, 18, 32, -110, 13, 32, 18, 32, -110, -68, -66, 18, -69, -65, -110, -66,
        18, 32, -95, -110, -95, 18, 32, -69, -110, -69, 32, -102, -44, -56, -59, -96,
        -61, 45, 54, 52, 32, -45, -61, -59, -50, -59, 32, -60, -63, -44, -63, -62,
        -63, -45, -59, 13, 32, 18, 5, 32, -110, -84, -69, -94, 18, -69, -110, -69,
        18, 32, -95, -110, -95, 18, 32, -95, -110, -95, 13, 32, 18, -69, -66, -110,
        -66, 18, -69, -66, -110, -66, 18, 32, -66, -110, -66, 18, 32, -66, -110, -66, 13
    };
}
