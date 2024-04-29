package eu.sblendorio.bbs.tenants.petscii;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class OneRssReady64Petscii extends OneRssPetscii {

    protected void readSections() throws Exception {
        this.sections = new LinkedHashMap<>();
        this.sections.put("", new NewsSection(null, "https://ready64.org/rss/rss.php"));
        this.screenRows = 19;
        this.pageSize = 10;
        this.showAuthor = true;
        this.newlineAfterDate = false;
        this.LOGO_MENU = this.LOGO_SECTION = readBinaryFile("petscii/ready64.seq");
    }

}
