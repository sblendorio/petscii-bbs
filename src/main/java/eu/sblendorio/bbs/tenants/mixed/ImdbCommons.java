package eu.sblendorio.bbs.tenants.mixed;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ImdbCommons {

    public static String httpGet(String url) throws IOException {
        final URL object=new URL(url);
        HttpURLConnection conn = (HttpURLConnection) object.openConnection();
        conn.setRequestProperty("User-Agent", "\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36\"");
        conn.setRequestProperty("Accept", "application/json, text/plain, */*");
        conn.setRequestProperty("Referer", "https://www.imdb.com/");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("GET");

        final StringBuilder sb;
        int responseCode = -1;
        try {
            responseCode = conn.getResponseCode();
        } catch (Exception e) {
            throw e;
        }
        if (responseCode >= 301 && responseCode <= 399) {
            final String newLocation = conn.getHeaderField("Location");
            return httpGet(newLocation);
        } else if (responseCode >= 200 && responseCode <= 299) {
            sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), UTF_8));
            String line;
            while ((line = br.readLine()) != null) sb.append(line + "\n");
            br.close();
            conn.disconnect();
            return sb.toString();
        } else {
            System.out.println(conn.getResponseMessage());
        }
        conn.disconnect();
        return null;
    }

    public static void search(String name) throws Exception {
        search(name, null, false, false);
    }

    public static void search(String name, Integer year, boolean person, boolean tv) throws Exception {
        String url = "https://imdb.com/find/?q=" + URLEncoder.encode(name, UTF_8) + (year != null ? "+" + year : "");
        String resp = httpGet(url);
        //System.out.println(resp);
        Document doc = Jsoup.parse(resp);

        // Estrai i risultati con il primo XPath
        Elements results = doc.select("section[data-testid='find-results-section-title'] > div > ul > li");

        // Filtra i risultati per TV se necessario
        if (tv) {
            results = results.select(":contains(TV)");
        }

        // Estrai e filtra i risultati per persona se necessario
        if (person) {
            results = doc.select("section[data-testid='find-results-section-name'] > div > ul > li");
            Elements filteredResults = new Elements();
            for (Element result : results) {
                if (result.select("a[href*='name']").size() > 0) {
                    filteredResults.add(result);
                }
            }
            results = filteredResults;
        }




        // Stampa la lunghezza dei risultati
        System.out.println("Number of results: " + results.size());

        // Crea l'output (lista di mappe)
        List<Map<String, String>> output = new ArrayList<>();
        for (Element result : results) {
            String title = result.text().replace("\n", " ");
            String uri = result.select("a").attr("href");
            if (!title.contains("Podcast") && !title.contains("Music Video")) {
                try {
                    String image = result.select("img").attr("src");
                    String fileId = uri.split("/")[2];
                    Map<String, String> resultData = new HashMap<>();
                    resultData.put("id", fileId);
                    resultData.put("name", title);
                    resultData.put("url", "https://www.imdb.com" + uri);
                    resultData.put("poster", image);
                    output.add(resultData);
                } catch (IndexOutOfBoundsException e) {
                    // Ignora eccezioni per immagini mancanti
                }
            }
        }

        // Stampa l'output
        System.out.println(output);

        // Simula l'assegnazione del risultato alla variabile `self.search_results`
        Map<String, Object> searchResults = new HashMap<>();
        searchResults.put("result_count", output.size());
        searchResults.put("results", output);

        // Stampa i risultati di ricerca
        System.out.println("Search Results: " + searchResults);
    }

    public static String get(String url) {
        String resultJson = "";
        try {
            Document doc = Jsoup.parse(httpGet(url));
            Elements scripts = doc.select("script[type='application/ld+json']");
            if (scripts.size() > 0) {
                Element script = scripts.first();
                resultJson = script.html().replaceAll("\n", ""); // rimuove newline
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        JsonObject result;
        try {
            result = JsonParser.parseString(resultJson).getAsJsonObject();
        } catch (Exception e) {
            return null;
        }

        Map<String, Object> output = new HashMap<>();
        output.put("type", result.has("@type") ? result.get("@type").getAsString() : null);
        output.put("name", result.has("name") ? result.get("name").getAsString() : null);
        output.put("url", "https://www.imdb.com" + (result.has("url") ? result.get("url").getAsString().split("/title")[1] : ""));
        output.put("poster", result.has("image") ? result.get("image").getAsString() : null);
        output.put("description", result.has("description") ? result.get("description").getAsString() : null);

        JsonObject review = result.has("review") ? result.getAsJsonObject("review") : new JsonObject();
        Map<String, Object> reviewMap = new HashMap<>();
        reviewMap.put("author", review.has("author") ? review.getAsJsonObject("author").get("name").getAsString() : null);
        reviewMap.put("dateCreated", review.has("dateCreated") ? review.get("dateCreated").getAsString() : null);
        reviewMap.put("inLanguage", review.has("inLanguage") ? review.get("inLanguage").getAsString() : null);
        reviewMap.put("heading", review.has("name") ? review.get("name").getAsString() : null);
        reviewMap.put("reviewBody", review.has("reviewBody") ? review.get("reviewBody").getAsString() : null);

        JsonObject reviewRating = review.has("reviewRating") ? review.getAsJsonObject("reviewRating") : new JsonObject();
        Map<String, Object> reviewRatingMap = new HashMap<>();
        reviewRatingMap.put("worstRating", reviewRating.has("worstRating") ? reviewRating.get("worstRating").getAsString() : null);
        reviewRatingMap.put("bestRating", reviewRating.has("bestRating") ? reviewRating.get("bestRating").getAsString() : null);
        reviewRatingMap.put("ratingValue", reviewRating.has("ratingValue") ? reviewRating.get("ratingValue").getAsString() : null);
        reviewMap.put("reviewRating", reviewRatingMap);
        output.put("review", reviewMap);

        JsonObject aggregateRating = result.has("aggregateRating") ? result.getAsJsonObject("aggregateRating") : new JsonObject();
        Map<String, Object> ratingMap = new HashMap<>();
        ratingMap.put("ratingCount", aggregateRating.has("ratingCount") ? aggregateRating.get("ratingCount").getAsString() : null);
        ratingMap.put("bestRating", aggregateRating.has("bestRating") ? aggregateRating.get("bestRating").getAsString() : null);
        ratingMap.put("worstRating", aggregateRating.has("worstRating") ? aggregateRating.get("worstRating").getAsString() : null);
        ratingMap.put("ratingValue", aggregateRating.has("ratingValue") ? aggregateRating.get("ratingValue").getAsString() : null);
        output.put("rating", ratingMap);

        output.put("contentRating", result.has("contentRating") ? result.get("contentRating").getAsString() : null);
        output.put("genre", result.has("genre") ? result.get("genre").getAsString() : null);
        output.put("datePublished", result.has("datePublished") ? result.get("datePublished").getAsString() : null);
        output.put("keywords", result.has("keywords") ? result.get("keywords").getAsString() : null);
        output.put("duration", result.has("duration") ? result.get("duration").getAsString() : null);

        List<Map<String, String>> actors = new ArrayList<>();
        if (result.has("actor")) {
            for (var actor : result.getAsJsonArray("actor")) {
                Map<String, String> actorMap = new HashMap<>();
                JsonObject actorObj = actor.getAsJsonObject();
                actorMap.put("name", actorObj.has("name") ? actorObj.get("name").getAsString() : null);
                actorMap.put("url", actorObj.has("url") ? actorObj.get("url").getAsString() : null);
                actors.add(actorMap);
            }
        }
        output.put("actor", actors);

        List<Map<String, String>> directors = new ArrayList<>();
        if (result.has("director")) {
            for (var director : result.getAsJsonArray("director")) {
                Map<String, String> directorMap = new HashMap<>();
                JsonObject directorObj = director.getAsJsonObject();
                directorMap.put("name", directorObj.has("name") ? directorObj.get("name").getAsString() : null);
                directorMap.put("url", directorObj.has("url") ? directorObj.get("url").getAsString() : null);
                directors.add(directorMap);
            }
        }
        output.put("director", directors);

        List<Map<String, String>> creators = new ArrayList<>();
        if (result.has("creator")) {
            for (var creator : result.getAsJsonArray("creator")) {
                JsonObject creatorObj = creator.getAsJsonObject();
                if ("Person".equals(creatorObj.get("@type").getAsString())) {
                    Map<String, String> creatorMap = new HashMap<>();
                    creatorMap.put("name", creatorObj.has("name") ? creatorObj.get("name").getAsString() : null);
                    creatorMap.put("url", creatorObj.has("url") ? creatorObj.get("url").getAsString() : null);
                    creators.add(creatorMap);
                }
            }
        }
        output.put("creator", creators);

        Gson gson = new Gson();
        return gson.toJson(output, Map.class);
    }

    public static void main(String[] args) throws Exception {
        search("supernatural",  null, false, false);
    }

}
