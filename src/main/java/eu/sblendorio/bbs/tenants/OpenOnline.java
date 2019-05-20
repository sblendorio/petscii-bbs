package eu.sblendorio.bbs.tenants;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import eu.sblendorio.bbs.core.HtmlUtils;
import eu.sblendorio.bbs.core.PetsciiThread;
import org.apache.commons.lang3.StringUtils;
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
import static eu.sblendorio.bbs.core.Utils.filterPrintableWithNewline;
import static java.lang.Math.min;
import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

public class OpenOnline extends WordpressProxy {

    public OpenOnline() {
        super();
        this.logo = LOGO;
        this.domain = "https://www.open.online";
        this.pageSize = 6;
        this.screenRows = 18;
        this.showAuthor = true;
    }

    public final static byte[] LOGO = new byte[] {
            18, 5, 32, 32, 32, 32, -94, -94, 32, 32, -94, -94, -69, 32, -94, -94,
            -94, 32, -94, 32, -84, -69, 32, 32, 32, -110, 13, 18, -95, 32, 32, -110,
            -66, 18, -66, -68, -110, -68, 18, 32, -110, 32, 18, 32, -110, 32, 18, 32,
            -110, 32, 18, 32, 32, 32, -110, 32, -68, -95, 18, -95, 32, 32, -110, -95,
            13, 32, 18, 32, 32, -110, 32, 18, 32, 32, -110, 32, 18, 32, -110, 32,
            -94, 18, -66, 32, -110, 32, -94, 18, -66, 32, -110, 32, 18, -68, -110, 32,
            18, -95, 32, 32, -110, 13, 32, 18, -95, 32, 32, -94, -110, -66, 18, -66,
            32, -110, 32, 18, 32, 32, 32, -110, 32, 18, -94, -94, 32, -110, 32, 18,
            32, -110, -95, 18, -95, 32, -110, -95, 13, 32, 32, 18, -94, -94, -94, -94,
            -94, -94, -94, -94, -94, -94, -94, -94, -94, -94, -94, -94, -94, -94, -94, -110, 13
    };

}
