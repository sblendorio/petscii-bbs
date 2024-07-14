package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.tenants.mixed.LiteCnnCommons;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static eu.sblendorio.bbs.core.Utils.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.math.NumberUtils.toInt;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

@Hidden
public class LiteCnnAscii extends AsciiThread {
    protected int screenLines;
    protected Integer secondaryLogoSize = null;
    protected int mainLogoSize = 1;
    protected byte[] logo = "CNN".getBytes(StandardCharsets.ISO_8859_1);

    List<LiteCnnCommons.ArticleItem> posts = Collections.emptyList();

    public LiteCnnAscii() {

    }

    @Override
    public void doLoop() throws Exception {
        screenLines = getScreenRows() - 3 - (secondaryLogoSize == null ? mainLogoSize : secondaryLogoSize);
        listPosts();
        while (true) {
            log("Wordpress waiting for input");
            print(getScreenColumns() >= 40
                    ? "(N+-)Page (H)elp (R)eload (.)Quit> "
                    : "(N+-)Page (.)Quit> "
            );
            resetInput();
            flush();
            String inputRaw = readLine(setOfChars(STR_ALPHANUMERIC, ".:,;_ []()<>@+-*/^='?!$%&#"));
            String input = lowerCase(trim(inputRaw));
        }
    }

    protected void drawLogo() {
        write(logo);
        newline();
        newline();
    }

    private void listPosts() throws Exception {
        cls();
        drawLogo();
        if (posts.isEmpty()) {
            posts = LiteCnnCommons.getArticles();
        }
        long totalRows = 0;
        for (int i=0; i<posts.size(); i++) {
            LiteCnnCommons.ArticleItem post = posts.get(i);
            print(i + ".");
            final int nCols = getScreenColumns() - 3;
            final int iLen = nCols-String.valueOf(i).length();
            String line = WordUtils.wrap(filterPrintable(htmlClean(post.title())), iLen, "\r", true);
            totalRows += 1 + line.chars().filter(ch -> ch == '\r').count();
            println(line.replaceAll("\r", newlineString() + " " + repeat(" ", nCols-iLen)));
        }
        for (int i = 0; i < (getScreenRows() - totalRows - mainLogoSize - 2); ++i) newline();

    }
}