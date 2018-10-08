package eu.sblendorio.bbs.tenants;

import com.google.common.collect.ImmutableMap;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class SpikeTelevideo {

    static String PREFIX = "http://www.servizitelevideo.rai.it/televideo/pub/";


    static class NewsSection {
        final String url;
        final byte[] logo;

        public NewsSection(String url, byte[] logo) {
            this.url = url; this.logo = logo;
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
        .put("Ultim'Ora", new NewsSection(PREFIX + "rss101.xml", null))
        .put("24h No Stop", new NewsSection(PREFIX + "rss102.xml", null))
        .put("Primopiano", new NewsSection(PREFIX + "rss110.xml", null))
        .put("Politica", new NewsSection(PREFIX + "rss120.xml", null))
        .put("Economia", new NewsSection(PREFIX + "rss130.xml", null))
        .put("Dall'Italia", new NewsSection(PREFIX + "rss140.xml", null))
        .put("Dal Mondo", new NewsSection(PREFIX + "rss150.xml", null))
        .put("Culture", new NewsSection(PREFIX + "rss160.xml", null))
        .put("Cittadini", new NewsSection(PREFIX + "rss170.xml", null))
        .put("Speciale", new NewsSection(PREFIX + "rss180.xml", null))
        .put("Atlante delle Crisi", new NewsSection(PREFIX + "rss190.xml", null))
        .put("Calcio", new NewsSection(PREFIX + "rss201.xml", null))
        .put("Brevi Calcio", new NewsSection(PREFIX + "rss229.xml", null))
        .put("Calcio Squadre ", new NewsSection(PREFIX + "rss230.xml", null))
        .put("Altri Sport", new NewsSection(PREFIX + "rss260.xml", null))
        .put("Brevissime", new NewsSection(PREFIX + "rss299.xml", null))
        .put("Motori", new NewsSection(PREFIX + "rss810.xml", null))
        .build();

    public static List<NewsFeed> getFeeds(String urlString) throws Exception {
        long a = System.currentTimeMillis();
        URL url = new URL(urlString);
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(url));
        List<NewsFeed> result = new LinkedList<>();
        List<SyndEntry> entries = feed.getEntries();
        long b = System.currentTimeMillis();
        System.out.println("=====> "+(b-a)+" millis");
        for (SyndEntry e : entries)
            result.add(new NewsFeed(e.getPublishedDate(), e.getTitle(), e.getDescription().getValue(), e.getUri()));
        return result;
    }

    public static Map<String, List<NewsFeed>> getAllFeeds() throws Exception {
        Map<String, List<NewsFeed>> result = new LinkedHashMap<>();
        for (Map.Entry<String, NewsSection> item: sections.entrySet())
            result.put(item.getKey(), getFeeds(item.getValue().url));
        return result;
    }

    public static void main(String[] args) throws Exception {
        long a = System.currentTimeMillis();
        Map<String, List<NewsFeed>> feeds = getAllFeeds();
        long b = System.currentTimeMillis();
        for (Map.Entry<String, List<NewsFeed>> feed: feeds.entrySet()) {
            System.out.println("-------------- " + feed.getKey() + " --------------------------");
            System.out.println(feed.getValue());
            System.out.println();
        }
        System.out.println("TIME: "+(b-a)+" millis");
    }

}
