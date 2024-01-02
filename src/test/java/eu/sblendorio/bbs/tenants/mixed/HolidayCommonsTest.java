package eu.sblendorio.bbs.tenants.mixed;

import org.junit.jupiter.api.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static eu.sblendorio.bbs.tenants.mixed.HolidayCommons.isXmasTime;
import static eu.sblendorio.bbs.tenants.mixed.HolidayCommons.xmasNewYear;
import static org.junit.jupiter.api.Assertions.*;

public class HolidayCommonsTest {

    private Calendar cal(String strDate) throws ParseException {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(sdf.parse(strDate));
        return c;
    }

    @Test
    public void shouldDetectXmasNewYear() throws Exception {
        assertEquals(2023, xmasNewYear("1220", cal("2023-12-10")));
        assertEquals(2025, xmasNewYear("1220", cal("2024-12-20")));
        assertEquals(2027, xmasNewYear("1220", cal("2027-01-20")));
        assertEquals(2027, xmasNewYear("1220", cal("2026-12-22")));
    }

    @Test
    public void shouldDetectXmasTime() throws Exception {
        assertFalse(isXmasTime("1220", "0106", cal("2020-12-19")));
        assertTrue(isXmasTime("1220", "0106", cal("2020-12-21")));
        assertTrue(isXmasTime("1220", "0106", cal("2021-01-06")));
        assertFalse(isXmasTime("1220", "0106", cal("2021-01-07")));
    }
}
