package eu.sblendorio.bbs.tenants.minitel;


import static eu.sblendorio.bbs.core.MinitelControls.*;
import eu.sblendorio.bbs.core.MinitelThread;
import eu.sblendorio.bbs.tenants.mixed.WikipediaCommons;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class WikipediaMinitel extends MinitelThread {
    protected static final String DEFAULT_WIKIPEDIA_LANG = "DEFAULT_WIKIPEDIA_LANG";
    private static Logger logger = LogManager.getLogger(WikipediaMinitel.class);
    private byte[] mainLogo;
    private byte[] headLogo = null;
    private String lang;

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
                    if (!WikipediaCommons.langs.contains(newLang)) {
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

    public void showSingleResult(WikipediaCommons.WikipediaItem item) {

    }

    public void chooseItem(List<WikipediaCommons.WikipediaItem> items) {

    }
}
