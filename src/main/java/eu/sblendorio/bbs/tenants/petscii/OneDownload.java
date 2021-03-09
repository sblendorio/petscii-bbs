package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.HtmlUtils;
import static eu.sblendorio.bbs.core.PetsciiColors.CYAN;
import static eu.sblendorio.bbs.core.PetsciiColors.GREY3;
import static eu.sblendorio.bbs.core.PetsciiColors.LIGHT_GREEN;
import static eu.sblendorio.bbs.core.PetsciiColors.WHITE;
import static eu.sblendorio.bbs.core.PetsciiKeys.CASE_LOCK;
import static eu.sblendorio.bbs.core.PetsciiKeys.DEL;
import static eu.sblendorio.bbs.core.PetsciiKeys.HOME;
import static eu.sblendorio.bbs.core.PetsciiKeys.LOWERCASE;
import static eu.sblendorio.bbs.core.PetsciiKeys.REVOFF;
import static eu.sblendorio.bbs.core.PetsciiKeys.REVON;
import eu.sblendorio.bbs.core.PetsciiThread;
import eu.sblendorio.bbs.core.XModem;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import static java.util.stream.Collectors.toList;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.lang3.math.NumberUtils.toInt;
import org.apache.commons.text.WordUtils;

public class OneDownload extends PetsciiThread {

    public static class DownloadEntry {
        public String title;
        public String url;
        public DownloadEntry(String title, String url) {
            this.title = title;
            this.url = url;
        }
    }

    List<DownloadEntry> entries;
    int currentPage = 0;
    int pageSize = 15;

    private void loadEntries() {
        final String filename = System.getProperty("DOWNLOADMES", "/data/b.txt");
        List<String> secTxt = readTxt(filename);
        entries = secTxt.stream()
            .filter(row -> isNotBlank(trim(row)))
            .filter(row -> row.contains("#"))
            .map(StringUtils::trim)
            .filter(row -> !row.startsWith(";"))
            .map(row -> row.replaceAll("\\s*#\\s*", "#"))
            .map(row -> row.split("#"))
            .map(values -> new DownloadEntry(values[0], values[1]))
            .collect(toList());
    }

    @Override
    public void doLoop() throws Exception {
        loadEntries();
        write(LOWERCASE, CASE_LOCK);
        listPosts();
        while (true) {
            write(WHITE);print("#"); write(GREY3);
            print(" [");
            write(WHITE); print("N+-"); write(GREY3);
            print("]Page [");
            write(WHITE); print("."); write(GREY3);
            print("]");
            write(WHITE); print("Q"); write(GREY3);
            print("uit> ");
            resetInput();
            flush(); String inputRaw = readLine();
            String input = lowerCase(trim(inputRaw));
            if (".".equals(input) || "exit".equals(input) || "quit".equals(input) || "q".equals(input)) {
                break;
            } else if (("+".equals(input) || "n".equals(input)) & (currentPage+1)*pageSize < entries.size())  {
                ++currentPage;
                try {
                    listPosts();
                } catch (NullPointerException e) {
                    --currentPage;
                    listPosts();
                    continue;
                }
                continue;
            } else if ("-".equals(input) && currentPage > 0) {
                --currentPage;
                listPosts();
                continue;
            } else if ("--".equals(input) && currentPage > 0) {
                currentPage = 0;
                listPosts();
                continue;
            } else if ("".equals(input)) {
                listPosts();
                continue;
            } else if (toInt(input.replace("#", "").replace("-", "")) <= entries.size()
                    && toInt(input.replace("#", "").replace("-", "")) > 0) {
                displayPost(toInt(input.replace("#", "")));
                listPosts();
            }
        }
        flush();
    }

    private void displayPost(int i) throws IOException {
        int n = i-1;
        DownloadEntry entry = entries.get(n);
        cls();
        drawLogo();
        waitOn();
        DownloadData file = download(new URL(entry.url));
        waitOff();

        println("Download file:");
        write(WHITE); println(entry.title);
        println();
        write(GREY3); println("URL:");
        write(WHITE); println(entry.url);
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
        int socketTimeout = socket.getSoTimeout();
        socket.setSoTimeout(3600000);
        XModem xm = new XModem(this);
        xm.send(file.getContent());
        socket.setSoTimeout(socketTimeout);
        println();
        write(CYAN);
        print("DONE - press any key to go back ");
        readKey();
        resetInput();

    }

    private void listPosts() {
        cls();
        drawLogo();
        for (int i = currentPage * pageSize; i < (currentPage+1) * pageSize && i < entries.size(); ++i) {
            DownloadEntry entry = entries.get(i);
            write(WHITE); print((i+1)  + "."); write(GREY3);
            final int nCols = getScreenColumns() - 3;
            final int iLen = nCols-String.valueOf(i+1).length();
            String line = WordUtils.wrap(filterPrintable(HtmlUtils.htmlClean(entry.title)), iLen, "\r", true);
            println(line.replaceAll("\r", newlineString() + " " + repeat(" ", nCols-iLen)));
        }
        newline();
    }

    private void drawLogo() {
        cls();
        gotoXY(28,2); write(WHITE); print("Downloads");
        write(HOME); write(LOGO_SECTION);
        write(GREY3);
    }

    public final static byte[] LOGO_SECTION = new byte[] {
        18, 5, 32, -110, 32, 32, 32, 32, 18, -66, 32, -68, -110, 32, 32, 18,
        -98, 32, 32, 32, -68, -110, 13, 18, 5, 32, -110, 32, 32, 32, -84, 18,
        -84, -110, 32, 18, -69, -110, -69, 32, 18, -98, 32, -110, -94, -94, 18, -84,
        -110, 32, 18, -66, 32, 32, -110, -95, 18, -69, -68, -110, 32, 18, 32, -110,
        32, 18, -66, 32, 32, -110, -95, 13, 18, 5, 32, -110, -94, -94, -69, 18,
        -66, 32, 32, 32, -68, -110, 32, 18, -98, 32, -110, -94, -94, 18, 32, -95,
        -68, -110, -94, 18, -66, -110, -95, 32, 18, -69, -68, 32, -95, -68, -110, -94,
        18, -66, -110, -95, 13, 18, 5, -94, -94, -94, -110, -66, 18, -94, -110, 32,
        32, 32, 18, -94, -110, 32, 18, -98, -94, -94, -94, -110, -66, 32, 18, -94,
        -94, -94, -110, -66, 32, -84, -94, 18, -84, -110, 32, 18, -94, -94, -94, -110,
        -66, 13
    };

    private List<String> readTxt(String filename) {
        List<String> result = new LinkedList<>();
        try {
            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                result.add(myReader.nextLine());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return result;
    }

    private void waitOn() {
        print("PLEASE WAIT...");
        flush();
    }

    private void waitOff() {
        for (int i=0; i<14; ++i) write(DEL);
        flush();
    }
}
