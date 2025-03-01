/*
 * Credits for this InternetBrowser:
 * Richard Bettridge (ssshake) of TheOldNet
 * http://bit.ly/38ZlPaS
 */
package eu.sblendorio.bbs.tenants.petscii;

import com.linkedin.urls.Url;
import com.linkedin.urls.detection.UrlDetector;
import com.linkedin.urls.detection.UrlDetectorOptions;
import eu.sblendorio.bbs.core.PetsciiKeys;
import eu.sblendorio.bbs.core.PetsciiThread;
import eu.sblendorio.bbs.tenants.CommonConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static eu.sblendorio.bbs.core.PetsciiColors.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

public class InternetBrowserNew extends PetsciiThread {

    public static final String URL_TEMPLATE = "http://www.frogfind.com/read.php?a=";

    protected int __currentPage = 1;
    protected int __pageSize = 10;
    protected int __screenRows = 18;

    Stack<String> history = new Stack<>();

    static class Entry {
        public final String name;
        public final String url;
        public final String fileType;

        public Entry(String url, String name) {
            this.url = Objects.toString(url, "");
            if (name.length() > 60){
                this.name = " ..." + StringUtils.right(name, 31).trim();
            } else {
                this.name = StringUtils.left(name, 35).trim();
            }
            this.fileType = Objects.toString(this.name, "").replaceAll("(?is)^.*\\.(.*?)$", "$1").toLowerCase();
        }
    }

    static class Pager {
        public boolean forward;
        public int page;
        public int currentRow;

        public Pager(boolean forward, int page, int currentRow) {
            this.forward = forward;
            this.page = page;
            this.currentRow = currentRow;
        }
    }


    Map<Integer, Entry> links = emptyMap();

    public static void main(String[] args) throws Exception {}

    public String userAgent = CommonConstants.get("BROWSER_USERAGENT", "");

    public void initScreen() {
        write(PetsciiKeys.CLR, PetsciiKeys.LOWERCASE, PetsciiKeys.CASE_LOCK);
    }

    @Override
    public void doLoop() throws Exception {
        while (true) {
            initScreen();
            writeHeader();
            writeFooter();

            loadWebPage(makeUrl("w3.org"));
            System.out.println("sdfsdXXoweruhioerhgioeuhriogehrigoehru");
            clearBrowserWindow();

            String url = focusAddressBar();
            if ("_quit_program".equals(url)) {
                throw new UnsupportedOperationException();
            }

            loadWebPage(url);
        }
    }

    String makeLinkUrl(String url) throws Exception {
        String finalUrl = Objects.toString(url, "")
                .trim()
                .replaceAll("^/read\\.php\\?a=", "");
        return URL_TEMPLATE + URLEncoder.encode(finalUrl, "UTF-8");
    }

    String makeUrl(String url) throws Exception {
        if (!isUrl(url)) {
            return "http://www.frogfind.com/?q=" + URLEncoder.encode(url, "UTF-8");
        }

        String finalUrl = Objects.toString(url, "").trim().toLowerCase().startsWith("http")
                ? Objects.toString(url, "").trim()
                : "https://" + Objects.toString(url, "").trim();
        return URL_TEMPLATE + URLEncoder.encode(finalUrl, "UTF-8");
    }

    boolean isUrl(String s) {
        UrlDetector parser = new UrlDetector(s, UrlDetectorOptions.Default);
        List<Url> found = parser.detect();
        return found != null && found.size() > 0;
    }

    String focusAddressBar() throws Exception{
        clearAddressBar();
        gotoXY(9,1);
        flush();
        resetInput();
        String search = readLine();

        if (Objects.toString(search, "").trim().equals(".")) {
            return "_quit_program";
        }

        String url = makeUrl(search);
        return url;
    }

    void clearAddressBar(){
        gotoXY(9,1);
        print("                            ");
    }

    void enterAddress(String previousAddress) throws Exception {
        String url = focusAddressBar();
        if (Objects.toString(url, "").equals("_quit_program")) {
            throw new UnsupportedOperationException();
        }
        loadWebPage(url);
        clearBrowserWindow();
        writeAddressBar(previousAddress);
    }

    void push(String url) {
        history.push(url);
    }

    String pop() {
        if (history.size() <= 1) {
            return null;
        }

        history.pop();
        return history.peek();
    }

    void loadWebPage(String url) throws Exception{
        push(url);
        loading();
        clearBrowserWindow();
        Document webpage;
        try {
            webpage = getWebpage(url);
        } catch (HttpStatusException | UnknownHostException ex) {
            ex.printStackTrace();
            webpage = Jsoup.parseBodyFragment("HTTP connection error");
        }
        displayPage(webpage, url);
    }

    protected void displayPage(Document webpage, String address) throws Exception {
        __currentPage = 1; //reset this globally, not sure if required

        Pager pager = new Pager(true, 1, 0);

        final String content = formattedWebpage(webpage);

        writeAddressBar(address);

        List<String> rows = wordWrap(content);

        while (pager.currentRow < rows.size() + 1) {
            logPaging(pager, rows);

            boolean startOfDocument = pager.page <= 1;
            boolean endOfDocument = pager.currentRow == rows.size();

            boolean startOfPage = pager.currentRow % __screenRows == 1;
            boolean endOfPage = pager.currentRow > 0 && pager.currentRow % __screenRows == 0 && pager.forward;

            if (startOfPage){
                printPageNumber(pager.page);
            }

            if (endOfPage || endOfDocument) {
                parkCursor();

                String nextStep = promptForUserInput(pager, webpage, address, startOfDocument, endOfDocument);
                switch (nextStep){
                    case "skip":
                        continue;
                    case "exit":
                        return;
                    default:
                        break;
                }
            }

            if (!endOfDocument){
                printRowWithColor(pager, rows);
                pager.forward = true;
                ++pager.currentRow;
            }
        }
    }

    void logPaging(Pager pager, List<String> rows){
        log("Current Row: " + pager.currentRow);
        log("Rows: " + rows.size());
        log("Page: " + pager.page);
        log("Prior Page Start Row: " + (pager.page - 1) * __screenRows);
    }

    String promptForUserInput(Pager pager, Document webpage, String currentAddress, boolean startOfDocument, boolean endOfDocument) throws Exception {
        String instruction = "";
        int key = getInputKey();
        if (key >= 193 && key <= 218) {
            key -= 96;
        }
        switch(key){
            case 'u' :
            case 'U' :
                enterAddress(currentAddress);
                break;
            case '.': throw new UnsupportedOperationException();
            case 'b':
            case 'B':
                String backPage = pop();
                if (backPage == null)
                    instruction = "skip";
                else {
                    loading();
                    clearBrowserWindow();
                    webpage = getWebpage(backPage);
                    displayPage(webpage, backPage);
                    writeAddressBar(backPage);
                    instruction = "exit";
                }

                break;

            case 'l':
            case 'L':
                listLinksForPage(pager, webpage, currentAddress);
                writeAddressBar(currentAddress);
                break;

            case 'p':
            case 'P':
                if (startOfDocument){
                    instruction = "skip";
                    break;
                }

                loadPreviousPage(pager, currentAddress);
                instruction = "skip";
                break;

            case 'n':
            case 'N':
                if (endOfDocument){
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

    void loadPreviousPage(Pager pager, String head) throws Exception {
        --pager.page;
        pager.currentRow = ( pager.page -1 ) * __screenRows;
        pager.forward = false;
        prepareDisplayForNewPage(head);
    }

    void loadNextPage(Pager pager, String head) throws Exception {
        ++pager.page;
        pager.forward = true;
        prepareDisplayForNewPage(head);
    }

    //why doest this function exist and getLinksForPage
    void listLinksForPage(Pager pager, Document webpage, String currentAddress) throws Exception {
        getAndDisplayLinksOnPage(webpage, currentAddress);
        clearBrowserWindow();
        pager.currentRow = 0;
        pager.page = 0;
    }

    int getInputKey() throws Exception {
        resetInput();
        return readKey();
    }

    void parkCursor(){
        write(BLACK);
        gotoXY(8,1);
        write(GREY3);
    }

    String formattedWebpage(Document webpage){
        String result = webpage
                .toString()
                .replaceAll("<img.[^>]*>", "<br>[IMAGE] ")
                .replaceAll("<a.[^>]*>", " <br>[LINK] ")
                .replaceAll("&quot;", "\"")
                .replaceAll("&apos;", "'")
                .replaceAll("&#xA0;", " ")
                .replaceAll("(?is)<script .*</script>", EMPTY)
                .replaceAll("(?is)^[\\s\\n\\r]+|^\\s*(</?(br|div|figure|iframe|img|p|h[0-9])[^>]*>\\s*)+", EMPTY)
                .replaceAll("(?is)^(<[^>]+>(\\s|\n|\r)*)+", EMPTY);
        return result;
    }

    void printRowWithColor(Pager pager, List<String> rows){
        String row = rows.get(pager.currentRow);
        String patternStringLink = ".*\\[LINK\\].*";
        Pattern patternLink = Pattern.compile(patternStringLink);
        Matcher matcherLink = patternLink.matcher(row);
        boolean matchesLink = matcherLink.matches();

        String patternStringImage = ".*\\[IMAGE\\].*";
        Pattern patternImage = Pattern.compile(patternStringImage);
        Matcher matcherImage = patternImage.matcher(row);
        boolean matchesImage = matcherImage.matches();

        if (matchesLink){
            log("MATCHES!!!!!!!!!!!");
            write(LIGHT_BLUE);
        }

        if (matchesImage){
            log("MATCHES!!!!!!!!!!!");
            write(YELLOW);
        }
        gotoXY(0, pager.currentRow % __screenRows + 3);
        print(row);

        if (matchesLink || matchesImage){
            write(GREY3);
        }
    }

    void printPageNumber(int page){
        write(BLACK);
        gotoXY(1,22);
        write(WHITE);
        print("PAGE " + page);
        write(GREY3);
    }

    void prepareDisplayForNewPage(String head) throws Exception {
        loading();
        clearBrowserWindow();
        writeAddressBar(head);
    }

    void writeAddressBar(String url) throws Exception {

        String tempUrl = URLDecoder.decode(
                Objects.toString(url, "")
                .trim()
                .replaceAll("^http://www\\.frogfind\\.com/read\\.php\\?a=", "")
                , "UTF-8")
                .replaceAll("(?i)^http://", "")
                .replaceAll("(?i)^https://", "")
                .replaceAll("(?i)^http:", "")
                .replaceAll("(?i)^https:", "")
                ;
        clearAddressBar();
        write(GREEN);
        gotoXY(9,1);
        print(StringUtils.left(tempUrl, 28));
        gotoXY(0,3);
        write(GREY3);
    }

    void getAndDisplayLinksOnPage(Document webpage, String currentAddress) throws Exception{
        loading();
        while (true) {

            writeAddressBar(currentAddress);
            listLinks(webpage);
            write(WHITE);
            print("Enter Link # or Command> ");
            write(GREY3);

            resetInput();
            flush();

            String inputRaw = readLine();
            String input = lowerCase(trim(inputRaw));

            //QUIT
            if ("B".equals(input) || "b".equals(input) || ".".equals(input) || "exit".equals(input) || "quit".equals(input) || "q".equals(input)) {
                break;
            }

            //NEXT PAGE
            else if ("n".equals(input) || "N".equals(input)) {
                ++__currentPage;
                links = null;
            }

            //PREVIOUS PAGE
            else if (("p".equals(input) || "P".equals(input))  && __currentPage > 1) {
                --__currentPage;
                links = null;
            }

            //SUCCESS PATH
            //DO THE THING WHERE YOU LOAD A NEW PAGE
            else if (links.containsKey(toInt(input))) {
                final Entry link = links.get(toInt(input));
                loadWebPage(makeLinkUrl(link.url));
            }
        }
    }

    private void listLinks(Document webpage) throws Exception {
        clearForLinks();
        gotoXY(0,4);
        write(ORANGE);
        println("Links On Page:");
        println();

        List<Entry> entries = getAllLinks(webpage);

        if (isEmpty(entries)) {
            write(RED);
            println("Zero result page - press any key");
            flush();
            resetInput();
            readKey();
            return;
        }

        links = getLinksForPage(entries, __currentPage, __pageSize);

        for (Map.Entry<Integer, Entry> entry: links.entrySet()) {
            int i = entry.getKey();
            Entry post = entry.getValue();

            write(WHITE);
            print(i + ".");
            write(GREY3);

            final int iLen = 37-String.valueOf(i).length(); //I'm guessing something to do with the row width

            String title = post.name;
            String line = WordUtils.wrap(filterPrintable(htmlClean(title)), iLen, "\r", true);

            println(line.replaceAll("\r", "\r " + repeat(" ", 37-iLen)));
        }
        newline();
    }

    private Map<Integer, Entry> getLinksForPage(List<Entry> entries, int page, int perPage) throws Exception {
        if (page < 1 || perPage < 1) {
            return null;
        };

        Map<Integer, Entry> result = new LinkedHashMap<>();

        for ( int i = ( page - 1 ) * perPage; i < page * perPage; ++i ){
            if (i<entries.size()) {
                result.put( i + 1, entries.get(i));
            }
        }
        return result;
    }

    public static List<Entry> getAllLinks(Document webpage) throws Exception {
        List<Entry> urls = new ArrayList<>(); //why
        Elements links = webpage.select("a[href]");
        Element link;

        for(int j=0; j < links.size(); j++){
            link=links.get(j);

            String label;
            if (!StringUtils.isBlank(link.text())){
                label = link.text();
            } else {
                try {
                    label = link.attr("href").split("url=")[1];
                } catch (ArrayIndexOutOfBoundsException e){
                    label = link.attr("href");
                }
            }

            urls.add(new Entry(link.attr("href"), label));

        }
        return urls;
    }

    public Document getWebpage(String url) throws Exception {
        Document doc = null;
        try {
            doc = Jsoup
                    .connect(url)
                    .userAgent(userAgent)
                    .get();
        } catch (Exception ex){
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
                .replaceAll("(?s)<a [^>]*nofollow[^>]>\\s*</a>", "")
                ;

        doc = Jsoup.parse(html);

        return doc;
    }

    protected List<String> wordWrap(String s) {
        String[] cleaned = filterPrintableWithNewline(htmlClean(s)).split("\n");
        List<String> result = new ArrayList<>();
        for (String item: cleaned) {
            String[] wrappedLine = WordUtils
                    .wrap(item, 39, "\n", true)
                    .split("\n");
            result.addAll(asList(wrappedLine));
        }
        return result;
    }

    private void loading() {
        gotoXY(9,1);
        write(PURPLE);
        print("LOADING...                  ");
        write(BLACK);
        flush();
    }

    private void clearBrowserWindow(){
        clearForLinks();
        // write(BLACK);
        // gotoXY(0, 3);
        // for (int i=0; i<720; ++i) {
        //         write(PERIOD);
        // }
        // flush();
        // write(GREY3);
    }

    private void clearForLinks(){
        write(BLACK);
        gotoXY(0, 3);
        for (int i=0; i<18; ++i) {
            gotoXY(0, i + 3);
            for (int j=0; j<39; ++j) {
                write(' ');
            }
        }
        flush();
        write(GREY3);
    }

    private void help() throws Exception {
        // writeHeader();
        println();
        println();
        println("Press any key to go back");
        readKey();
    }

    private void writeHeader() throws Exception {
        gotoXY(0,0);
        write(BROWSERTOP);
        write(GREY3);
        gotoXY(0,5);
    }

    private void writeFooter() throws Exception {
        gotoXY(0,21);
        write(BROWSERBOTTOM);
        write(GREY3);
    }

    private final static byte[] BROWSERTOP = {
            -101, 18, 32, 32, 32, 32, 32, 32, 32, 32, -110, -73, -73, -73, -73,
            -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73,
            -73, -73, -73, -73, -73, -73, -73, -73, -73, 18, 32, 32, 13, 18, -104, 32, 91, -43,
            93, -46, -52, 32, 32, 31, -110, 32, 32, 32, 32, 32, 32, 32, 32,
            32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
            32, 5, 32, 32, 32, 32, -104, 18, 32, 32, 13, 18, -105, 32, 32, 32, 32,
            32, 32, 32, 32, -110, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81,
            -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81,
            -81, -81, 18, 32, 32, -102, -110, 32, 32, 32, 32, 32, 32, 32, 32, 32,
            32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
            32, 32, 32, 32, 32, 32,
            13
    };

    private final static byte[] BROWSERBOTTOM = {
            -101, 18, 32, -110, -73, -73, -73, -73, -73, -73, -73, -73, 18, 32, -110,
            -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, 18, 32, -110,
            -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, 18, 32, 13, 18,
            -104, 32, 5, -110, 32, 32, 32, 32, 32, -105, 32, 32, 32, -104, 18,
            32, -110, 91, 5, 80, -104, 93, -101, 82, 69, 86, 5, 32, -104, 91, 5,
            78, -104, 93, -101, 69, 88, 84, -104, 18, 32, -110, 91, 5, 76, -104, 93,
            -101, 73, 78, 75, 83, 32, -104, 91, 5, 66, -104, 93, -101, 65, 67, 75,
            -104, 18, 32, 13, 18, -105, 32, -110, -81, -81, -81, -81, -81, -81, -81, -81, 18,
            32, -110, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, 18,
            32, -110, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81,
            -81, 18, 32, -102, -110
    };

}