/*
 * Credits for this InternetBrowser:
 * Richard Bettridge (ssshake) of TheOldNet
 * http://bit.ly/38ZlPaS
 */
package eu.sblendorio.bbs.tenants;

import eu.sblendorio.bbs.core.HtmlUtils;
import eu.sblendorio.bbs.core.PetsciiThread;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.text.WordUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static eu.sblendorio.bbs.core.Colors.*;
import static eu.sblendorio.bbs.core.Keys.*;
import static eu.sblendorio.bbs.core.Utils.*;
import static java.util.Arrays.asList;
import static eu.sblendorio.bbs.core.Utils.filterPrintable;
import static java.util.Collections.emptyMap;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class InternetBrowser extends PetsciiThread {

    public static final String URL_TEMPLATE = "http://theoldnet.com/get?url=";

    protected int __currentPage = 1;
    protected int __pageSize = 10;
    protected int __screenRows = 16;

    static class Entry {
        public final String name;
        public final String url;
        public final String fileType;

        public Entry(String url, String name) throws Exception {
            this.url = defaultString(url);
            if (name.length() > 60){
                this.name = " ..." + StringUtils.right(name, 31).trim();
            } else {
                this.name = StringUtils.left(name, 35).trim();
            }
            this.fileType = defaultString(this.name).replaceAll("(?is)^.*\\.(.*?)$", "$1").toLowerCase();
        }
    }

    static class Pager {
        public boolean forward;
        public int page;
        public int currentRow;

        public Pager(boolean forward, int page, int currentRow) throws Exception {
            this.forward = forward;
            this.page = page;
            this.currentRow = currentRow;
        }
    }


    protected Map<Integer, Entry> links = emptyMap();

    public static void main(String[] args) throws Exception {}

    @Override
    public void doLoop() throws Exception {
        do {
            write(CLR, LOWERCASE, CASE_LOCK);
            // write(BROWSERSPLASH);
            writeHeader();
            writeFooter();

            loadWebPage(makeUrl("w3c.org"));
            clearBrowserWindow();

            String url = focusAddressBar();

            if (url == "_quit_program"){
                return;
            }

            loadWebPage(url);

        } while (true);
    }

    String makeUrl(String url) {
        if (!defaultString(url).startsWith("http")) return "http://" + defaultString(url);
        return url;
    }

    String focusAddressBar() throws Exception{
        clearAddressBar();
        gotoXY(10,1);
        flush();
        resetInput();
        String search = readLine();

        if (isBlank(search) || search.trim().equals(".")) {
            return "_quit_program";
        }

        String url = makeUrl(search);
        return url;
    }

    void clearAddressBar(){
        gotoXY(10,1);
        print("                            ");
    }

    void enterAddress(String previousAddress) throws Exception {
        String url = focusAddressBar();
        loadWebPage(url);
        clearBrowserWindow();
        writeAddressBar(previousAddress);
    }

    void loadWebPage(String url) throws Exception{
        loading();
        clearBrowserWindow();
        Document webpage = getWebpage(url);
        displayPage(webpage, url);
    }

    protected void displayPage(Document webpage, String url) throws Exception {
        __currentPage = 1; //reset this globally, not sure if required

        Pager pager = new Pager(true, 1, 0);

        final String content = formattedWebpage(webpage);

        writeAddressBar(url);

        List<String> rows = wordWrap("");
        rows.addAll(wordWrap(content));

        while (pager.currentRow < rows.size() + 1) {
            logPaging(pager, rows);

            boolean startOfDocument = pager.page <= 1;
            boolean endOfDocument = pager.currentRow == rows.size();

            boolean startOfPage = pager.currentRow % __screenRows == 1;
            boolean endOfPage = pager.currentRow > 0 && pager.currentRow % __screenRows == 0 && pager.forward;

            if (startOfPage){
                printPageNumber(pager.page);
                gotoXY(0, pager.currentRow % __screenRows + 3);
            }

            if (endOfPage || endOfDocument) {
                parkCursor();

                String nextStep = promptForUserInput(pager, webpage, url, startOfDocument, endOfDocument);
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
        log("Current Row: " + Integer.toString(pager.currentRow));
        log("Rows: " + Integer.toString(rows.size()));
        log("Page: " + Integer.toString(pager.page));
        log("Prior Page Start Row: " + Integer.toString((pager.page - 1 )* __screenRows));
    }

    String promptForUserInput(Pager pager, Document webpage, String currentAddress, boolean startOfDocument, boolean endOfDocument) throws Exception {
        String instruction = "";
        switch(getInputKey()){
            case 'u' :
            case 'U' :
                enterAddress(currentAddress);
                break;
            case '.':
            case 'b':
            case 'B':
                instruction = "exit";
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

    void loadPreviousPage(Pager pager, String head){
        --pager.page;
        pager.currentRow = ( pager.page -1 ) * __screenRows;
        pager.forward = false;
        prepareDisplayForNewPage(head);
    }

    void loadNextPage(Pager pager, String head){
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
        gotoXY(9,1);
        write(GREY3);
    }

    String formattedWebpage(Document webpage){
        log("WPA=" + (webpage==null ? "": webpage.toString()));
        final String result = webpage == null ? "" :webpage
                .toString()
                .replaceAll("<img [^>]*>", "<br>[IMAGE] ")
                .replaceAll("<a [^>]*>", " <br>[LINK] ")
                .replaceAll("&quot;", "\"")
                .replaceAll("&apos;", "'")
                .replaceAll("&#xA0;", " ")
                .replaceAll("(?is)<style(\\s|>).*?</style>", EMPTY)
                .replaceAll("(?is)<script(\\s|>).*?</script>", EMPTY)
                .replaceAll("(?is)^[\\s\\n\\r]+|^\\s*(</?(br|div|figure|iframe|img|p|h[0-9])[^>]*>\\s*)+", EMPTY)
                .replaceAll("(?is)^(<[^>]+>(\\s|\n|\r)*)+", EMPTY);
        //log("wpage="+result);
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
            write(LIGHT_BLUE);
        }

        if (matchesImage){
            write(YELLOW);
        }

//        gotoXY(0, pager.currentRow % __screenRows + 3);
        println(row);

        System.out.println("> " + row);

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

    void prepareDisplayForNewPage(String head){
        loading();
        clearBrowserWindow();
        writeAddressBar(head);
    }

    void writeAddressBar(String url){
        clearAddressBar();
        write(GREEN);
        gotoXY(10,1);
        print(StringUtils.left(url, 28));
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
                loadWebPage(link.url);
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
            String line = WordUtils.wrap(filterPrintable(HtmlUtils.htmlClean(title)), iLen, "\r", true);

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
        String title = webpage.title();
        Elements links = webpage.select("a[href]");
        Element link;

        for(int j=0; j < links.size(); j++){
            link=links.get(j);

            String label = "Empty";
            if (StringUtils.isBlank(link.text())){
                label = link.attr("href");
            } else {
                label = link.text();
            }

            urls.add(new Entry(link.absUrl("href"), label));

        }
        return urls;
    }

    public static Document getWebpage(String url) throws Exception {
        Connection conn;
        try {
            conn = Jsoup.connect(url);
        } catch (Exception e1) {
            System.out.println("Couldn't connect with the website.");
            return null;
        }
        return conn
                //.header("HTTP-User-Agent", "")
                .get();
    }

    protected List<String> wordWrap(String s) {
        String[] cleaned = filterPrintableWithNewline(HtmlUtils.htmlClean(s)).split("\n");
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
        gotoXY(10,1);
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
            //gotoXY(0, i + 3);
            for (int j=0; j<39; ++j) {
                write(SPACE_CHAR);
            }
            println();
        }
        flush();
        write(GREY3);
    }

    private void waitOff() {
        for (int i=0; i<10; ++i) {
            write(DEL);
        }
        flush();
    }

    private void help() throws Exception {
        // writeHeader();
        println();
        println();
        println("Press any key to go back");
        readKey();
    }

    private void writeHeader() throws Exception {
        // write(TheOldNet.LOGO);
        // write(LOGO);
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

    private final static byte[] LOGO = {
            -102, 32, 18, 32, 30, 32, -104, -110, 32, 32, 18, 32, -102, -110, 32, -104,
            32, 32, 18, 32, -110, 32, 18, 32, -110, 32, 32, 32, 18, 32, -110, 32,
            18, 32, -110, 32, 32, 32, 18, 32, -110, 32, 32, 32, 32, 32, -102, 32,
            32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 18, 32, 30, 32,
            32, -102, 32, -104, -110, 32, 18, 32, -110, 32, 18, 32, -110, 32, 18, 32,
            -110, 32, 18, 32, -110, 32, 18, 32, -110, 32, 18, 32, -110, 32, 18, 32,
            -110, 32, 18, 32, -110, 32, 18, 32, -110, 32, 32, 32, -102, 32, 32, 32,
            32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 18, 32, 30, 32,
            -102, 32, 32, -104, -110, 32, 32, 18, 32, -110, 32, 18, 32, -110, 32, 32,
            32, 18, 32, -110, 32, 18, 32, -110, 32, 32, 32, 18, 32, -110, 32, 18,
            32, -110, 32, 32, 32, 32, -102, 32, 32, 32, 32, 32, 32, 32, 32, 32,
            32, 32, 32, 32, 32, 32, -104, 32, -102, 18, 32, 30, 32, -104, -110, 32,
            32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
            32, 32, 32, 32, 32, 32, 32, -102, 32, 32, 32, 32, 32, 32, 32, 32,
            32, 32, 32, 32, 32, -104, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
            32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
            32, 32, -102, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, -104,
            32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
            32, 32, 32, 32, 32, 32, 32, 32, 32, -102, 32, -104, 32, 32, -102, 32,
            32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
            32, 32, -104, 32, 32, 32, 32, -102, 32, 32, 32, 32, 32, 32, 32, 32,
            32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
            32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, -104, 32, -102,
            32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
            32, 32, 32, 32, 32, 32, 32,
            13
    };

    private final static byte[] BROWSERTOP = {
            -101, 18, 32, 32, 32, 32, 32, 32, 32, 32, 32, -110, -73, -73, -73, -73,
            -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73,
            -73, -73, -73, -73, -73, -73, -73, -73, -73, 18, 32, 32, -104, 32, 91, -43,
            93, -46, -52, 32, 32, 32, 31, -110, 32, 32, 32, 32, 32, 32, 32, 32,
            32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
            32, 5, 32, 32, 32, 32, -104, 18, 32, 32, -105, 32, 32, 32, 32, 32,
            32, 32, 32, 32, -110, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81,
            -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81,
            -81, -81, 18, 32, 32, -102, -110, 32, 32, 32, 32, 32, 32, 32, 32, 32,
            32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,

            32, 32, 32, 32, 32, 32, 32,

            13
    };

    private final static byte[] BROWSERBOTTOM = {
            -101, 18, 32, -110, -73, -73, -73, -73, -73, -73, -73, -73, -73, 18, 32, -110,
            -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, 18, 32, -110,
            -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, -73, 18, 32,
            -104, 32, 5, -110, 32, 32, 32, 32, 32, 32, -105, 32, 32, 32, -104, 18,
            32, -110, 91, 5, 80, -104, 93, -101, 82, 69, 86, 5, 32, -104, 91, 5,
            78, -104, 93, -101, 69, 88, 84, -104, 18, 32, -110, 91, 5, 76, -104, 93,
            -101, 73, 78, 75, 83, 32, -104, 91, 5, 66, -104, 93, -101, 65, 67, 75,
            -104, 18, 32, -105, 32, -110, -81, -81, -81, -81, -81, -81, -81, -81, -81, 18,
            32, -110, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, 18,
            32, -110, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81, -81,
            18, 32, -102, -110, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
            32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,

            32, 32, 32, 32
    };

    private final static byte[] BROWSERSPLASH = {
            -102, -115, -115, -115, -115, -115, -115, -115, 18, -110, 32, 18, -110, 32, 18, -110,
            32, 18, -110, 32, 18, -110, 32, 18, -110, 32, 18, -110, 32, 18, -110, 32,
            18, -110, 32, 18, -110, 32, 18, -110, 32, 18, -110, 32, 18, 32, 32, 32,
            32, 5, -110, -76, -102, 18, 32, 32, 32, 5, -110, -76, -98, 18, 32, 32,
            32, 5, -110, -76, -98, 18, 32, 5, -110, -76, -98, 18, -110, 32, 18, 32,
            32, 5, -110, -76, -102, -115, 32, 32, 32, 32, 32, 32, 32, -94, 18, 32,
            30, 32, -102, -110, -94, 18, -110, 32, 18, -110, 32, 18, 32, 5, -110, -76,
            -102, 18, 32, 5, -110, -76, -102, 18, 32, -110, -98, 18, -110, 32, 18, -110,
            32, 18, -110, 32, 18, 32, 5, -110, -76, -98, 18, 32, 5, -110, -76, -98,
            18, 32, 5, -110, -76, -98, 18, -110, 32, 18, 32, 5, -110, -76, -98, 18,
            32, 5, -110, -76, -102, -115, 18, -110, 32, 18, -110, 32, 18, -110, 32, 18,
            -110, 32, 18, -110, 32, 18, -110, 32, 18, -95, 32, 30, 32, 32, -102, 32,
            -110, -95, 18, -110, 32, 18, 32, 5, -110, -76, -102, 18, 32, 32, 32, -110,
            -72, -98, 18, -110, 32, 18, -110, 32, 18, 32, 5, -110, -76, -98, 18, 32,
            5, -110, -76, -98, 18, 32, 5, -110, -76, -98, 18, -110, 32, 18, 32, 5,
            -110, -76, -98, 18, 32, 5, -110, -76, -102, -115, 18, -110, 32, 18, -110, 32,
            18, -110, 32, 18, -110, 32, 18, -110, 32, 18, -110, 32, 18, 32, 30, 32,
            32, 32, 32, -102, 32, -110, 18, -110, 32, 18, 32, 5, -110, -76, -102, 18,
            32, 5, -110, -76, -102, 18, 32, 32, 32, 5, -110, -76, -98, 18, 32, 32,
            32, 5, -110, -76, -98, 18, 32, 32, 5, -110, -76, -98, 18, 32, 32, 5,
            -110, -76, -102, -115, 18, -110, 32, 18, -110, 32, 18, -110, 32, 18, -110, 32,
            18, -110, 32, 18, -110, 32, 18, 32, 32, 30, 32, -102, 32, 32, 32, -110,
            -115, 18, -110, 32, 18, -110, 32, 18, -110, 32, 18, -110, 32, 18, -110, 32,
            18, -110, 32, 18, -95, 32, 32, 30, 32, 32, -102, -110, -95, 28, 18, -110,
            32, 18, -110, 32, 18, 32, 5, -110, -76, 28, 18, -110, 32, 18, 32, 5,
            -110, -76, 28, 18, 32, 32, 32, 5, -110, -76, 28, 18, 32, 32, 32, 32,
            5, -110, -76, -102, -115, 18, -110, 32, 18, -110, 32, 18, -110, 32, 18, -110,
            32, 18, -110, 32, 18, -110, 32, 18, -110, 32, 18, -94, 32, 30, 32, -102,
            -94, -110, 28, 18, -110, 32, 18, -110, 32, 18, -110, 32, 18, 32, 32, 5,
            -110, -76, 28, 18, 32, 5, -110, -76, 28, 18, 32, -102, -110, 28, 18, -110,
            32, 18, -110, 32, 18, -110, 32, 18, -110, 32, 18, 32, 5, -110, -76, -115,
            28, 18, -110, 32, 18, -110, 32, 18, -110, 32, 18, -110, 32, 18, -110, 32,
            18, -110, 32, 18, -110, 32, 18, -110, 32, 18, -110, 32, 18, -110, 32, 18,
            -110, 32, 18, -110, 32, 18, -110, 32, 18, -110, 32, 18, 32, 5, -110, -76,
            28, 18, 32, 32, 5, -110, -76, 28, 18, 32, -110, -72, 18, -110, 32, 18,
            -110, 32, 18, -110, 32, 18, 32, 5, -110, -76, -115, 28, 18, -110, 32, 18,
            -110, 32, 18, -110, 32, 18, -110, 32, 18, -110, 32, 18, -110, 32, 18, -110,
            32, 18, -110, 32, 18, -110, 32, 18, -110, 32, 18, -110, 32, 18, -110, 32,
            18, -110, 32, 18, -110, 32, 18, 32, 5, -110, -76, 28, 18, -110, 32, 18,
            32, 5, -110, -76, 28, 18, 32, 32, 32, 5, -110, -76, 28, 18, -110, 32,
            18, 32, 5, -110, -76, 30, 46, 67, 79, 77, -102, -115, -115, -115, -115, -115,
            -115, -115, -115, -115,

            13
    };
}