package eu.sblendorio.bbs.tenants.minitel;


import static eu.sblendorio.bbs.core.MinitelControls.*;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

import eu.sblendorio.bbs.core.MinitelThread;
import eu.sblendorio.bbs.tenants.mixed.WikipediaCommons;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WikipediaMinitel extends MinitelThread {
    protected static final String DEFAULT_WIKIPEDIA_LANG = "DEFAULT_WIKIPEDIA_LANG";
    private static Logger logger = LogManager.getLogger(WikipediaMinitel.class);
    private byte[] mainLogo;
    private byte[] headLogo = null;
    private String lang;

    String HR_TOP;
    int screenLines = 18;

    @Override
    public void initBbs() throws Exception {
        HR_TOP = StringUtils.repeat('-', getScreenColumns() - 1);
    }

    public WikipediaMinitel() {
        mainLogo = readBinaryFile("minitel/wikipedia-title.vdt");
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
            write(MOVEXY, 0x40 + 8, 0x40 + 25);
            write(ESC, BACKGROUND_WHITE, ESC, CHAR_BLUE);
            print(" "+StringUtils.upperCase(lang));
            flush();
            resetInput();
            do {
                ch = readKey();

                if (ch == '.') {
                    return;
                } else if (ch == '1') {
                    write(MOVEXY, 0x40+19, 0x40+2);
                    print("                                     ");
                    write(MOVEXY, 0x40 + 8, 0x40 + 25);
                    write(ESC, BACKGROUND_BLACK, ESC, CHAR_WHITE);
                    print("        ");
                    write(MOVEXY, 0x40 + 8, 0x40 + 26);
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

                    write(MOVEXY, 0x40 + 8, 0x40 + 25);
                    write(ESC, BACKGROUND_WHITE, ESC, CHAR_BLUE);
                    print("        ");
                    write(MOVEXY, 0x40 + 8, 0x40 + 25);
                    write(ESC, BACKGROUND_WHITE, ESC, CHAR_BLUE);
                    print(" "+StringUtils.upperCase(lang));
                    continue;
                } else if (ch == '2') {
                    write(MOVEXY, 0x40 + 10, 0x40 + 8);
                    write(ESC, BACKGROUND_RED, ESC, CHAR_WHITE);
                    print(" 2. Search               ");
                } else if (ch == '3') {
                    write(MOVEXY, 0x40 + 12, 0x40 + 8);
                    write(ESC, BACKGROUND_RED, ESC, CHAR_WHITE);
                    print(" 3. I feel lucky         ");
                } else {
                    continue;
                }

                write(MOVEXY, 0x40+19, 0x40+2);
                print("                                     ");
                write(MOVEXY, 0x40+19, 0x40+2);
                print("Query> ");
                flush(); resetInput();
                write(CURSOR_ON);
                String keywords = readLine();
                write(CURSOR_OFF);

                if (StringUtils.isNotBlank(keywords)) {
                    write(MOVEXY, 0x40 + 19, 0x40 + 2);
                    print("                                     ");
                    write(MOVEXY, 0x40 + 19, 0x40 + 2);
                    print("PLEASE WAIT...");
                    write(CURSOR_ON);
                }

                items = (ch == '2')
                    ? WikipediaCommons.search(lang, keywords)
                    : WikipediaCommons.searchFirst (lang, keywords)
                ;

                flush(); resetInput();
                write(CURSOR_OFF);
                write(MOVEXY, 0x40+19, 0x40+2);
                print("                                     ");

                if (items.size() == 0) {
                    if (ch == '2') {
                        write(MOVEXY, 0x40 + 10, 0x40 + 8);
                        write(ESC, BACKGROUND_WHITE, ESC, CHAR_BLUE);
                        print(" 2. Search               ");
                    } else if (ch == '3') {
                        write(MOVEXY, 0x40 + 12, 0x40 + 8);
                        write(ESC, BACKGROUND_WHITE, ESC, CHAR_BLUE);
                        print(" 3. I feel lucky         ");
                    }

                    write(MOVEXY, 0x40+19, 0x40+2);
                    print("                                     ");
                    write(MOVEXY, 0x40+19, 0x40+2);
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
        write(readBinaryFile("minitel/wikipedia-header.vdt"));
        write(MOVEXY, 0x40+5, 0x40+1);
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
        List<String> rows = wordWrap(head);
        List<String> article = wordWrap(wikiText);
        rows.addAll(article);
        waitOff();

        rows.addAll(article);
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
        println();
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
                if (i < items.size()) println( (i+1) + ". " + StringUtils.substring(items.get(i).title,0,35));
            }
            write(MOVEXY, 24, 1);
            println();
            print("#, (N+-)Page (R)eload (.)Quit> ");
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

    protected List<String> wordWrap(String s) {
        String[] cleaned = filterPrintableWithNewline(s).split("\n");
        List<String> result = new ArrayList<>();
        for (String item: cleaned) {
            String[] wrappedLine = WordUtils
                    .wrap(item, getScreenColumns() - 1, "\n", true)
                    .split("\n");
            result.addAll(asList(wrappedLine));
        }
        return result;
    }
}
