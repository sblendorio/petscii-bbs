package eu.sblendorio.bbs.tenants;

import static eu.sblendorio.bbs.core.Colors.CYAN;
import static eu.sblendorio.bbs.core.Colors.GREEN;
import static eu.sblendorio.bbs.core.Colors.GREY2;
import static eu.sblendorio.bbs.core.Colors.GREY3;
import static eu.sblendorio.bbs.core.Colors.LIGHT_GREEN;
import static eu.sblendorio.bbs.core.Colors.LIGHT_RED;
import static eu.sblendorio.bbs.core.Colors.PURPLE;
import static eu.sblendorio.bbs.core.Colors.RED;
import static eu.sblendorio.bbs.core.Colors.WHITE;
import static eu.sblendorio.bbs.core.Colors.YELLOW;
import static eu.sblendorio.bbs.core.Keys.CASE_LOCK;
import static eu.sblendorio.bbs.core.Keys.CLR;
import static eu.sblendorio.bbs.core.Keys.DEL;
import static eu.sblendorio.bbs.core.Keys.LOWERCASE;
import static eu.sblendorio.bbs.core.Keys.REVOFF;
import static eu.sblendorio.bbs.core.Keys.REVON;
import static eu.sblendorio.bbs.core.Keys.UP;
import static eu.sblendorio.bbs.core.Utils.filterPrintable;
import static java.lang.Integer.compare;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.MapUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.text.WordUtils;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import droid64.addons.DiskUtilities;
import eu.sblendorio.bbs.core.HtmlUtils;
import eu.sblendorio.bbs.core.PetsciiThread;
import eu.sblendorio.bbs.core.XModem;

public class CsdbReleasesSD2IEC extends PetsciiThread {

    private static final String RSS_LATESTRELEASES = "https://csdb.dk/rss/latestreleases.php";
    private static final String RSS_LATESTADDITIONS = "https://csdb.dk/rss/latestadditions.php?type=release";
    private static final String URL_TEMPLATE = "https://csdb.dk/search/?seinsel=releases&all=1&search=";
    private static final String OTHER_PLATFORM = "Other Platform C64 Tool";


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
            return "Title: "+title+"\nDate:"+publishedDate+"\nDescription:"+description+"\nUri:"+uri+"\n";
        }
    }

    static class ReleaseEntry {
        final String id;
        final String releaseUri;
        final String type;
        final Date publishedDate;
        final String strDate;
        final String title;
        final String releasedBy;
        final List<String> links;

        ReleaseEntry(String id, String releaseUri, String type, Date publishedDate, String title, String releasedBy, List<String> links) {
            this.strDate = null;
            this.id = id; this.releaseUri = releaseUri; this.type = type;
            this.publishedDate = publishedDate; this.title = title; this.releasedBy = releasedBy; this.links = links;
        }

        ReleaseEntry(String id, String releaseUri, String type, String strDate, String title, String releasedBy, List<String> links) {
            this.publishedDate = null;
            this.id = id; this.releaseUri = releaseUri; this.type = type;
            this.strDate = strDate; this.title = title; this.releasedBy = releasedBy; this.links = links;
        }

        public String toString() {
            return "releaseUri: "+releaseUri+"\nid: "+id+"\nTitle: "+title+"\nStrDate:"+strDate+"\nDate:"+publishedDate+"\nreleasedBy:"+releasedBy+"\nlinks:"+links+"\n";
        }

    }

    private Map<Integer, ReleaseEntry> posts = emptyMap();
    private List<NewsFeed> entries = emptyList();
    private List<ReleaseEntry> searchResults = emptyList();
    private boolean searchMode = false;

    @Override
    public void doLoop() throws Exception {
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
                searchMode = false;
                browseLatestReleases(RSS_LATESTRELEASES);
            } else if ("a".equalsIgnoreCase(nsearch)) {
                entries = emptyList();
                searchMode = false;
                browseLatestReleases(RSS_LATESTADDITIONS);
            } else {
                println();
                println();
                waitOn();
                searchResults = searchReleaseEntries(URL_TEMPLATE + URLEncoder.encode(search, "UTF-8"));
                waitOff();
                if (isEmpty(searchResults)) {
                    write(RED); println("Zero result page - press any key");
                    flush(); resetInput(); readKey();
                    continue;
                }
                searchMode = true;
                browseLatestReleases(EMPTY);
            }
        } while (true);
    }

    public void browseLatestReleases(String rssUrl) throws Exception {
        posts = null;
        currentPage = 1;
        listPosts(rssUrl);
        while (true) {
            log("CSDb waiting for input");
            write(CYAN);print("#"); write(GREY3);
            print(", [");
            write(CYAN); print("+-"); write(GREY3);
            print("]Page [");
            write(CYAN); print("H"); write(GREY3);
            print("]elp [");
            write(CYAN); print("R"); write(GREY3);
            print("]eload [");
            write(CYAN); print("."); write(GREY3);
            print("]");
            write(CYAN); print("Q"); write(GREY3);
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
                entries = null;
                listPosts(rssUrl);
            } else if ("r".equals(input) || "reload".equals(input) || "refresh".equals(input)) {
                entries = null;
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
            strDate = p.strDate == null ? dateFormat.format(p.publishedDate) : p.strDate;
        } catch (Exception e) {
            strDate = EMPTY;
        }
        final String releasedBy = p.releasedBy;
        final String releaseUri = p.releaseUri;
        final String url = isEmpty(p.links) ? findDownloadLink(new URL(p.releaseUri)) : p.links.get(0);
        final String title = p.title;
        final String type = p.type;
        final String id   = p.id;
        boolean noChoice = false;


        //Get Content File and get fileName
        DownloadData file = PetsciiThread.download(new URL(url));
        String fileName = file.getFilename();
        byte[] content = null;

        //Is D64
        if("D64".equals(fileName.substring(fileName.length()-3, fileName.length()).toUpperCase())) {
            logo();
            print("----------------------------------------");
            write(GREEN);
            print("       Download ");write(LIGHT_GREEN);print("D");write(GREEN);print("64 or ");write(LIGHT_GREEN);print("Z");write(GREEN);println("IP file?");
            write(LIGHT_GREEN);
            println("             Press D or Z");
            resetInput(); int key = readKey();
            key = Character.toLowerCase(key);
            if (key == 'd') {
                //Download D64
                content =  file.getContent();
            }
            else if (key == 'z')  {
                //Zip D64 into ZipFile
                content = DiskUtilities.zipBytes(fileName, file.getContent());
            }
            else {
                content = null;
                noChoice = true;
            }
        }
        //Is ZIP file
        else if (
                "ZIP".equals(fileName.substring(fileName.length()-3, fileName.length()).toUpperCase()) &&
                        !type.equals(OTHER_PLATFORM)
        ) {
            logo();
            print("----------------------------------------");
            write(GREEN);
            println("       After Download Zipfile...");
            println("         You can uncompress it ");
            content =  file.getContent();
        }
        // Type not Allowed OTHER_PLATFORM
        else if (type.equals("OTHER_PLATFORM")) {
            content = null;
        }
        else {
            content = DiskUtilities.getPrgContentFromFile(file);
        }

        waitOff();

        newline();
        print("----------------------------------------");
        if (content != null) {
            write(LIGHT_RED); print("Title:");
            write(PURPLE);println(title);
            write(LIGHT_RED); print("From: ");
            write(PURPLE); print(releasedBy);
            println();
            write(LIGHT_RED); print("Type: ");
            write(PURPLE); print(type);
            println();
            write(LIGHT_RED); print("ID:   ");
            write(PURPLE); print(id);
            println();
            write(LIGHT_RED); print("Date: ");
            write(PURPLE); println(strDate);
            write(LIGHT_RED); print("Size: ");
            write(PURPLE); println(content.length + " bytes");
            write(LIGHT_RED); print("File: ");
            write(PURPLE); println(fileName);
            write(LIGHT_RED); print("URL:  ");
            write(PURPLE); print(releaseUri);
        }


        if (content == null) {
            if(!noChoice) {
                log("Can't download " + releaseUri);
                write(RED, REVON); println("      ");
                write(RED, REVON); print(" WARN "); write(WHITE, REVOFF); println("Ops! Can't handle this. Use browser.");
                write(RED, REVON); println("      "); write(WHITE, REVOFF);
                write(CYAN); println();
                println("       Press any key to go back");
            }
            else {
                newline();
                println("          Key not allowed....");
                println("       Press any key to go back");
            }
            readKey();
            resetInput();
        } else {
            print("----------------------------------------");

            newline(); write(YELLOW);
            println("   Press any key to Prepare Download");
            println("       Press . to abort it");
            resetInput();
            int ch = readKey();
            if (ch == '.') return;
            println();
            cls();
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
            String title = post.title + (isNotBlank(post.releasedBy) ? " (" + post.releasedBy+")" : EMPTY);
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
                String id = item.uri.replaceAll("(?is)^.*id=([0-9a-zA-Z_\\-]+).*$", "$1"); // https://csdb.dk/release/?id=178862&rs
                String releasedBy = item.description.matches("(?is)^.*Released by:\\s*<a [^>]*>(.*?)<.*$") ? item.description.replaceAll("(?is)^.*Released by:\\s*<a [^>]*>(.*?)<.*$", "$1") : EMPTY;
                String type = item.description.matches("(?is)^.*Type:\\s*[^>]*>(.*?)<.*$") ? item.description.replaceAll("(?is)^.*Type:\\s*[^>]*>(.*?)<.*$", "$1") : EMPTY;
                Matcher m = p.matcher(item.description);
                List<String> urls = new ArrayList<>();
                while (m.find()) urls.add(m.group(1));
                if (!type.equalsIgnoreCase(OTHER_PLATFORM)) list.add(new ReleaseEntry(id, releaseUri, type, item.publishedDate, item.title, releasedBy, null));
            }
        }
        return list;
    }

    private Map<Integer, ReleaseEntry> getPosts(String rssURL, int page, int perPage) throws Exception {
        if (page < 1 || perPage < 1) return null;
        List<ReleaseEntry> list;

        if (searchMode) {
            list = searchResults;
        } else {
            if (isEmpty(entries)) entries = getFeeds(rssURL);
            list = getReleases();
        }

        return pagePosts(list, page, perPage);
    }

    private Map<Integer, ReleaseEntry> pagePosts(List<ReleaseEntry> list, int page, int perPage) throws Exception {
        Map<Integer, ReleaseEntry> result = new LinkedHashMap<>();
        for (int i=(page-1)*perPage; i<page*perPage; ++i)
            if (i<list.size()) result.put(i+1, list.get(i));
        return result;
    }

    private static List<NewsFeed> getFeeds(String urlString) throws Exception {
        URL url = new URL(urlString);
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(url));
        List<CsdbReleasesSD2IEC.NewsFeed> result = new LinkedList<>();
        List<SyndEntry> entries = feed.getEntries();
        for (SyndEntry e : entries)
            result.add(new CsdbReleasesSD2IEC.NewsFeed(
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
        write(YELLOW);
        gotoXY(15,3); print("Search your releases");
        write(LOWERCASE);
        write(CYAN); gotoXY(15,2); print(" for SD2IEC devices");
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

    public static List<ReleaseEntry> searchReleaseEntries(String url) throws Exception {
        String output = defaultString(httpGet(url));
        if (!output.matches("(?is)^.*<title>\\[CSDb\\] - Search for.*$")
                && output.matches("(?is)^.*<font size=6>([^<\\n]+?)</font.*$")
                && !output.matches("^.*There are no downloads because.*$")) {
            // PARSE RESULT SINGLE OUTPUT
            final String link = findDownloadLink(output);
            final String id = output.matches("(?is)^.*<a href=\"/voteview.php\\?type=release&id=([^\"'\\n']+?)\">.*$") ? output.replaceAll("(?is)^.*<a href=\"/voteview.php\\?type=release&id=([^\"'\\n']+?)\">.*$", "$1").trim() : EMPTY;
            final String releaseUri = isBlank(id) ? EMPTY : "https://csdb.dk/release/?id=" + id;
            final String title = output.matches("(?is)^.*<font size=6>([^<\\n]+?)</font.*$") ? output.replaceAll("(?is)^.*<font size=6>([^<\\n]+?)</font.*$", "$1").trim() : EMPTY;
            final String type = output.matches("(?is)^.*<b>Type :</b><br><a href=\"[^\"\\n]+?\">([^<]+?)<.*$") ? output.replaceAll("(?is)^.*<b>Type :</b><br><a href=\"[^\"\\n]+?\">([^<]+)<.*$", "$1").trim() : EMPTY;
            final String releasedBy = output.matches("(?is)^.*<b>Released by :</b><br><a href=\"[^\"]+?\">([^<\\n]+?)</a>.*$") ? output.replaceAll("(?is)^.*<b>Released by :</b><br><a href=\"[^\"]+?\">([^<\\n]+?)</a>.*$", "$1").trim() : EMPTY;
            final String date = output.matches("(?is)^.*<b>Release Date :</b><br>.*?<font [^>\\n]+?>([^<\\n]+?)</font>.*$") ? output.replaceAll("(?is)^.*<b>Release Date :</b><br>.*?<font [^>\\n]+?>([^<\\n]+?)</font>.*$","$1").trim() : EMPTY;
            return type.equalsIgnoreCase(OTHER_PLATFORM)
                    ? Collections.<ReleaseEntry> emptyList()
                    : asList(new ReleaseEntry(id, releaseUri, type, date, title, releasedBy, asList(link)));
        }
        Pattern p = Pattern.compile("<li>\\s*<a href=\"([^\\\"]+?)\">\\s*<img .*?Download.*?>\\s*</a>\\s*<a href=\"([^\\\"]+?)\">([^<]+?)</a>\\s*\\(([^\\)]+?)\\)(\\s*by\\s*.*?<font .*?>([^<]+?)<)?([^\\(\\n]*?\\(([^\\)]+?)\\))?.*?<br>");
        Matcher m = p.matcher(output);
        List<ReleaseEntry> urls = new ArrayList<>();
        while (m.find()) {
            int count = m.groupCount();
            final String link = "https://csdb.dk" + trim(m.group(1));
            final String releaseUri = "https://csdb.dk" + trim(m.group(2));
            final String id = trim(m.group(2).replaceAll("(?is)^.*/\\?id=(.*)$","$1"));
            final String title = trim(m.group(3));
            final String type = trim(m.group(4));
            final String releasedBy = trim(defaultString(count >= 6 ? m.group(6) : null));
            final String date = defaultString(count >= 8 ? m.group(8) : null);
            if (!type.equalsIgnoreCase(OTHER_PLATFORM)) urls.add(new ReleaseEntry(id, releaseUri, type, date, title, releasedBy, null));
        }
        return urls;
    }

    static class DownloadEntry implements Comparable<DownloadEntry> {
        public final String link;
        public final String caption;
        public final int downloads;

        public DownloadEntry(String link, String caption, int downloads) {
            this.link = defaultString(link);
            this.caption = defaultString(caption);
            this.downloads = downloads;
        }

        @Override
        public int compareTo(DownloadEntry o2) {
            if (o2 == null) return -1;
            String ext1 = defaultString(this.caption.replaceAll("^.*\\.([^\\.]+)$", "$1")).toLowerCase();
            String ext2 = defaultString(o2.caption.replaceAll("^.*\\.([^\\.]+)$", "$1")).toLowerCase();

            if ("prg".equals(ext1) && !"prg".equals(ext2))
                return -1;
            if ("prg".equals(ext2) && !"prg".equals(ext1))
                return 1;

            if ("p00".equals(ext1) && !"p00".equals(ext2))
                return -1;
            if ("p00".equals(ext2) && !"p00".equals(ext1))
                return 1;

            if ("t64".equals(ext1) && !"t64".equals(ext2))
                return -1;
            if ("t64".equals(ext2) && !"t64".equals(ext1))
                return 1;

            if ("d64".equals(ext1) && !"d64".equals(ext2))
                return -1;
            if ("d64".equals(ext2) && !"d64".equals(ext1))
                return 1;

            if ("zip".equals(ext1) && !"zip".equals(ext2))
                return -1;
            if ("zip".equals(ext2) && !"zip".equals(ext1))
                return 1;

            if ("d71".equals(ext1) && !"d71".equals(ext2))
                return -1;
            if ("d71".equals(ext2) && !"d71".equals(ext1))
                return 1;

            if ("d81".equals(ext1) && !"d81".equals(ext2))
                return -1;
            if ("d81".equals(ext2) && !"d81".equals(ext1))
                return 1;

            if (ext1.equals(ext2))
                return -compare(this.downloads, o2.downloads);
            else
                return -ext1.compareTo(ext2);
        }
    }

    private static String findDownloadLink(URL url) throws Exception {
        return findDownloadLink(defaultString(httpGet(url.toString())));
    }

    private static String findDownloadLink(String output) {
        // <a href="download.php?id=214496">http://csdb.dk/getinternalfile.php/177919/ultimate-term.d64</a>
        Pattern p = Pattern.compile("<a href=\"(download\\.php\\?id=[^\"]+?)\">([^<]+?)</a>( \\(downloads: [0-9]+\\))?");
        Matcher m = p.matcher(output);
        List<DownloadEntry> list = new ArrayList<>();
        while (m.find()) {
            final String link = "https://csdb.dk/release/" + trim(m.group(1));
            final String caption = trim(m.group(2));
            int downloads = 0;
            try {
                downloads = m.groupCount() >= 3 ? toInt(m.group(3).replaceAll("[^0-9]", EMPTY)) : 0;
            } catch (NullPointerException e) {
                // do nothing: downloads keeps 0
            }
            list.add(new DownloadEntry(link, caption, downloads));
        }
        Collections.sort(list);
        return list.size() == 0 ? EMPTY : list.get(0).link;
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
