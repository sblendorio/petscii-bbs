package eu.sblendorio.bbs.tenants.petscii;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class OneRssOpenOnline extends OneRssPetscii {
    protected void readSections() throws Exception {
        this.sections = new LinkedHashMap<>();
        this.sections.put("1", new OneRssPetscii.NewsSection("Ultime notizie", "https://www.open.online/feed/"));
        this.sections.put("2", new OneRssPetscii.NewsSection("Fact checking", "https://www.open.online/c/fact-checking/feed"));
        this.sections.put("3", new OneRssPetscii.NewsSection("Scienze", "https://www.open.online/c/scienze/feed"));
        this.sections.put("4", new OneRssPetscii.NewsSection("Attualita'", "https://www.open.online/c/attualita/feed"));
        this.sections.put("5", new OneRssPetscii.NewsSection("Politica", "https://www.open.online/c/politica/feed"));
        this.sections.put("6", new OneRssPetscii.NewsSection("David Puente", "https://www.open.online/author/david-puente/feed"));
        this.sections.put("7", new OneRssPetscii.NewsSection("Juanne Pili", "https://www.open.online/author/juanne-pili/feed"));
        this.screenRows = 18;
        this.pageSize = 4;
        this.showAuthor = true;
        this.newlineAfterDate = false;
        this.offsetX = 25;
        this.twoColumns = false;
        this.LOGO_MENU = LOGO_BYTES_CENTER;
        this.LOGO_SECTION = LOGO_BYTES;
    }

    @Override
    public String getArticleBody(SyndEntry e) {
        if (e == null || e.getContents() == null)
            return "";
        return e.getContents().stream()
            .map(SyndContent::getValue)
            .map(StringUtils::defaultString)
            .collect(Collectors.joining("<br>"));
    }

    private static final byte[] LOGO_BYTES_CENTER = new byte[] {
        32, 32, 32, 32, 32, 32, 32, 32,
        18, 5, 32, 32, 32, 32, -94, -94, 32, 32,
        -94, -94, -69, 32, -94, -94, -94, 32, -94, 32,
        -84, -69, 32, 32, 32, -110, 13,

        32, 32, 32, 32, 32, 32, 32, 32,
        18, -95, 32,
        32, -110, -66, 18, -66, -68, -110, -68, 18, 32,
        -110, 32, 18, 32, -110, 32, 18, 32, -110, 32,
        18, 32, 32, 32, -110, 32, -68, -95, 18, -95,
        32, 32, -110, -95, 13,

        32, 32, 32, 32, 32, 32, 32, 32,
        32, 18, 32, 32, -110,
        32, 18, 32, 32, -110, 32, 18, 32, -110, 32,
        -94, 18, -66, 32, -110, 32, -94, 18, -66, 32,
        -110, 32, 18, -68, -110, 32, 18, -95, 32, 32,
        -110, 13,

        32, 32, 32, 32, 32, 32, 32, 32,
        32, 18, -95, 32, 32, -94, -110, -66,
        18, -66, 32, -110, 32, 18, 32, 32, 32, -110,
        32, 18, -94, -94, 32, -110, 32, 18, 32, -110,
        -95, 18, -95, 32, -110, -95, 13,

        32, 32, 32, 32, 32, 32, 32, 32,
        32, 32, 18,
        -94, -94, -94, -94, -94, -94, -94, -94, -94, -94,
        -94, -94, -94, -94, -94, -94, -94, -94, -94, -110,
        13 };

    private static final byte[] LOGO_BYTES = new byte[] {
        18, 5, 32, 32, 32, 32, -94, -94, 32, 32,
        -94, -94, -69, 32, -94, -94, -94, 32, -94, 32,
        -84, -69, 32, 32, 32, -110, 13,

        18, -95, 32,
        32, -110, -66, 18, -66, -68, -110, -68, 18, 32,
        -110, 32, 18, 32, -110, 32, 18, 32, -110, 32,
        18, 32, 32, 32, -110, 32, -68, -95, 18, -95,
        32, 32, -110, -95, 13,

        32, 18, 32, 32, -110,
        32, 18, 32, 32, -110, 32, 18, 32, -110, 32,
        -94, 18, -66, 32, -110, 32, -94, 18, -66, 32,
        -110, 32, 18, -68, -110, 32, 18, -95, 32, 32,
        -110, 13,

        32, 18, -95, 32, 32, -94, -110, -66,
        18, -66, 32, -110, 32, 18, 32, 32, 32, -110,
        32, 18, -94, -94, 32, -110, 32, 18, 32, -110,
        -95, 18, -95, 32, -110, -95, 13,

        32, 32, 18,
        -94, -94, -94, -94, -94, -94, -94, -94, -94, -94,
        -94, -94, -94, -94, -94, -94, -94, -94, -94, -110,
        13 };
}
