package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.HtmlUtils;
import eu.sblendorio.bbs.core.PetsciiThread;
import eu.sblendorio.bbs.tenants.mixed.WikipediaCommons;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;

import static eu.sblendorio.bbs.core.PetsciiColors.*;
import static eu.sblendorio.bbs.core.PetsciiKeys.*;
import static eu.sblendorio.bbs.core.BlockGraphicsPetscii.getRenderedMidres;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

public class WikipediaPetscii extends PetsciiThread {
    protected static final String DEFAULT_WIKIPEDIA_LANG = "DEFAULT_WIKIPEDIA_LANG";
    private static Logger logger = LogManager.getLogger(WikipediaPetscii.class);
    private static byte[] mainLogo = readBinaryFile("petscii/wikipedia-title.vdt");
    private static byte[] headLogo = readBinaryFile("petscii/wikipedia-header.vdt");;
    private String lang;

    String HR_TOP;
    int screenLines = 18;

    @Override
    public void initBbs() throws Exception {
        HR_TOP = StringUtils.repeat(chr(163), getScreenColumns() - 1);
    }

    public WikipediaPetscii() {
    }

    @Override
    public void doLoop() throws Exception {
        try {
            lang = (String) getRoot().getCustomObject(DEFAULT_WIKIPEDIA_LANG);
        } catch (NullPointerException | ClassCastException e) {
            lang = "en";
            logger.debug("Using default language: {}", lang);
        }
        if (lang == null) lang = "en";
        cls();
        write(19, WHITE, DOWN);
        write(getRenderedMidres(14, WikipediaCommons.WIKILOGO, false, false));
        write(19, GREY1, DOWN, DOWN, DOWN, DOWN);
        write(getRenderedMidres(14, WikipediaCommons.WIKILOGO_SHADOW, false, true));

        write(19, GREY2);
        write(getRenderedMidres(0,WikipediaCommons.WIKILOGO_2, false, false));

        write(19, LIGHT_BLUE, DOWN, DOWN, DOWN, DOWN);
        write(getRenderedMidres(5, WikipediaCommons.WIKI_VERTICAL_2, true, true));

        write(GREY3, RIGHT);
        print("Press any key ");
        flush();resetInput();
        keyPressed(40_000L);

        int ch;
        do {
            List<WikipediaCommons.WikipediaItem> items;
            cls();
            write(mainLogo);
            gotoXY(24, 8);
            write(GREY3, REVON);
            print(" "+StringUtils.upperCase(lang));
            flush();
            resetInput();
            do {
                ch = readKey();

                if (ch == '.') {
                    return;
                } else if (ch == '1') {
                    write(REVON, BLUE);
                    gotoXY(1,20);
                    print("                                     ");
                    gotoXY(24,8);
                    write(REVOFF, WHITE);
                    print("        ");
                    write(LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT);
                    flush(); resetInput();
                    String newLang;
                    newLang = readLine(6);
                    if ("".equals(newLang)) newLang = lang;
                    newLang = StringUtils.lowerCase(newLang);
                    if (!WikipediaCommons.LATIN_ALPHAPET_LANGS.contains(newLang)) {
                        write(7);
                        newLang = lang;
                    }
                    lang = newLang;
                    getRoot().setCustomObject(DEFAULT_WIKIPEDIA_LANG, lang);

                    gotoXY(24,8);
                    write(REVON, GREY3);
                    write(LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT);
                    gotoXY(24,8);
                    print(" "+StringUtils.upperCase(lang));
                    for (int i=0; i<7-lang.length(); i++) write(' ');
                    write(LEFT);
                    continue;
                } else if (ch == '2') {
                    gotoXY(6,10);
                    write(REVON, RED);
                    print(" 2. Search                ");
                } else if (ch == '3') {
                    gotoXY(6, 12);
                    write(REVON, RED);
                    print(" 3. I feel lucky          ");
                } else if (ch == '4') {
                    gotoXY(6, 14);
                    write(REVON, RED);
                    print(" 4. Pick a random page    ");
                } else {
                    continue;
                }

                String keywords = "dummy";
                if (ch != '4') {
                    write(BLUE, REVON);
                    gotoXY(1, 20);
                    println("                                     ");
                    write(UP, RIGHT, REVON);
                    print(" Query> ");
                    write(REVOFF, WHITE);
                    println("                             ");
                    write(UP, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT);
                    flush();
                    resetInput();
                    keywords = readLine(29);
                }
                if (StringUtils.isNotBlank(keywords)) {
                    write(BLUE, REVON);
                    gotoXY(1,20);
                    print("                                     ");
                    gotoXY(1,20);
                    print(" PLEASE WAIT...");
                }

                switch (ch) {
                    case '2': items = WikipediaCommons.search(lang, keywords); break;
                    case '3': items = WikipediaCommons.searchFirst(lang, keywords); break;
                    default:  items = WikipediaCommons.pickRandomPage(lang);
                }

                flush(); resetInput();
                write(REVON, BLUE);
                gotoXY(1,20);
                print("                                     ");

                if (items.size() == 0) {
                    if (ch == '2') {
                        gotoXY(6,10);
                        write(REVON, GREY3);
                        print(" 2. Search                ");
                    } else if (ch == '3') {
                        gotoXY(6,12);
                        write(REVON, GREY3);
                        print(" 3. I feel lucky          ");
                    }

                    write(REVON, BLUE);
                    gotoXY(1,20);
                    print("                                     ");
                    gotoXY(1,20);
                    if (StringUtils.isNotBlank(keywords)) {
                        write(BLUE);
                        print(" ");
                        write(RED);
                        print(" NO RESULT ");
                    }
                    continue;
                }

                break;
            } while (true);

            if (items.size() == 1) {
                showSingleResult(items.get(0));
            } else {
                chooseItem(items);
            }
        } while (true);
    }

    public void displayHeader() {
        write(headLogo);
        println();
        write(GREY3);
    }

    public void waitOn() {
        write(GREY3);
        print("PLEASE WAIT...");
    }

    public void waitOff() {
        for (int i=0; i<14; i++) write(DEL);
    }

    public void showSingleResult(WikipediaCommons.WikipediaItem item) throws IOException, ParseException {
        cls();
        displayHeader();
        waitOn();
        String wikiText = WikipediaCommons.getTextContent(item);
        wikiText = HtmlUtils.utilHtmlDiacriticsToAscii(wikiText);
        final String head = HtmlUtils.utilHtmlDiacriticsToAscii(item.title) + "\n" + HR_TOP;
        List<String> rows = WikipediaCommons.wordWrap(head, this);
        List<String> article = WikipediaCommons.wordWrap(wikiText, this);
        rows.addAll(article);
        waitOff();
        write(GREY3);

        int page = 1;
        int j = 0;
        boolean forward = true;
        while (j < rows.size()) {
            if (j>0 && j % screenLines == 0 && forward) {
                println();
                write(WHITE);
                print(getScreenColumns() >= 40
                        ? "-PAGE " + page + "-  SPACE=NEXT  -=PREV  .=EXIT"
                        :  "(" + page + ") SPC -PREV .EXIT"
                );

                resetInput(); int ch = readKey();
                if (ch == '.') {
                    return;
                }
                if (ch == '-' && page > 1) {
                    j -= (screenLines *2);
                    --page;
                    forward = false;
                    cls();
                    displayHeader();
                    write(GREY3);
                    continue;
                } else {
                    ++page;
                }
                cls();
                displayHeader();
                write(GREY3);
            }
            String row = rows.get(j);
            println(row);
            forward = true;
            ++j;
        }
        gotoXY(0, 24); print("Press any key...");
        flush(); resetInput(); keyPressed(60_000);
    }

    public void chooseItem(List<WikipediaCommons.WikipediaItem> items) throws IOException, ParseException {
        int offset = 0;
        int limit = 16;
        do {
            cls();
            displayHeader();
            write(GREY3);

            println("Select search result:");
            println(HR_TOP);
            for (int i = offset; i < offset + limit; i++) {
                int numLen = String.valueOf(offset+limit+1).length();
                if (i < items.size()) {
                    String title = HtmlUtils.utilHtmlDiacriticsToAscii(items.get(i).title);
                    println(
                        String.format("%"+numLen+"d", (i+1)) +
                        ". " +
                        StringUtils.substring(title,0, getScreenColumns()-numLen-3));
                }
            }
            println();
            print("#, (N+)Next (-)Prev (.)Quit> ");
            resetInput();
            flush();
            String inputRaw = readLineNoCr();
            String input = lowerCase(trim(inputRaw));

            if (".".equals(input) || "exit".equals(input) || "quit".equals(input) || "q".equals(input)) {
                break;
            } else if (("+".equals(input) || "n".equals(input) || "n+".equals(input)) && offset+limit<items.size()) {
                offset += limit;
            } else if (("-".equals(input) || "n-".equals(input)) && offset > 0) {
                offset -= limit;
            } else if ("--".equals(input)) {
                offset = 0;
            } else if (toInt(input) >= 1 && toInt(input) <= items.size()) {
                showSingleResult(items.get(toInt(input)-1));
            }
        } while (true);
    }
}
