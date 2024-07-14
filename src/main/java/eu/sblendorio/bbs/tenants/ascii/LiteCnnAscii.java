package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.tenants.mixed.LiteCnnCommons;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Math.min;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.util.Arrays.asList;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

@Hidden
public class LiteCnnAscii extends AsciiThread {
    protected int screenLines;
    protected Integer secondaryLogoSize = null;
    protected int mainLogoSize = 1;
    protected byte[] logo = "CNN\n".getBytes(StandardCharsets.ISO_8859_1);
    public byte[] LOGO_SECTION = "LOGO_SECTION".getBytes(ISO_8859_1);
    protected int pageSize = 10;
    protected int currentPage = 1;
    protected int gap = 4;
    protected boolean alwaysRefreshFeed = false;
    protected String HR_TOP;

    List<LiteCnnCommons.ArticleItem> posts = Collections.emptyList();

    public LiteCnnAscii() {
    }

    @Override
    public void initBbs() throws Exception {
        HR_TOP = StringUtils.repeat('-', getScreenColumns() - 1);
        screenRows = getScreenRows() - gap;
    }

    @Override
    public void doLoop() throws Exception {
        boolean keepGoing = listPosts();
        if (!keepGoing) return;

        while (true) {
            log("RssReader waiting for input");
            print(getScreenColumns() >= 40
                    ? "#, (N+-)Page (R)eload (.)Quit> "
                    : "(N+-)Page (.)Quit> "
            );
            resetInput();
            flush();
            String inputRaw = readLine();
            String input = lowerCase(trim(inputRaw));
            if (".".equals(input) || "exit".equals(input) || "quit".equals(input) || "q".equals(input)) {
                break;
            } else if (("+".equals(input) || "n".equals(input) || "n+".equals(input)) && currentPage*pageSize<posts.size()) {
                ++currentPage;
                if (alwaysRefreshFeed) posts = null;
                try {
                    listPosts();
                } catch (NullPointerException e) {
                    --currentPage;
                    if (alwaysRefreshFeed) posts = null;
                    listPosts();
                }
            } else if (("-".equals(input) || "n-".equals(input)) && currentPage > 1) {
                --currentPage;
                if (alwaysRefreshFeed) posts = null;
                listPosts();
            } else if ("--".equals(input)) {
                currentPage = 1;
                if (alwaysRefreshFeed) posts = null;
                listPosts();
            } else if ("r".equals(input) || "reload".equals(input) || "refresh".equals(input)) {
                posts = null;
                listPosts();
            } else if (toInt(input) >= 1 && toInt(input) <= posts.size()) {
                boolean exitByUser = displayPost(posts.get(toInt(input) - 1));
                if (exitByUser) listPosts();
                listPosts();
            } else if ("".equals(input)) {
                listPosts();
            }
        }
    }

    protected void emptyRow() {
        println();
    }

    private boolean displayPost(LiteCnnCommons.ArticleItem item) throws Exception {
        drawLogo();
        LiteCnnCommons.Article article = LiteCnnCommons.getArticle(item);
        List<String> rows = LiteCnnCommons.feedToText(this, article);

        int page = 1;
        int j = 0;
        boolean forward = true;
        while (j < rows.size()) {
            if (j > 0 && j % screenRows == 0 && forward) {
                emptyRow();
                print(getScreenColumns() >= 40
                        ? "-PAGE " + page + "-  SPACE=NEXT  -=PREV  .=EXIT"
                        : "(" + page + ") SPC -PREV .EXIT");

                flush();
                int ch;
                do {
                    resetInput();
                    ch = readKey();
                } while (ch == 27 || ch == 0);

                if (ch == '.') {
                    return true;
                } else if (ch == '-' && page > 1) {
                    j -= (screenRows * 2);
                    --page;
                    forward = false;
                    drawLogo();
                    continue;
                } else {
                    ++page;
                }
                drawLogo();
            }
            String row = rows.get(j);
            println(row);
            forward = true;
            ++j;
        }
        println();
        return false;
    }


    protected void drawLogo() {
        cls();
        write(logo);
        println();
    }

    private boolean listPosts() throws Exception {
        final int mainLogoSize = 2;
        cls();
        write(LOGO_SECTION);
        println();
        println();
        if (isEmpty(posts)) {
            posts = LiteCnnCommons.getArticles();
        }
        final int start = pageSize * (currentPage-1);
        final int end = min(pageSize + start, posts.size());

        long totalRows = 0;
        for (int i = start; i < end; ++i) {
            LiteCnnCommons.ArticleItem post = posts.get(i);
            print((i+1) + ".");
            final int iLen = (getScreenColumns()-3)-String.valueOf(i+1).length();
            String line = WordUtils.wrap(filterPrintable(htmlClean(post.title())), iLen, "\r", true);
            totalRows += 1 + line.chars().filter(ch -> ch == '\r').count();
            println(line.replaceAll("\r", newlineString() +" " + repeat(" ", (getScreenColumns()-3)-iLen)));
        }
        for (int i = 0; i <= (getScreenRows() - totalRows - mainLogoSize - (gap-2)); ++i) newline();
        flush();
        return true;
    }
}