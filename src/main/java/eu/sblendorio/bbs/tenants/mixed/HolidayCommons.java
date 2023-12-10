package eu.sblendorio.bbs.tenants.mixed;

import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.tenants.ascii.ChatA1;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Set;
import java.util.TreeSet;

import static java.lang.Integer.valueOf;
import static java.lang.System.getProperty;
import static java.lang.System.getenv;
import static org.apache.commons.lang3.StringUtils.defaultString;

public class HolidayCommons {

    private static Logger logger = LogManager.getLogger(HolidayCommons.class);
    public static String XMAS_START_DEFAULT = "1206";
    public static String XMAS_END_DEFAULT = "0106";

    public static Set<String> specialIp = new TreeSet<>();

    public static int xmasNewYear() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int xmasStartMonth =
                valueOf(defaultString(getenv("XMAS_START"), getProperty("XMAS_START", XMAS_START_DEFAULT))
                        .substring(0, 2)
                );

        return month >= xmasStartMonth ? year + 1 : year;
    }

    public static boolean isXmasTime() {
        Calendar c = Calendar.getInstance();
        String year = String.format("%04d",c.get(Calendar.YEAR));
        String nextYear = String.format("%04d",c.get(Calendar.YEAR) + 1);
        String month = String.format("%02d", c.get(Calendar.MONTH)+1);
        String day = String.format("%02d", c.get(Calendar.DAY_OF_MONTH));
        String xmasStartDef = defaultString(getenv("XMAS_START"), getProperty("XMAS_START", XMAS_START_DEFAULT));
        String xmasEndDef = defaultString(getenv("XMAS_END"), getProperty("XMAS_END", XMAS_END_DEFAULT));
        if (xmasEndDef.compareTo(xmasStartDef) >=0) {
            nextYear = year;
        }

        String today = year + month + day;
        String xmasStart = year + xmasStartDef;
        String xmasEnd = nextYear + xmasEndDef;

        return (today.compareTo(xmasStart) >= 0) && (today.compareTo(xmasEnd) <= 0);
    }

    public static String getCountryFromIp(String ip) {
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
            logger.debug("{}: {}", countryCode2, ip);

            return countryCode2;
        } catch (Exception e) {
            return StringUtils.EMPTY;
        }
    }

    public static boolean isAscanioDay() {
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        return (month == 1) && (day == 8);
    }


    public static void main(String[] args) {
        System.out.println("isXmas="+isXmasTime());
    }
}
