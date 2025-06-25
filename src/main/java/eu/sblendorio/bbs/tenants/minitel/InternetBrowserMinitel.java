/*
 * Credits for this InternetBrowser:
 * Richard Bettridge (ssshake) of TheOldNet
 * http://bit.ly/38ZlPaS
 */
package eu.sblendorio.bbs.tenants.minitel;

import com.linkedin.urls.Url;
import com.linkedin.urls.detection.UrlDetector;
import com.linkedin.urls.detection.UrlDetectorOptions;
import eu.sblendorio.bbs.core.MinitelConstants;
import eu.sblendorio.bbs.core.MinitelControls;
import eu.sblendorio.bbs.core.MinitelThread;
import eu.sblendorio.bbs.tenants.CommonConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

public class InternetBrowserMinitel extends MinitelThread {

    public static final String URL_TEMPLATE = "http://www.frogfind.com/read.php?a=";

    public String userAgent = CommonConstants.get("BROWSER_USERAGENT", "");
    protected int currentPage = 1;
    protected int pageSize = 10;
    protected int screenRows = 18;
    Stack<String> history = new Stack<>();
    Map<Integer, Entry> links = emptyMap();

    public static List<Entry> getAllLinks(Document webpage) {
        List<Entry> urls = new ArrayList<>();
        Elements links = webpage.select("a[href]");

        for (Element link : links) {
            String label;
            if (isNotBlank(link.text())) {
                label = link.text();
            } else {
                try {
                    label = decodeUrl(link.attr("href"));
                } catch (ArrayIndexOutOfBoundsException e) {
                    label = link.attr("href");
                }
            }

            urls.add(new Entry(link.attr("href"), label));
        }
        return urls;
    }

    public void logo() throws Exception {
        write(MinitelControls.SCROLL_OFF);
        write(MinitelControls.CURSOR_OFF);
        cls();
        write(readBinaryFile("minitel/frogfind.vdt"));
        flush(); resetInput();
        keyPressed(120000L);
        cls();
        write(MinitelControls.CURSOR_ON);
        write(MinitelControls.SCROLL_ON);
    }

    @Override
    public void doLoop() throws Exception {
        logo();

        String address = makeUrl("w3.org");

        writeHeader();
        do {
            Document webpage = loadWebPage(address);
            String action = displayPage(webpage, address);

            if (action != null && action.startsWith("navigate:")) {
                address = action.substring("navigate:".length());
            } else if ("quit".equals(action)) {
                break;
            }

        } while (true);
    }

    static String makeLinkUrl(String url) {
        String finalUrl = Objects.toString(url, "")
                .trim()
                .replaceAll("^/read\\.php\\?a=", "");
        return URL_TEMPLATE + URLEncoder.encode(finalUrl, UTF_8);
    }

    static String makeUrl(String url) {
        if (!isUrl(url)) {
            return "http://www.frogfind.com/?q=" + URLEncoder.encode(url, UTF_8);
        }

        String finalUrl = Objects.toString(url, "").trim().toLowerCase().startsWith("http")
                ? Objects.toString(url, "").trim()
                : "https://" + Objects.toString(url, "").trim();
        return URL_TEMPLATE + URLEncoder.encode(finalUrl, UTF_8);
    }

    static boolean isUrl(String s) {
        UrlDetector parser = new UrlDetector(s, UrlDetectorOptions.Default);
        List<Url> found = parser.detect();
        return found != null && found.size() > 0;
    }

    String focusAddressBar() throws Exception {
        clearAddressBar();
        gotoXY(9, 1);
        flush();
        resetInput();
        String search = readLine();

        if (Objects.toString(search, "").trim().equals(".")) {
            return "_quit_program";
        }

        return makeUrl(search);
    }

    void clearAddressBar() {
        gotoXY(9, 1);
        print("                             ");
    }

    String enterAddress(String previousAddress) throws Exception {
        String url = focusAddressBar();
        if (Objects.toString(url, "").equals("_quit_program")) {
            throw new UnsupportedOperationException();
        }
        return "http://www.frogfind.com/?q=".equals(url.trim()) ? previousAddress : url;
    }

    void push(String url) {
        history.push(url);
    }

    String pop() {
        if (history.size() <= 1) return null;

        history.pop();
        return history.peek();
    }

    Document loadWebPage(String url) {
        if (history.isEmpty() || !history.peek().equals(url)) {
            push(url);
        }
        loading();
        clearBrowserWindow();
        return getWebpage(url);
    }

    protected String displayPage(Document webpage, String address) throws Exception {
        currentPage = 1; //reset this globally, not sure if required
        Pager pager = new Pager(true, 1, 0);
        final String content = formattedWebpage(webpage);
        writeAddressBar(address);
        List<String> rows = wordWrap(content);

        boolean startOfPage = true;
        while (pager.currentRow < rows.size() + 1) {
            logPaging(pager, rows);

            boolean startOfDocument = pager.page <= 1;
            boolean endOfDocument = pager.currentRow == rows.size();

            //boolean startOfPage = pager.currentRow % screenRows == 1;
            boolean endOfPage = pager.currentRow > 0 && pager.currentRow % screenRows == 0 && pager.forward;

            if (startOfPage) {
                printPageNumber(pager.page);
                gotoXY(0, 3);
            }

            if (endOfPage || endOfDocument) {
                parkCursor();

                String prompt = promptForUserInput(pager, webpage, address, startOfDocument, endOfDocument);
                startOfPage = true;
                if (prompt.startsWith("navigate:")) {
                    return prompt;
                }
                switch (prompt) {
                    case "skip":
                        continue;
                    case "exit":
                        return null;
                    default:
                        break;
                }
            }

            if (!endOfDocument) {
                printRowWithColor(pager, rows);
                startOfPage = false;
                pager.forward = true;
                ++pager.currentRow;
            }
        }
        return null;
    }

    void logPaging(Pager pager, List<String> rows) {
        log("Current Row: " + pager.currentRow);
        log("Rows: " + rows.size());
        log("Page: " + pager.page);
        log("Prior Page Start Row: " + (pager.page - 1) * screenRows);
    }

    String promptForUserInput(Pager pager, Document webpage, String currentAddress, boolean startOfDocument, boolean endOfDocument) throws Exception {
        String instruction = "";
        int key = getInputKey();
        if (key >= 193 && key <= 218) {
            key -= 96;
        }
        switch (key) {
            case 'u':
            case 'U':
                String newUrl = enterAddress(currentAddress);
                return "navigate:" + newUrl;

            case '.':
                throw new UnsupportedOperationException();

            case 'b':
            case 'B':
                String backPage = pop();
                if (backPage == null) {
                    return "skip";
                } else {
                    return "navigate:" + backPage;
                }

            case 'l':
            case 'L':
                String linkUrl = listLinksForPage(pager, webpage, currentAddress);
                if (linkUrl != null) {
                    return linkUrl;
                }
                writeAddressBar(currentAddress);
                break;

            case 'p':
            case 'P':
                if (startOfDocument) {
                    instruction = "skip";
                    break;
                }

                loadPreviousPage(pager, currentAddress);
                instruction = "skip";
                break;

            case 'n':
            case 'N':
                if (endOfDocument) {
                    instruction = "skip";
                    break;
                }
                loadNextPage(pager, currentAddress);
                break;

            default:
                instruction = "skip";
        }
        return instruction;
    }

    void loadPreviousPage(Pager pager, String head) {
        --pager.page;
        pager.currentRow = (pager.page - 1) * screenRows;
        pager.forward = false;
        prepareDisplayForNewPage(head);
    }

    void loadNextPage(Pager pager, String head) {
        ++pager.page;
        pager.forward = true;
        prepareDisplayForNewPage(head);
    }

    String listLinksForPage(Pager pager, Document webpage, String currentAddress) throws Exception {
        String selectedUrl = getAndDisplayLinksOnPage(webpage, currentAddress);
        if (selectedUrl != null) {
            return "navigate:" + selectedUrl;
        }

        clearBrowserWindow();
        pager.currentRow = 0;
        pager.page = 0;
        return null;
    }

    int getInputKey() throws Exception {
        resetInput();
        return readKey();
    }

    void parkCursor() {
        gotoXY(8, 1);
    }

    String formattedWebpage(Document webpage) {
        return webpage
            .toString()
            .replaceAll("<img.[^>]*>", "<br>[IMAGE] ")
            .replaceAll("<a.[^>]*>", " <br>[LINK] ")
            .replaceAll("&quot;", "\"")
            .replaceAll("&apos;", "'")
            .replaceAll("&#xA0;", " ")
            .replaceAll("(?is)<script .*</script>", EMPTY)
            .replaceAll("(?is)^[\\s\\n\\r]+|^\\s*(</?(br|div|figure|iframe|img|p|h[0-9])[^>]*>\\s*)+", EMPTY)
            .replaceAll("(?is)^(<[^>]+>(\\s|\n|\r)*)+", EMPTY);
    }

    void printRowWithColor(Pager pager, List<String> rows) {
        String row = rows.get(pager.currentRow);
        String patternStringLink = ".*\\[LINK\\].*";
        Pattern patternLink = Pattern.compile(patternStringLink);
        Matcher matcherLink = patternLink.matcher(row);
        boolean matchesLink = matcherLink.matches();

        String patternStringImage = ".*\\[IMAGE\\].*";
        Pattern patternImage = Pattern.compile(patternStringImage);
        Matcher matcherImage = patternImage.matcher(row);
        boolean matchesImage = matcherImage.matches();

        if (matchesLink) {
            attributes(MinitelControls.CHAR_GREEN); //write(LIGHT_BLUE);
        }

        if (matchesImage) {
            attributes(MinitelControls.CHAR_MAGENTA); //write(YELLOW);
        }
        //gotoXY(0, pager.currentRow % screenRows + 3);
        println(row);

        if (matchesLink || matchesImage) {
            attributes(MinitelControls.CHAR_WHITE); //write(GREY3);
        }
    }

    void printPageNumber(int page) {
        gotoXY(1, 22);
        print("PAGE " + String.format("%-3s", page));
    }

    void prepareDisplayForNewPage(String head) {
        loading();
        clearBrowserWindow();
        writeAddressBar(head);
    }

    static String decodeUrl(String url) {
        return URLDecoder.decode(
                        Objects.toString(url, "")
                                .trim()
                                .replaceAll("^(http://www\\.frogfind\\.com)?/read\\.php\\?a=", ""), UTF_8)
                .replaceAll("(?i)^http://", "")
                .replaceAll("(?i)^https://", "")
                .replaceAll("(?i)^http:", "")
                .replaceAll("(?i)^https:", "");
    }

    void writeAddressBar(String url) {

        String tempUrl = decodeUrl(url);
        clearAddressBar();
        gotoXY(9, 1);
        //write(GREEN);
        print(StringUtils.left(tempUrl, 28));
        gotoXY(0, 3);
        //write(GREY3);
    }

    String getAndDisplayLinksOnPage(Document webpage, String currentAddress) throws Exception {
        loading();
        while (true) {
            writeAddressBar(currentAddress);
            listLinks(webpage);
            if (links == null) return null;

            attributes(MinitelControls.CHAR_WHITE); //write(WHITE);
            print("Enter Link # or Command> ");
            attributes(MinitelControls.CHAR_WHITE); //write(GREY3);

            resetInput();
            flush();

            String inputRaw = readLine();
            String input = lowerCase(trim(inputRaw));

            // QUIT
            if ("".equals(input) || "b".equals(input) || ".".equals(input) || "exit".equals(input) || "quit".equals(input) || "q".equals(input)) {
                return null;
            }

            // NEXT PAGE
            else if ("n".equals(input) || "N".equals(input)) {
                ++currentPage;
                links = null;
            }

            // PREVIOUS PAGE
            else if (("p".equals(input) || "P".equals(input)) && currentPage > 1) {
                --currentPage;
                links = null;
            }

            // SUCCESS PATH
            else if (links.containsKey(toInt(input))) {
                final Entry link = links.get(toInt(input));
                return makeLinkUrl(link.url);
            }
        }
    }

    private void listLinks(Document webpage) throws Exception {
        clearForLinks();
        gotoXY(0, 4);
        attributes(MinitelControls.CHAR_WHITE); //write(ORANGE);
        println("Links On Page:");
        println();

        List<Entry> entries = getAllLinks(webpage);

        if (isEmpty(entries)) {
            links = null;
            attributes(MinitelControls.CHAR_RED); //write(RED);
            println("No links on page");
            flush();
            resetInput();
            readKey();
            return;
        }

        links = getLinksForPage(entries, currentPage, pageSize);
        for (Map.Entry<Integer, Entry> entry : links.entrySet()) {
            int i = entry.getKey();
            Entry post = entry.getValue();

            attributes(MinitelControls.CHAR_WHITE); //write(WHITE);
            print(i + ".");
            attributes(MinitelControls.CHAR_GREEN); //write(GREY3);

            final int iLen = 37 - String.valueOf(i).length();

            String title = post.name;
            String line = WordUtils.wrap(filterPrintable(htmlClean(title)), iLen, "\r", true);

            println(line.replaceAll("\r", "\r " + repeat(" ", 37 - iLen)));
        }
        newline();
    }

    static private Map<Integer, Entry> getLinksForPage(List<Entry> entries, int page, int perPage) {
        Map<Integer, Entry> result = new LinkedHashMap<>();
        if (page < 1 || perPage < 1) return result;

        for (int i = (page - 1) * perPage; i < page * perPage; ++i) {
            if (i < entries.size()) {
                result.put(i + 1, entries.get(i));
            }
        }
        return result;
    }

    public Document getWebpage(String url) {
        Document doc;
        try {
            doc = Jsoup
                    .connect(url)
                    .userAgent(userAgent)
                    .get();
        } catch (Exception ex) {
            ex.printStackTrace();
            doc = Jsoup.parseBodyFragment("HTTP connection error");
        }
        String html = doc.toString()
                .replaceAll("(?s)<p>\\s*View page images: <a href=.?image\\.php\\?i=http.*?</p>", "")
                .replaceAll("(?s)<body>.*?</form>", "<body>")
                .replaceAll("(?s)<head>.*?</head>", "")
                .replaceAll("(?s)<title>.*?</title>", "")
                .replaceAll("(?s)<style>.*?</style>", "")
                .replaceAll("<p><font color=.?red.?>Article is missing Content-Type or Content-Length header<br></font></p>", "")
                .replaceAll("(?s)<a [^>]*nofollow[^>]>\\s*</a>", "");

        doc = Jsoup.parse(html);

        return doc;
    }

    protected List<String> wordWrap(String s) {
        String[] cleaned = filterPrintableWithNewline(htmlClean(s)).split("\n");
        List<String> result = new ArrayList<>();
        for (String item : cleaned) {
            String[] wrappedLine = WordUtils
                    .wrap(item, 39, "\n", true)
                    .split("\n");
            result.addAll(asList(wrappedLine));
        }
        return result;
    }

    private void loading() {
        gotoXY(9, 1);
        attributes(MinitelControls.CHAR_RED); //write(PURPLE);
        print("LOADING...                   ");
        attributes(MinitelControls.CHAR_WHITE); //write(GREY3);
        flush();
    }

    private void clearBrowserWindow() {
        clearForLinks();
    }

    private void clearForLinks() {
        //write(GREY3);
        gotoXY(0, 3);
        for (int i = 0; i < 18; ++i) {
            for (int j = 0; j < 39; ++j) {
                write(' ');
            }
            println();
        }
        flush();
        attributes(MinitelControls.CHAR_WHITE); //write(GREY3);
    }

    private void writeHeader() throws Exception {
        write(MinitelControls.SCROLL_OFF);
        cls();
        write(readBinaryFile("minitel/browser.vdt"));
        flush(); resetInput();
        gotoXY(0, 0);
        write(MinitelControls.SCROLL_ON);
        write(MinitelControls.CURSOR_ON);
    }

    public static class Entry {
        public final String name;
        public final String url;
        public final String fileType;

        public Entry(String url, String name) {
            this.url = Objects.toString(url, "");
            if (name.length() > 60) {
                // this.name = " ..." + StringUtils.right(name, 31).trim();
                this.name = StringUtils.left(name, 31).trim() + " ...";
            } else {
                this.name = StringUtils.left(name, 35).trim();
            }
            this.fileType = Objects.toString(this.name, "").replaceAll("(?is)^.*\\.(.*?)$", "$1").toLowerCase();
        }
    }

    public static class Pager {
        public boolean forward;
        public int page;
        public int currentRow;

        public Pager(boolean forward, int page, int currentRow) {
            this.forward = forward;
            this.page = page;
            this.currentRow = currentRow;
        }
    }
}