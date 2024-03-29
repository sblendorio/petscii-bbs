package eu.sblendorio.bbs.tenants.prestel;

import eu.sblendorio.bbs.core.PrestelThread;
import eu.sblendorio.bbs.core.Utils;
import eu.sblendorio.bbs.tenants.mixed.WikipediaCommons;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;

import static eu.sblendorio.bbs.core.MinitelControls.CURSOR_ON;
import static eu.sblendorio.bbs.core.PrestelControls.*;
import static eu.sblendorio.bbs.core.Utils.bytes;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

public class WikipediaPrestel extends PrestelThread {
    protected static final String DEFAULT_WIKIPEDIA_LANG = "DEFAULT_WIKIPEDIA_LANG";
    private static Logger logger = LogManager.getLogger(WikipediaPrestel.class);
    private byte[] mainLogo;
    private byte[] headLogo;
    private String lang;

    String HR_TOP;
    int screenLines = 18;

    @Override
    public void initBbs() throws Exception {
        HR_TOP = StringUtils.repeat('-', getScreenColumns() - 1);
    }

    public WikipediaPrestel() {
        mainLogo = readBinaryFile("prestel/wikipedia-title.cept3");
        headLogo = readBinaryFile("prestel/wikipedia-header.cept3");
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

        int ch;
        do {
            List<WikipediaCommons.WikipediaItem> items;
            write(CURSOR_OFF);
            cls();
            write(mainLogo);
            gotoXY(24, 8);
            // write(bytes(BACKGROUND_RED, CHAR_BLUE));
            print(" "+StringUtils.upperCase(lang));
            flush();
            resetInput();
            do {
                ch = readKey();

                if (ch == '.') {
                    return;
                } else if (ch == '1') {
                    gotoXY(1, 18);
                    print("                                      ");
                    gotoXY(24, 8);
                    write(bytes(CHAR_WHITE, 93));
                    print("        ");
                    gotoXY(27, 8);
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

                    gotoXY(24,8);
                    write(bytes(BACKGROUND_WHITE, CHAR_BLUE));
                    print("        ");
                    gotoXY(24,8);
                    write(bytes(BACKGROUND_WHITE, CHAR_BLUE));
                    print(" "+StringUtils.upperCase(lang));
                    continue;
                } else if (ch == '2') {
                    gotoXY(7, 9);
                    write(bytes(BACKGROUND_RED, CHAR_WHITE));
                    print(" 2. Search               ");
                } else if (ch == '3') {
                    gotoXY(7, 11);
                    write(bytes(BACKGROUND_RED, CHAR_WHITE));
                    print(" 3. I feel lucky         ");
                } else {
                    continue;
                }

                gotoXY(1, 18);
                print("                                      ");
                gotoXY(1, 18);
                print("Query> ");
                flush(); resetInput();
                write(CURSOR_ON);
                String keywords = readLine(31);
                write(CURSOR_OFF);

                if (StringUtils.isNotBlank(keywords)) {
                    gotoXY(1, 18);
                    print("                                      ");
                    gotoXY(1, 18);
                    print("PLEASE WAIT...");
                    write(CURSOR_ON);
                }

                items = (ch == '2')
                    ? WikipediaCommons.search(lang, keywords)
                    : WikipediaCommons.searchFirst(lang, keywords)
                ;

                flush(); resetInput();
                write(CURSOR_OFF);
                gotoXY(1, 18);
                print("                                      ");

                if (items.size() == 0) {
                    if (ch == '2') {
                        gotoXY(7, 9);
                        write(bytes(BACKGROUND_WHITE, CHAR_BLUE));
                        print(" 2. Search               ");
                    } else if (ch == '3') {
                        gotoXY(7, 11);
                        write(bytes(BACKGROUND_WHITE, CHAR_BLUE));
                        print(" 3. I feel lucky         ");
                    }

                    gotoXY(1, 18);
                    print("                                      ");
                    gotoXY(1, 18);
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
            if (j>0 && j % screenLines == 0 && forward) {
                println();
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
        gotoXY(0, 23); print("Press any key...");
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
            String inputRaw = readLine();
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
