package eu.sblendorio.bbs.tenants.mixed;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import static java.util.stream.Collectors.joining;

// Text of RaiNews articles, downloaded from the <link> of the RSS items
public class RaiNewsArticles {

    private static final String AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36";
    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    private static final long CACHE_TTL_MILLIS = Duration.ofMinutes(15).toMillis();
    private static final String PARAGRAPH_BREAK = "<br><br>";
    private static final Pattern SENTENCE_END = Pattern.compile("[.!?…][\"»”’)]*(?=\\s|$)");

    private record CachedParagraphs(List<String> paragraphs, long expiresAt) {}

    private static final Map<String, CachedParagraphs> CACHE = new ConcurrentHashMap<>();

    private RaiNewsArticles() {
    }

    // Downloads the article pages in parallel and returns their text, keyed by link, as paragraphs separated by <br><br>
    // and cut at a sentence end within about maxChars. Links that fail, time out or have no text are missing from the result
    public static Map<String, String> fetchTexts(Collection<String> links, int maxChars) {
        final long now = System.currentTimeMillis();
        CACHE.values().removeIf(cached -> cached.expiresAt() < now);

        final Map<String, List<String>> found = new HashMap<>();
        final Map<String, CompletableFuture<List<String>>> pending = new LinkedHashMap<>();
        try (HttpClient client = HttpClient.newBuilder()
                .connectTimeout(TIMEOUT)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build()) {
            for (String link : links) {
                if (StringUtils.isBlank(link) || found.containsKey(link) || pending.containsKey(link)) {
                    continue;
                }
                final CachedParagraphs cached = CACHE.get(link);
                if (cached != null) {
                    if (!cached.paragraphs().isEmpty()) {
                        found.put(link, cached.paragraphs());
                    }
                    continue;
                }
                final HttpRequest request = HttpRequest.newBuilder(URI.create(link))
                        .header("User-Agent", AGENT)
                        .timeout(TIMEOUT)
                        .GET()
                        .build();
                // null = download failed, not cached; an empty list (page without text, e.g. a video) is cached too
                pending.put(link, client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenApply(response -> response.statusCode() == 200 ? extractParagraphs(response.body()) : null)
                        .exceptionally(e -> null));
            }
            pending.forEach((link, future) -> {
                final List<String> paragraphs = future.join();
                if (paragraphs == null) {
                    return;
                }
                CACHE.put(link, new CachedParagraphs(paragraphs, now + CACHE_TTL_MILLIS));
                if (!paragraphs.isEmpty()) {
                    found.put(link, paragraphs);
                }
            });
        }

        final Map<String, String> result = new HashMap<>();
        found.forEach((link, paragraphs) -> result.put(link, toHtml(truncate(paragraphs, maxChars))));
        return result;
    }

    static List<String> extractParagraphs(String html) {
        return Jsoup.parse(html).select(".article__content .article__content__unit--text p").stream()
                .map(Element::text)
                .map(StringUtils::trim)
                .filter(StringUtils::isNotBlank)
                .toList();
    }

    // Keeps whole paragraphs while they fit; the first one that does not fit is cut at its last sentence end within
    // the limit, or at a word boundary followed by "..." when that would leave too little text
    static List<String> truncate(List<String> paragraphs, int maxChars) {
        final List<String> result = new ArrayList<>();
        int used = 0;
        for (String paragraph : paragraphs) {
            final int available = maxChars - used;
            if (paragraph.length() <= available) {
                result.add(paragraph);
                used += paragraph.length() + 2;
                continue;
            }
            int sentenceEnd = -1;
            final Matcher m = SENTENCE_END.matcher(paragraph);
            while (m.find() && m.end() <= available) {
                sentenceEnd = m.end();
            }
            if (sentenceEnd > 0 && used + sentenceEnd >= maxChars / 2) {
                result.add(paragraph.substring(0, sentenceEnd));
            } else if (used < maxChars / 2) {
                final int wordEnd = paragraph.lastIndexOf(' ', Math.max(available - 3, 0));
                result.add(paragraph.substring(0, wordEnd > 0 ? wordEnd : Math.max(available - 3, 0)) + "...");
            }
            break;
        }
        return result;
    }

    private static String toHtml(List<String> paragraphs) {
        return paragraphs.stream()
                .map(text -> text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;"))
                .collect(joining(PARAGRAPH_BREAK));
    }
}
