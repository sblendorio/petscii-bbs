package eu.sblendorio.bbs.tenants.petscii;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import eu.sblendorio.bbs.tenants.ascii.OneRssAscii;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class OneRssAJPlusPetscii extends OneRssPetscii {
    protected void readSections() throws Exception {
        this.sections = new LinkedHashMap<>();
        this.sections.put("1", new NewsSection("General news", "https://www.ajplus.net/stories?format=rss"));
        this.sections.put("2", new NewsSection("World", "https://www.ajplus.net/stories/tag/World?format=rss"));
        this.sections.put("3", new NewsSection("Climate Crisis", "https://www.ajplus.net/stories/tag/Climate?format=rss"));
        this.sections.put("4", new NewsSection("Culture", "https://www.ajplus.net/stories/tag/Culture?format=rss"));
        this.sections.put("5", new NewsSection("Good Trouble", "https://www.ajplus.net/stories/tag/Good+Trouble?format=rss"));
        this.sections.put("6", new NewsSection("(in)Justice", "https://www.ajplus.net/stories/tag/Injustice?format=rss"));
        this.sections.put("7", new NewsSection("Solutions", "https://www.ajplus.net/stories/tag/Solutions?format=rss"));
        this.sections.put("8", new NewsSection("Pandemic", "https://www.ajplus.net/stories/tag/Pandemic?format=rss"));
        this.screenRows = 19;
        this.pageSize = 10;
        this.showAuthor = true;
        this.newlineAfterDate = false;
        this.offsetX = 25;
        this.offsetY = 1;
        this.twoColumns = false;
        this.LOGO_MENU = this.LOGO_SECTION = readBinaryFile("petscii/aj.seq");
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

}
