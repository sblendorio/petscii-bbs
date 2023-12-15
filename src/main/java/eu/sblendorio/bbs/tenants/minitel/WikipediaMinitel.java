package eu.sblendorio.bbs.tenants.minitel;

import eu.sblendorio.bbs.core.BlockGraphicsMinitel;
import eu.sblendorio.bbs.core.MinitelThread;
import eu.sblendorio.bbs.tenants.mixed.WikipediaCommons;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;

import static eu.sblendorio.bbs.core.MinitelControls.*;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

public class WikipediaMinitel extends MinitelThread {
    protected static final String DEFAULT_WIKIPEDIA_LANG = "DEFAULT_WIKIPEDIA_LANG";
    private static Logger logger = LogManager.getLogger(WikipediaMinitel.class);
    private static byte[] mainLogo = readBinaryFile("minitel/wikipedia-title.vdt");
    private static byte[] headLogo  = readBinaryFile("minitel/wikipedia-header.vdt");
    private static byte[] drcsLogo = readBinaryFile("minitel/wikipedia-drcs-complete.vdt");
    private String lang;

    String HR_TOP;
    int screenLines = 18;

    @Override
    public void initBbs() throws Exception {
        HR_TOP = StringUtils.repeat('`', getScreenColumns() - 1);
    }

    public WikipediaMinitel() {
        super();
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

        write(CURSOR_OFF);
        cls();
        write(SCROLL_OFF);
        write(GRAPHICS_MODE);
        write(readBinaryFile("minitel/wikipedia-big-logo.vdt"));

        gotoXY(0,0);
        write(GRAPHICS_MODE);
        attributes(CHAR_CYAN);
        write(BlockGraphicsMinitel.getRenderedMidres(0, WikipediaCommons.WIKILOGO_3, false, true));

        //gotoXY(0,5);
        //write(GRAPHICS_MODE);
        //attributes(CHAR_RED);
        //write(BlockGraphicsMinitel.getRenderedMidres(0, WikipediaCommons.WIKI_VERTICAL_3));

        flush(); resetInput();
        write(TEXT_MODE);
        attributes(CHAR_WHITE);

        //gotoXY(16,0);
        //attributes(FLASH_ON);
        //print("Press a key");
        //attributes(FLASH_OFF);

        write(SCROLL_ON);
        keyPressed(40_000L);
        doLoopNoDrcs();

    }


    public void doLoopDrcs() throws Exception {
        int ch;
        do {
            List<WikipediaCommons.WikipediaItem> items;
            write(CURSOR_OFF);
            cls();
            write(drcsLogo);
            gotoXY(28, 6);
            attributes(BACKGROUND_WHITE, CHAR_BLUE);
            print(" "+StringUtils.upperCase(lang));
            flush();
            resetInput();
            do {
                ch = readKey();

                if (ch == '.') {
                    return;
                } else if (ch == '1') {
                    gotoXY(0, 18);
                    print("                                        ");
                    gotoXY(28, 6);
                    attributes(BACKGROUND_RED, CHAR_WHITE);
                    print("        ");
                    gotoXY(29, 6);
                    flush(); resetInput();
                    String newLang;
                    write(CURSOR_ON);
                    newLang = readLine(6);
                    write(CURSOR_OFF);
                    if ("".equals(newLang)) newLang = lang;
                    newLang = StringUtils.lowerCase(newLang);
                    if (!WikipediaCommons.LATIN_ALPHAPET_LANGS.contains(newLang)) {
                        write(BEEP);
                        newLang = lang;
                    }
                    lang = newLang;
                    getRoot().setCustomObject(DEFAULT_WIKIPEDIA_LANG, lang);

                    gotoXY(28,6);
                    attributes(BACKGROUND_WHITE, CHAR_BLUE);
                    print("        ");
                    gotoXY(28,6);
                    attributes(BACKGROUND_WHITE, CHAR_BLUE);
                    print(" "+StringUtils.upperCase(lang));
                    continue;
                } else if (ch == '2') {
                    gotoXY(14, 8);
                    attributes(BACKGROUND_RED, CHAR_WHITE);
                    print(" 2. Search              ");
                } else if (ch == '3') {
                    gotoXY(14, 10);
                    attributes(BACKGROUND_RED, CHAR_WHITE);
                    print(" 3. I feel lucky        ");
                } else if (ch == '4') {
                    gotoXY(14, 12);
                    attributes(BACKGROUND_RED, CHAR_WHITE);
                    print(" 4. Pick a random page  ");
                } else {
                    continue;
                }

                String keywords = "dummy";
                if (ch != '4') {
                    gotoXY(0, 18);
                    print("                                        ");
                    gotoXY(0, 18);
                    print(" Query> ");
                    flush();
                    resetInput();
                    write(CURSOR_ON);
                    keywords = readLine(31);
                    write(CURSOR_OFF);
                }

                if (StringUtils.isNotBlank(keywords)) {
                    gotoXY(0, 18);
                    print("                                        ");
                    gotoXY(0, 18);
                    print(" PLEASE WAIT...");
                    write(CURSOR_ON);
                }

                switch (ch) {
                    case '2': items = WikipediaCommons.search(lang, keywords); break;
                    case '3': items = WikipediaCommons.searchFirst(lang, keywords); break;
                    default:  items = WikipediaCommons.pickRandomPage(lang);
                }

                flush(); resetInput();
                write(CURSOR_OFF);
                gotoXY(0, 18);
                print("                                        ");

                if (items.size() == 0) {
                    if (ch == '2') {
                        gotoXY(14, 8);
                        attributes(BACKGROUND_WHITE, CHAR_BLUE);
                        print(" 2. Search              ");
                    } else if (ch == '3') {
                        gotoXY(14, 10);
                        attributes(BACKGROUND_WHITE, CHAR_BLUE);
                        print(" 3. I feel lucky        ");
                    }

                    gotoXY(0, 18);
                    print("                                        ");
                    gotoXY(0, 18);
                    if (StringUtils.isNotBlank(keywords)) {
                        attributes(BACKGROUND_RED, CHAR_WHITE, FLASH_ON);
                        print(" NO RESULT ");
                        attributes(FLASH_OFF);
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

    public void doLoopNoDrcs() throws Exception {
        int ch;
        do {
            List<WikipediaCommons.WikipediaItem> items;
            write(CURSOR_OFF);
            cls();
            write(mainLogo);
            gotoXY(24, 7);
            attributes(BACKGROUND_WHITE, CHAR_BLUE);
            print(" "+StringUtils.upperCase(lang));
            flush();
            resetInput();
            do {
                ch = readKey();

                if (ch == '.') {
                    return;
                } else if (ch == '1') {
                    gotoXY(1, 19);
                    print("                                      ");
                    gotoXY(24, 7);
                    attributes(BACKGROUND_BLACK, CHAR_WHITE);
                    print("        ");
                    gotoXY(25, 7);
                    flush(); resetInput();
                    String newLang;
                    write(CURSOR_ON);
                    newLang = readLine(6);
                    write(CURSOR_OFF);
                    if ("".equals(newLang)) newLang = lang;
                    newLang = StringUtils.lowerCase(newLang);
                    if (!WikipediaCommons.LATIN_ALPHAPET_LANGS.contains(newLang)) {
                        write(BEEP);
                        newLang = lang;
                    }
                    lang = newLang;
                    getRoot().setCustomObject(DEFAULT_WIKIPEDIA_LANG, lang);

                    gotoXY(24,7);
                    attributes(BACKGROUND_WHITE, CHAR_BLUE);
                    print("        ");
                    gotoXY(24,7);
                    attributes(BACKGROUND_WHITE, CHAR_BLUE);
                    print(" "+StringUtils.upperCase(lang));
                    continue;
                } else if (ch == '2') {
                    gotoXY(7, 9);
                    attributes(BACKGROUND_RED, CHAR_WHITE);
                    print(" 2. Search               ");
                } else if (ch == '3') {
                    gotoXY(7, 11);
                    attributes(BACKGROUND_RED, CHAR_WHITE);
                    print(" 3. I feel lucky         ");
                } else if (ch == '4') {
                    gotoXY(7, 13);
                    attributes(BACKGROUND_RED, CHAR_WHITE);
                    print(" 4. Pick a random page   ");
                } else {
                    continue;
                }

                String keywords = "dummy";
                if (ch != '4') {
                    gotoXY(1, 19);
                    print("                                      ");
                    gotoXY(1, 19);
                    print("Query> ");
                    flush();
                    resetInput();
                    write(CURSOR_ON);
                    keywords = readLine(31);
                    write(CURSOR_OFF);
                }

                if (StringUtils.isNotBlank(keywords)) {
                    gotoXY(1, 19);
                    print("                                      ");
                    gotoXY(1, 19);
                    print("PLEASE WAIT...");
                    write(CURSOR_ON);
                }

                switch (ch) {
                    case '2': items = WikipediaCommons.search(lang, keywords); break;
                    case '3': items = WikipediaCommons.searchFirst(lang, keywords); break;
                    default:  items = WikipediaCommons.pickRandomPage(lang);
                }

                flush(); resetInput();
                write(CURSOR_OFF);
                gotoXY(1, 19);
                print("                                      ");

                if (items.size() == 0) {
                    if (ch == '2') {
                        gotoXY(7, 9);
                        attributes(BACKGROUND_WHITE, CHAR_BLUE);
                        print(" 2. Search               ");
                    } else if (ch == '3') {
                        gotoXY(7, 11);
                        attributes(BACKGROUND_WHITE, CHAR_BLUE);
                        print(" 3. I feel lucky         ");
                    }

                    gotoXY(1, 19);
                    print("                                      ");
                    gotoXY(1, 19);
                    if (StringUtils.isNotBlank(keywords)) print("NO RESULT");
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
        gotoXY(0, 4);
    }

    public void waitOn() {
        print("PLEASE WAIT...");
    }

    public void waitOff() {
        for (int i=0; i<14; i++) write(CURSOR_LEFT);
        print("              ");
        for (int i=0; i<14; i++) write(CURSOR_LEFT);
    }

    public void showSingleResult(WikipediaCommons.WikipediaItem item) throws IOException, ParseException {
        cls();
        displayHeader();
        waitOn();
        String wikiText = WikipediaCommons.getTextContent(item);
        final String head = item.title + "\n" + HR_TOP;
        List<String> rows = WikipediaCommons.wordWrap(head, this);
        List<String> article = WikipediaCommons.wordWrap(wikiText, this);
        rows.addAll(article);
        waitOff();

        int page = 1;
        int j = 0;
        boolean forward = true;
        while (j < rows.size()) {
            if (j > 0 && j % screenLines == 0 && forward) {
                println();
                print(getScreenColumns() >= 40
                        ? "-PAGE " + page + "-  SPACE=NEXT  -=PREV  .=EXIT"
                        : "(" + page + ") SPC -PREV .EXIT"
                );

                resetInput();
                int ch = readKey();
                if (ch == '.') {
                    return;
                }
                if (ch == '-' && page > 1) {
                    j -= (screenLines * 2);
                    --page;
                    forward = false;
                    cls();
                    displayHeader();
                    continue;
                } else {
                    ++page;
                }
                cls();
                displayHeader();
            }
            String row = rows.get(j);
            println(row);
            forward = true;
            ++j;
        }
        gotoXY(0, 23); print("Press any key..."); write(CURSOR_ON);
        flush(); resetInput(); keyPressed(60_000);
    }

    public void chooseItem(List<WikipediaCommons.WikipediaItem> items) throws IOException, ParseException {
        int offset = 0;
        int limit = 16;
        do {
            cls();
            displayHeader();
            println("Select search result:");
            println(HR_TOP);
            for (int i = offset; i < offset + limit; i++) {
                int numLen = String.valueOf(offset+limit+1).length();
                if (i < items.size()) println(
                        String.format("%"+numLen+"d", (i+1)) +
                        ". " +
                        StringUtils.substring(items.get(i).title,0, getScreenColumns()-numLen-3));
            }
            gotoXY(0, 23);
            print("#, (N+)Next (-)Prev (.)Quit> ");
            write(CURSOR_ON);
            resetInput();
            flush();
            String inputRaw = readLineNoCr();
            String input = lowerCase(trim(inputRaw));
            write(CURSOR_OFF);

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
