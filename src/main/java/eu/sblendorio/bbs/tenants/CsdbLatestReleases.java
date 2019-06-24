package eu.sblendorio.bbs.tenants;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.core.PetsciiThread;
import eu.sblendorio.bbs.core.XModem;
import org.apache.commons.lang3.math.NumberUtils;

import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Hidden
public class CsdbLatestReleases extends PetsciiThread {

    //public static final String RSS = "https://csdb.dk/rss/latestreleases.php";
    public static final String RSS = "http://www.sblendorio.eu/samplefeed.xml";


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

    @Override
    public void doLoop() throws Exception {
        Pattern p = Pattern.compile("(?is)<a href=['\\\"]([^'\\\"]*?)['\\\"] title=['\\\"][^'\\\"]*?\\.prg['\\\"]>");
        List<NewsFeed> entries = getFeeds(RSS);
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

        for (int i=0; i<list.size(); ++i) {
            ReleaseEntry entry = list.get(i);
            if (i < 11) println( i+". "+ entry.title+"/"+entry.releasedBy);
        }
        println();
        print("Select: ");
        String ch = readLine();
        int n = NumberUtils.toInt(ch);
        String url = list.get(n).links.get(0);
        println(list.get(n).links.toString());
        println("Press a key to start download");
        readKey();
        byte file[] = downloadFile(new URL(url));
        println("Please start XModem transfer");
        XModem xm = new XModem(cbm, cbm.out());
        xm.send(file);
    }


    public static List<CsdbLatestReleases.NewsFeed> getFeeds(String urlString) throws Exception {
        URL url = new URL(urlString);
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(url));
        List<CsdbLatestReleases.NewsFeed> result = new LinkedList<>();
        List<SyndEntry> entries = feed.getEntries();
        for (SyndEntry e : entries)
            result.add(new CsdbLatestReleases.NewsFeed(e.getPublishedDate(), e.getTitle(), e.getDescription().getValue(), e.getUri()));
        return result;
    }

}
