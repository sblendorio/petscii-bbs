package eu.sblendorio.bbs.tenants;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import eu.sblendorio.bbs.core.HtmlUtils;
import eu.sblendorio.bbs.core.PetsciiThread;
import eu.sblendorio.bbs.core.XModem;
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
import static java.util.Collections.emptyMap;
import static org.apache.commons.collections4.MapUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

public class CsdbLatestReleases extends PetsciiThread {

    public static final String RSS = "https://csdb.dk/rss/latestreleases.php";

    protected int currentPage = 1;
    protected int pageSize = 10;

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

    static class ReleaseEntry {
        final String id;
        final String type;
        final Date publishedDate;
        final String title;
        final String releasedBy;
        final List<String> links;

        public ReleaseEntry(String id, String type, Date publishedDate, String title, String releasedBy, List<String> links) {
            this.id = id; this.type = type;
            this.publishedDate = publishedDate; this.title = title; this.releasedBy = releasedBy; this.links = links;
        }

        public String toString() {
            return "Title: "+title+"\nDate:"+publishedDate+"\nreleasedBy:"+releasedBy+"\nlinks:"+links+"\n";
        }

    }

    protected Map<Integer, ReleaseEntry> posts = emptyMap();

    @Override
    public void doLoop() throws Exception {
        listPosts();
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
                listPosts();
                continue;
            } else if ("+".equals(input)) {
                ++currentPage;
                posts = null;
                try {
                    listPosts();
                } catch (NullPointerException e) {
                    --currentPage;
                    posts = null;
                    listPosts();
                    continue;
                }
                continue;
            } else if ("-".equals(input) && currentPage > 1) {
                --currentPage;
                posts = null;
                listPosts();
                continue;
            } else if ("--".equals(input) && currentPage > 1) {
                currentPage = 1;
                posts = null;
                listPosts();
                continue;
            } else if ("r".equals(input) || "reload".equals(input) || "refresh".equals(input)) {
                posts = null;
                listPosts();
                continue;
            } else if (posts.containsKey(toInt(input))) {
                displayPost(toInt(input));
                listPosts();
            } else if ("".equals(input)) {
                listPosts();
                continue;
            }
        }
        flush();
    }


    protected void displayPost(int n) throws Exception {
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
        final URL url = new URL(p.links.get(0));
        final String title = p.title;
        final String type = p.type;
        final String id = p.id;
        byte[] content = downloadFile(url);
        waitOff();

        write(WHITE); print(title);
        write(GREY3); print(" from ");
        write(WHITE); print(releasedBy);
        println();
        write(GREY3); print("Type: ");
        write(WHITE); print(type);
        println();
        write(GREY3); print("Date: ");
        write(WHITE); print(strDate);
        println();
        println();
        write(GREY3); println("URL:");
        write(WHITE); println(url.toString());
        println();
        write(GREY3); println("Press any key to prepare to download");
        println("Or press \".\" to abort it");
        resetInput(); int ch = readKey();
        if (ch == '.') return;
        println();
        write(WHITE); println("Let's start XMODEM transfer!");
        XModem xm = new XModem(cbm, cbm.out());
        xm.send(content);
        readKey();
        resetInput();
    }


    protected void listPosts() throws Exception {
        cls();
        logo();
        if (isEmpty(posts)) {
            waitOn();
            posts = getPosts(currentPage, pageSize);
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

    private static List<ReleaseEntry> getReleases(List<NewsFeed> entries) throws Exception {
        Pattern p = Pattern.compile("(?is)<a href=['\\\"]([^'\\\"]*?)['\\\"] title=['\\\"][^'\\\"]*?\\.prg['\\\"]>");
        List<ReleaseEntry> list = new LinkedList<>();
        for (NewsFeed item: entries) {
            if (item.description.matches("(?is).*=\\s*[\\\"'][^\\\"']*\\.prg[^\\\"']*[\\\"'].*")) {
                String id = item.uri.replaceAll("(?is).*id=([0-9a-zA-Z_\\-]+).*$", "$1"); // https://csdb.dk/release/?id=178862&rs
                String releasedBy = item.description.replaceAll("(?is).*Released by:\\s*[^>]*>(.*?)<.*", "$1");
                String type = item.description.replaceAll("(?is).*Type:\\s*[^>]*>(.*?)<.*", "$1");
                Matcher m = p.matcher(item.description);
                List<String> urls = new ArrayList<>();
                while (m.find()) urls.add(m.group(1));
                list.add(new ReleaseEntry(id, type, item.publishedDate, item.title, releasedBy, urls));
            }
        }
        return list;
    }


    protected Map<Integer, ReleaseEntry> getPosts(int page, int perPage) throws Exception {
        if (page < 1 || perPage < 1) return null;

        List<NewsFeed> entries = getFeeds(RSS);
        List<ReleaseEntry> list = getReleases(entries);

        Map<Integer, ReleaseEntry> result = new LinkedHashMap<>();
        for (int i=(page-1)*perPage; i<page*perPage; ++i)
            if (i<list.size()) result.put(i+1, list.get(i));
        return result;
        /*
        JSONArray posts = (JSONArray) httpGetJson(getApi() + "posts?context=view&page="+page+"&per_page="+perPage);
        for (int i=0; i<posts.size(); ++i) {
            Post post = new Post();
            JSONObject postJ = (JSONObject) posts.get(i);
            post.id = (Long) postJ.get("id");
            post.content = ((String) ((JSONObject) postJ.get("content")).get("rendered")).replaceAll("(?is)(\\[/?vc_[^]]*\\])*", EMPTY);
            post.title = (String) ((JSONObject) postJ.get("title")).get("rendered");
            post.date = ((String) postJ.get("date")).replace("T", SPACE).replaceAll(":\\d\\d\\s*$", EMPTY);
            post.excerpt = (String) ((JSONObject) postJ.get("excerpt")).get("rendered");
            post.authorId = toLong(postJ.get("author").toString());
            result.put(i+1+(perPage*(page-1)), post);
        }
        return result;
         */
    }


    private static List<NewsFeed> getFeeds(String urlString) throws Exception {
        URL url = new URL(urlString);
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(url));
        List<CsdbLatestReleases.NewsFeed> result = new LinkedList<>();
        List<SyndEntry> entries = feed.getEntries();
        for (SyndEntry e : entries)
            result.add(new CsdbLatestReleases.NewsFeed(e.getPublishedDate(), e.getTitle(), e.getDescription().getValue(), e.getUri()));
        return result;
    }

    protected void help() throws Exception {
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
        print("WAIT PLEASE...");
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
