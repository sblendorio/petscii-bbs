package eu.sblendorio.bbs.tenants.mixed;

import eu.sblendorio.bbs.core.BbsThread;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

import static java.lang.System.getProperty;
import static java.lang.System.getenv;
import static org.apache.commons.lang3.StringUtils.defaultString;

public class GeolocationCommons {
    private static Logger logger = LogManager.getLogger(GeolocationCommons.class);
    public static long CACHE_DURATION_MILLIS = 1000L * 60L * 60L * 24L * 2L;
    private static Map<String, String> cache = null;
    private static long latestCacheInit = 0;

    public static boolean isItaly(String ip) {
        return "IT".equalsIgnoreCase(getCountryByIp(ip));
    }

    public static boolean isLocalhost(String ip) {
        return "127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip);
    }

    public static String getCountryByIp(String ip) {
        ip = ip.replaceAll("(?is)[^0-9:\\.]","");
        if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip))
            return StringUtils.EMPTY;

        long now = System.currentTimeMillis();
        if (now - latestCacheInit > CACHE_DURATION_MILLIS) {
            cache = null;
            latestCacheInit = now;
        }
        if (cache == null) cache = new TreeMap<>();

        String cachedCountry = cache.get(ip);
        if (cachedCountry == null) {
            String country = callGetCountryByIp(ip);
            cache.put(ip, country);
            logger.debug("Missed; {} {}; Hash size={}", country, ip, cache.size());
            return country;
        } else {
            logger.debug("Cached; {} {}; Hash size={}", cachedCountry, ip, cache.size());
            return cachedCountry;
        }
    }

    public static String callGetCountryByIp(String ip) {
        ip = ip.replaceAll("(?is)[^0-9:\\.]","");
        if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip))
            return StringUtils.EMPTY;

        String apiKey = defaultString(getenv("GEOKEY"), getProperty("GEOKEY", "DUMMY"));
        if ("DUMMY".equalsIgnoreCase(apiKey))
            return StringUtils.EMPTY;

        try {
            String url = "https://api.ipgeolocation.io/timezone?apiKey="
                    + URLEncoder.encode(apiKey, "UTF-8")
                    + "&ip="
                    + URLEncoder.encode(ip, "UTF-8");

            JSONObject root = (JSONObject) BbsThread.httpGetJson(url);
            JSONObject geo = (JSONObject) root.get("geo");
            String countryCode2 = (String) geo.get("country_code2");

            return countryCode2;
        } catch (Exception e) {
            return StringUtils.EMPTY;
        }
    }

}
