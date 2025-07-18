package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.BbsInputOutput;
import eu.sblendorio.bbs.core.HtmlUtils;
import eu.sblendorio.bbs.tenants.mixed.WikipediaCommons;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

public class WikipediaAscii extends AsciiThread {
    private BbsInputOutput interfaceType = null;
    protected static final String DEFAULT_WIKIPEDIA_LANG = "DEFAULT_WIKIPEDIA_LANG";
    private static Logger logger = LogManager.getLogger(WikipediaAscii.class);
    private String lang;

    String HR_TOP;
    int screenLines;

    @Override
    public void initBbs() throws Exception {
        HR_TOP = StringUtils.repeat('-', getScreenColumns() - 1);
        screenLines = getScreenRows()-4;
    }

    public WikipediaAscii() {
        super();
    }

    public WikipediaAscii(BbsInputOutput interfaceType) {
        this.interfaceType = interfaceType;
    }

    @Override
    public void doLoop() throws Exception {
        if (interfaceType != null) {
            this.setBbsInputOutput(interfaceType);
        }

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
            displayHeader();
            do {
                ch = readKey();

                if (ch == '.') {
                    return;
                } else if (ch == '1') {
                    println();
                    print("Language> ");
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
                    displayHeader();
                    continue;
                } else if (ch == '2') {
                    println("2: SEARCH");
                } else if (ch == '3') {
                    println("3: I FEEL LUCKY");
                } else if (ch == '4') {
                    println("4: PICK A RANDOM PAGE");
                } else {
                    continue;
                }
                println();
                String keywords = null;
                if (ch != '4') {
                    print("Query> ");
                    flush();
                    resetInput();
                    keywords = readLine();

                    if (StringUtils.isBlank(keywords)) {
                        displayHeader();
                        continue;
                    }
                }
                print("Searching...");

                switch (ch) {
                    case '2' -> items = WikipediaCommons.search(lang, keywords);
                    case '3' -> items = WikipediaCommons.searchFirst(lang, keywords);
                    default  -> items = WikipediaCommons.pickRandomPage(lang);
                }

                println();

                if (items.size() == 0) {
                    if (StringUtils.isNotBlank(keywords)) print("NO RESULT");
                    resetInput(); readKey();
                    break;
                }

                break;
            } while (true);

            if (items.size() == 1) {
                showSingleResult(items.get(0));
            } else if (items.size() > 1) {
                chooseItem(items);
            }
        } while (true);
    }

    public void displayHeader() throws IOException {
        cls();
        println("Wikipedia");
        println();
        println("1- Set language. Current: " + StringUtils.upperCase(lang));
        println("2- Search");
        println("3- I feel lucky");
        println("4- Pick a random page");
        println();
        println("Type '.' to go back");
        println();
        print(">");
        flush();
        resetInput();
    }

    public void waitOn() {
        print("...");
    }

    public void waitOff() {
        println();
    }

    public void showSingleResult(WikipediaCommons.WikipediaItem item) throws IOException, ParseException {
        cls();
        println("Wikipedia: "+StringUtils.substring(HtmlUtils.utilHtmlDiacriticsToAscii(item.title),0,getScreenColumns()-13));
        String wikiText = WikipediaCommons.getTextContent(item);
        wikiText = HtmlUtils.utilHtmlDiacriticsToAscii(wikiText);

        final String head = HtmlUtils.utilHtmlDiacriticsToAscii(item.title) + "\n" + HR_TOP;
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
                    println("Wikipedia: "+StringUtils.substring(HtmlUtils.utilHtmlDiacriticsToAscii(item.title),0,getScreenColumns()-13));
                    println();
                    continue;
                } else {
                    ++page;
                }
                cls();
                println("Wikipedia: "+StringUtils.substring(HtmlUtils.utilHtmlDiacriticsToAscii(item.title),0,getScreenColumns()-13));
                println();
            }
            String row = rows.get(j);
            println(row);
            forward = true;
            ++j;
        }
        println();
        println("Press any key...");
        flush(); resetInput(); keyPressed(60_000);
    }

    public void chooseItem(List<WikipediaCommons.WikipediaItem> items) throws IOException, ParseException {
        int offset = 0;
        int limit = screenLines-2;
        do {
            cls();
            println("Wikipedia");
            println();
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
            String inputRaw = readLine();
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
