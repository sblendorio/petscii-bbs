package eu.sblendorio.bbs.tenants.mixed;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Integer.valueOf;
import static java.lang.System.getProperty;
import static java.lang.System.getenv;
import static java.util.Calendar.*;
import static org.apache.commons.lang3.StringUtils.defaultString;

public class HolidayCommons {

    private static Logger logger = LogManager.getLogger(HolidayCommons.class);
    public static String XMAS_START_DEFAULT = "1206";
    public static String XMAS_END_DEFAULT = "0106";

    public static Set<String> italianIp = new TreeSet<>();

    public static int xmasNewYear() {
        return xmasNewYear(
                defaultString(getenv("XMAS_START"), getProperty("XMAS_START", XMAS_START_DEFAULT))
                        .substring(0, 2),
                Calendar.getInstance());
    }

    public static int currentYear() {
        Calendar c = Calendar.getInstance();
        return c.get(YEAR);
    }

    public static int xmasNewYear(String xmasStart, Calendar today) {
        DateFormat sdf = new SimpleDateFormat("MMdd");
        String todayMMDD = sdf.format(today.getTime());
        int year = today.get(YEAR);

        return todayMMDD.compareTo(xmasStart) >= 0 ? year + 1 : year;
    }

    public static boolean isXmasTime() {
        String xmasStartDef = defaultString(getenv("XMAS_START"), getProperty("XMAS_START", XMAS_START_DEFAULT));
        String xmasEndDef = defaultString(getenv("XMAS_END"), getProperty("XMAS_END", XMAS_END_DEFAULT));

        return isXmasTime(
                xmasStartDef,
                xmasEndDef,
                Calendar.getInstance()
        );
    }

    public static boolean isXmasTime(String xmasStartDef, String xmasEndDef, Calendar todayCal) {
        boolean ascent = xmasEndDef.compareTo(xmasStartDef) >=0;
        int xmasNewYear = xmasNewYear(xmasStartDef, todayCal);

        String year = String.format("%04d", xmasNewYear - (ascent ? 0 : 1));
        String nextYear = String.format("%04d", xmasNewYear);
        String month = String.format("%02d", todayCal.get(MONTH)+1);
        String day = String.format("%02d", todayCal.get(DAY_OF_MONTH));

        String today = String.format("%04d", todayCal.get(YEAR)) + month + day;
        String xmasStart = year + xmasStartDef;
        String xmasEnd = nextYear + xmasEndDef;

        return (today.compareTo(xmasStart) >= 0) && (today.compareTo(xmasEnd) <= 0);
    }

    public static boolean isAscanioDay() {
        Calendar c = Calendar.getInstance();
        int month = c.get(MONTH) + 1;
        int day = c.get(DAY_OF_MONTH);

        return (month == 1) && (day == 8);
    }

    public static void main(String[] args) {
        System.out.println("isXmas="+isXmasTime());
        System.out.println("new year="+xmasNewYear());
    }

    public static boolean isSanremo() {
        return new File(getProperty("user.home") + File.separator + "sanremo.txt").exists();
    }


}
