
package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.core.HtmlUtils;
import static eu.sblendorio.bbs.core.PetsciiColors.CYAN;
import static eu.sblendorio.bbs.core.PetsciiColors.GREY1;
import static eu.sblendorio.bbs.core.PetsciiColors.GREY3;
import static eu.sblendorio.bbs.core.PetsciiColors.LIGHT_GREEN;
import static eu.sblendorio.bbs.core.PetsciiColors.RED;
import static eu.sblendorio.bbs.core.PetsciiColors.WHITE;
import static eu.sblendorio.bbs.core.PetsciiKeys.CASE_LOCK;
import static eu.sblendorio.bbs.core.PetsciiKeys.CLR;
import static eu.sblendorio.bbs.core.PetsciiKeys.DEL;
import static eu.sblendorio.bbs.core.PetsciiKeys.LOWERCASE;
import static eu.sblendorio.bbs.core.PetsciiKeys.REVOFF;
import static eu.sblendorio.bbs.core.PetsciiKeys.REVON;
import static eu.sblendorio.bbs.core.PetsciiKeys.UP;
import eu.sblendorio.bbs.core.XModem;
import eu.sblendorio.bbs.core.PetsciiThread;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import static java.util.Collections.emptyMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import net.sourceforge.droid64.addons.DiskUtilities;
import net.sourceforge.droid64.d64.CbmException;
import static org.apache.commons.codec.CharEncoding.UTF_8;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.apache.commons.lang3.StringUtils.trim;
import org.apache.commons.lang3.math.NumberUtils;
import static org.apache.commons.lang3.math.NumberUtils.toInt;
import org.apache.commons.text.WordUtils;

@Hidden
public class ArnoldC64 extends PetsciiThread {

    private static final BiPredicate<Integer, Integer> generateOverflow = (i, j) -> (i-1) * j < 0 || i * j < 0;
    private static final BiPredicate<Integer, Integer> negativePageOrPerPage = (i, j) -> i < 1 || j < 1;

    public static final String URL_TEMPLATE = "https://cbm8bit.com/search-embedded?servers%5B1%5D=on&width=900&results_per_page=100&embedder=arnold&query=";

    protected int currentPage = 1;
    protected int pageSize = 10;

    static class Entry {
        public final String name;
        public final String url;
        public final String fileType;

        public Entry(String url) throws UnsupportedEncodingException {
            this.url = defaultString(url);
            this.name = URLDecoder.decode(defaultString(this.url).replaceAll("(?is)^.*/([^/]*?)$", "$1"), UTF_8);
            this.fileType = defaultString(this.name).replaceAll("(?is)^.*\\.(.*?)$", "$1").toLowerCase();
        }
    }

    protected Map<Integer, Entry> posts = emptyMap();

    public ArnoldC64() {
    }

    @Override
    public void doLoop() throws Exception {
        do {
            currentPage = 1;
            drawLogo();
            println();
            print("Enter search criteria ");
            write(GREY1);
            println("(\".\" to go back):");
            write(GREY3);
            println();
            println(StringUtils.repeat(chr(163), 21));
            write(UP, UP);
            flush();
            resetInput();
            String search = readLine();
            if (defaultString(search).trim().equals(".") || isBlank(search))
                return;
            println();
            println();
            waitOn();
            List<Entry> entries = getUrls(URL_TEMPLATE + URLEncoder.encode(search, UTF_8));
            waitOff();
            if (isEmpty(entries)) {
                write(RED); println("Zero result page - press any key");
                flush(); resetInput(); readKey();
                continue;
            }
            displaySearchResults(entries);
        } while (true);
    }
    private void drawLogo() {
        write(CLR, LOWERCASE, CASE_LOCK);
        write(LOGO_BYTES);
        write(GREY3); gotoXY(0,5);
    }

    public void displaySearchResults(List<Entry> entries) throws IOException, CbmException {
        listPosts(entries);
        while (true) {
            log("ArnoldC64 waiting for input");
            write(WHITE);print("#"); write(GREY3);
            print(" [");
            write(WHITE); print("N+-"); write(GREY3);
            print("]Page [");
            write(WHITE); print("H"); write(GREY3);
            print("]elp [");
            write(WHITE); print("R"); write(GREY3);
            print("]eload [");
            write(WHITE); print("."); write(GREY3);
            print("]");
            write(WHITE); print("Q"); write(GREY3);
            print("uit> ");
            resetInput();
            flush(); String inputRaw = readLine();
            String input = lowerCase(trim(inputRaw));
            if (".".equals(input) || "exit".equals(input) || "quit".equals(input) || "q".equals(input)) {
                break;
            } else if ("help".equals(input) || "h".equals(input)) {
                help();
                listPosts(entries);
            } else if ("+".equals(input) || "n".equals(input) || "n+".equals(input)) {
                ++currentPage;
                posts = null;
                try {
                    listPosts(entries);
                } catch (NullPointerException e) {
                    --currentPage;
                    posts = null;
                    listPosts(entries);
                }
            } else if (("-".equals(input) || "n-".equals(input)) && currentPage > 1) {
                --currentPage;
                posts = null;
                listPosts(entries);
            } else if ("--".equals(input) && currentPage > 1) {
                currentPage = 1;
                posts = null;
                listPosts(entries);
            } else if ("r".equals(input) || "reload".equals(input) || "refresh".equals(input)) {
                posts = null;
                listPosts(entries);
            } else if (posts.containsKey(toInt(input.replace("#", "")))) {
                displayPost(toInt(input.replace("#", "")));
                listPosts(entries);
            } else if ("".equals(input)) {
                listPosts(entries);
            }
        }
        flush();
    }

    private void displayPost(int n) throws IOException, CbmException {
        cls();
        drawLogo();

        waitOn();
        final Entry p = posts.get(n);
        final String url = p.url;
        final String title = p.name;
        byte[] content = DiskUtilities.getPrgContentFromUrl(url);
        waitOff();

        write(GREY3);
        println("Title:");
        write(WHITE);
        println(title.replaceAll("(?is)\\.[a-z0-9]+(\\.gz)?$", EMPTY));
        println();
        if (content == null) {
            log("Can't download " + url);
            write(RED, REVON); println("      ");
            write(RED, REVON); print(" WARN "); write(WHITE, REVOFF); println(" Can't handle this. Use browser.");
            write(RED, REVON); println("      "); write(WHITE, REVOFF);
            write(CYAN); println();
            print("SORRY - press any key to go back ");
            readKey();
            resetInput();
        } else {
            write(GREY3);
            println("Size:");
            write(WHITE);
            final int blocks = (content.length + 255) / 256;
            println(content.length + " bytes (" + blocks + " blocks)");
            println();
            write(GREY3);
            println("Press any key to prepare to download");
            println("Or press \".\" to abort it");
            resetInput();
            int ch = readKey();
            if (ch == '.') return;
            println();
            write(REVON, LIGHT_GREEN);
            write(REVON); println("                              ");
            write(REVON); println(" Please start XMODEM transfer ");
            write(REVON); println("                              ");
            write(REVOFF, WHITE);
            log("Downloading " + url);
            int socketTimeout = socket.getSoTimeout();
            socket.setSoTimeout(3600000);
            XModem xm = new XModem(this);
            socket.setSoTimeout(socketTimeout);
            xm.send(content);
            println();
            write(CYAN);
            print("DONE - press any key to go back ");
            readKey();
            resetInput();
        }
    }

    private void listPosts(List<Entry> entries) {
        drawLogo();
        posts = getPosts(entries, currentPage, pageSize);
        for (Map.Entry<Integer, Entry> entry: posts.entrySet()) {
            int i = entry.getKey();
            Entry post = entry.getValue();
            write(WHITE); print(i + "."); write(GREY3);
            final int iLen = 37-String.valueOf(i).length();
            String title = post.name;
            String line = WordUtils.wrap(filterPrintable(htmlClean(title)), iLen, "\r", true);
            println(line.replaceAll("\r", "\r " + repeat(" ", 37-iLen)));
        }
        newline();
    }

    static Map<Integer, Entry> getPosts(List<Entry> entries, int page, int perPage) {
        Objects.requireNonNull(entries);
        if(negativePageOrPerPage.test(page, perPage)) {
            return Collections.emptyMap();
        }

        if(generateOverflow.test(page, perPage)) {
            return Collections.emptyMap();
        }

        return IntStream.rangeClosed((page - 1) * perPage, page * perPage)
                .filter(i -> i >= 0 && i < entries.size())
                .boxed()
                .collect(LinkedHashMap::new, (m, i) -> m.put(i + 1, entries.get(i)), LinkedHashMap::putAll);
    }


    public static List<Entry> getUrls(String url) throws IOException {
        String output = httpGet(url);
        Pattern p = Pattern.compile("(?is)href=\"(ftp://[^\"]+\\.(p00|prg|d64|zip|t64|d71|d81|d82|d64\\.gz|t64\\.gz|d81\\.gz|d82\\.gz|d71\\.gz))\"");
        Matcher m = p.matcher(output);
        List<Entry> urls = new ArrayList<>();
        while (m.find()) urls.add(new Entry(m.group(1)));

        String latestPageStr = output.replaceAll("(?is)^.*href=\".*?embedder=arnold&amp;page=([0-9]+)\">[0-9]+</a>\\s*<div class=\"footer\".*?$", "$1");
        int latestPage = NumberUtils.toInt(latestPageStr.replaceAll("\\s|\n|\r",""));

        if (latestPage > 0) {
            for (int i=1; i<=latestPage; ++i) {
                output = httpGet(url + "&page=" + i);
                m = p.matcher(output);
                while (m.find()) urls.add(new Entry(m.group(1)));
            }
        }
        return urls;
    }

    private void waitOn() {
        print("PLEASE WAIT...");
        flush();
    }

    private void waitOff() {
        for (int i=0; i<14; ++i) write(DEL);
        flush();
    }

    private void help() throws IOException {
        drawLogo();
        println();
        println();
        println("Press any key to go back");
        readKey();
    }

    private static final byte[] LOGO_BYTES = {
            32, 32, 28, -95, 32, 32, -41, 69, 76, 67, 79, 77, 69, 32, 84, 79,
            32, -95, 32, 32, 32, 18, -95, -110, 13, 32, 18, -95, -95, -65, -110, 32,
            -69, -94, 32, -84, 32, -84, 32, -84, -69, 32, -84, -66, 32, -84, -69, -95,
            32, 32, 32, (byte) CYAN, -56, -49, -51, -59, -96, -49, -58, -96, -44, -56, -59, 13,
            32, 18, 28, -68, -65, -110, -69, 32, 18, -84, -110, 32, 32, -95, -95, 18,
            -65, -110, -84, -66, 18, -95, -110, 32, 18, -65, -110, 32, -84, -66, 18, -95,
            -110, 32, (byte) CYAN, -61, -49, -51, -51, -49, -60, -49, -46, -59, 32, 54, 52, 32,
            -57, -63, -51, -59, -45, 13, 18, 28, -95, -110, 32, 32, -95, 18, -95, -110,
            32, 32, 18, -95, -110, 32, -65, -66, -68, -94, -66, 32, -65, -66, -68, -94,
            -66, 18, -68, -110, 13
    };
}
