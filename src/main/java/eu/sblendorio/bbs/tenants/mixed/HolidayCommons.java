package eu.sblendorio.bbs.tenants.mixed;

import java.util.Calendar;

import static java.lang.Integer.valueOf;
import static java.lang.System.getProperty;
import static java.lang.System.getenv;
import static org.apache.commons.lang3.StringUtils.defaultString;

public class HolidayCommons {
    public static String XMAS_START_DEFAULT = "1206";
    public static String XMAS_END_DEFAULT = "0106";

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

    public static void main(String[] args) {
        System.out.println("isXmas="+isXmasTime());
    }
}
