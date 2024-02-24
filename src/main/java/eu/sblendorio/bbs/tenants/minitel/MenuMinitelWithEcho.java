package eu.sblendorio.bbs.tenants.minitel;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import eu.sblendorio.bbs.core.*;
import eu.sblendorio.bbs.tenants.ascii.*;
import eu.sblendorio.bbs.tenants.mixed.HolidayCommons;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static eu.sblendorio.bbs.core.MinitelControls.*;
import static eu.sblendorio.bbs.core.Utils.*;
import static eu.sblendorio.bbs.tenants.mixed.GeolocationCommons.isItaly;
import static eu.sblendorio.bbs.tenants.mixed.GeolocationCommons.isLocalhost;
import static eu.sblendorio.bbs.tenants.mixed.HolidayCommons.isSanremo;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public class MenuMinitelWithEcho extends MinitelThread {

    private byte[] clsBytes = new byte[] { 12 };

    public MenuMinitelWithEcho() {
        super();
        setLocalEcho(true);
    }

    public byte[] initializingBytes() {
        return bytes(CAPSLOCK_OFF, SCROLL_ON, CURSOR_ON);
    }

    public String rssPropertyTimeout() { return "rss.a1.timeout"; }

    public String rssPropertyTimeoutDefault() { return "40000"; }


    @Override
    public void doLoop() throws Exception {
        resetInput();
        logo();
        while (true) {
            log("Starting Minitel / main menu");
            cls();
            displayMenu();

            flush();
            boolean validKey;
            do {
                validKey = true;
                log("Menu. Waiting for key pressed.");
                resetInput();
                String choice;
                int key = readSingleKey();
                choice = String.valueOf((char) key);
                resetInput();
                choice = StringUtils.lowerCase(choice);
                log("Menu. Choice = "+ choice);
                BbsThread subThread;
                if (".".equals(choice)) {
                    cls();
                    println("Goodbye! Come back soon!");
                    println();
                    println("* Disconnected");
                    return;
                }
                else if ("1".equals(choice)) subThread = new CnnAscii(
                        io,
                        rssPropertyTimeout(),
                        rssPropertyTimeoutDefault(),
                        getTerminalType(),
                        bytes(0x1b, 0x3a, 0x6a, 0x43, 0x1e, readBinaryFile("minitel/cnn_home.vdt"), 17),
                        bytes(31, 64+15, 64+2, 0x1b, 0x54, 0x1b, 0x47, 0x1b, 0x5c, 32, 32, 32, 32, 32, 32, 31, 64+15, 64+2 ,0x1b, 0x54, 0x1b, 0x47)
                );
                else if ("2".equals(choice)) subThread = new BbcAscii(
                        io,
                        rssPropertyTimeout(),
                        rssPropertyTimeoutDefault(),
                        getTerminalType(),
                        bytes(0x1b, 0x3a, 0x6a, 0x43, 0x1e, readBinaryFile("minitel/bbc_home.vdt"), 17),
                        bytes(31, 64+22, 64+2, 0x1b, 0x54, 0x1b, 0x47, 0x1b, 0x5c, 32, 32, 32, 32, 32, 32, 31, 64+22, 64+2 ,0x1b, 0x54, 0x1b, 0x47)
                );
                else if ("3".equals(choice)) subThread = new OneRssPoliticoAscii();
                else if ("4".equals(choice)) subThread = new OneRssAJPlusAscii();
                else if ("5".equals(choice)) subThread = new OneRssFoxNewsAscii();
                else if ("6".equals(choice)) subThread = new IndieRetroNewsAscii();
                else if ("7".equals(choice)) subThread = new VcfedAscii();
                else if ("8".equals(choice)) subThread = new The8BitGuyAscii();
                else if ("a".equals(choice)) subThread = new TelevideoRaiAscii(
                        io,
                        rssPropertyTimeout(),
                        rssPropertyTimeoutDefault(),
                        getTerminalType(),
                        readBinaryFile("minitel/menu-televideo.vdt"),
                        bytes(31, 64+23, 64+1, 32, 32, 32, 32, 32, 32, 31, 64+23, 64+1)
                );
                else if ("b".equals(choice)) subThread = new LercioAscii();
                else if ("c".equals(choice)) subThread = new DisinformaticoAscii();
                else if ("d".equals(choice)) subThread = new MupinAscii();
                else if ("e".equals(choice)) subThread = new IlFattoQuotidianoAscii();
                else if ("f".equals(choice)) subThread = new AmedeoValorosoAscii();
                else if ("z".equals(choice)) subThread = new OneRssAmedeoValorosoEngAscii();
                else if ("g".equals(choice)) subThread = new ButacAscii();
                else if ("h".equals(choice)) subThread = new AlessandroAlbanoAscii();
                else if ("i".equals(choice)) subThread = new TicTacToeAscii();
                else if ("j".equals(choice)) subThread = new Connect4Ascii();
                else if ("k".equals(choice)) subThread = new ZorkMachineMinitel("zmpp/zork1.z3");
                else if ("l".equals(choice)) subThread = new ZorkMachineMinitel("zmpp/Zork-1-ITA-v7.z5");
                else if ("m".equals(choice)) subThread = new ZorkMachineMinitel("zmpp/zork2.z3");
                else if ("n".equals(choice)) subThread = new ZorkMachineMinitel("zmpp/zork3.z3");
                else if ("o".equals(choice)) subThread = new ZorkMachineMinitel("zmpp/hitchhiker-r60.z3", readBinaryFile("minitel/hitchhikers.vdt"));
                else if ("p".equals(choice)) subThread = new ZorkMachineMinitel("zmpp/planetfall-r39.z3", readBinaryFile("minitel/planetfall.vdt"));
                else if ("q".equals(choice)) subThread = new ChatA1(io, getTerminalType());
                else if ("r".equals(choice)) subThread = new PrivateMessagesAscii(io);
                else if ("s".equals(choice)) subThread = new ElizaAscii(io);
                else if ("t".equals(choice)) subThread = new ClientChatGptAscii(io, readBinaryFile("minitel/chatgpt-mainlogo.vdt"));
                else if ("u".equals(choice)) { showPatrons(); subThread = null; }
                else if ("v".equals(choice)) { patronsPublishers(); subThread = null; }
                //else if ("u".equals(choice)) { wifiModem(); subThread = null; }
                else if ("w".equals(choice)) subThread = new WikipediaMinitel();
                else if ("x".equals(choice)) { videotelVault(); subThread = null; }
                else if ("y".equals(choice)) { textDemo(); subThread = null; }
                else if (isSanremo() && "9".equals(choice)) subThread = new SanremoAscii(io);
                else if ("*".equals(choice)) subThread = new TestClientVideotex();
                else {
                    validKey = false;
                    subThread = null;
                }
                if (subThread == null) continue;

                if (subThread instanceof AsciiThread) {
                    ((AsciiThread) subThread).clsBytes = this.clsBytes;
                    ((AsciiThread) subThread).screenColumns = this.getScreenColumns();
                    ((AsciiThread) subThread).screenRows = this.getScreenRows();
                }
                launch(subThread);
            } while (!validKey);
        }
    }

    public void logo() throws Exception {
        write(CURSOR_OFF);
        write(SCROLL_OFF);

        if (
                HolidayCommons.isAscanioDay() && (isItaly(ipAddress.getHostAddress()) || isLocalhost(ipAddress.getHostAddress()))
        ) {
            write(readBinaryFile("minitel/ascanio.vdt"));
            String[] matrix = BlockGraphicsMinitel.stringToQr("t.ly/yjubs", ErrorCorrectionLevel.L);
            int len = matrix.length+1;
            List<String> listMatrix = new ArrayList<>();
            listMatrix.add(StringUtils.repeat('.', matrix[0].length()+1));
            for (String line: matrix) {
                listMatrix.add("." + line);
            }
            String[] strMatrix = listMatrix.toArray(new String[len]);
            gotoXY(0,0);
            write(MinitelControls.GRAPHICS_MODE);
            write(BlockGraphicsMinitel.getRenderedMidres(29, strMatrix, false, true));
            write(TEXT_MODE);
        } else if (HolidayCommons.isXmasTime()) {
            write(readBinaryFile("minitel/santaclaus.vdt"));
            gotoXY(28, 18);
            write(TEXT_MODE);
            attributes(TEXTSIZE_DOUBLE_ALL, CHAR_WHITE);
            print(String.valueOf(HolidayCommons.xmasNewYear()));
        } else {
            write(readBinaryFile("minitel/intro-retrocampus.vdt"));
        }

        write(SCROLL_ON);
        flush(); resetInput();
        keyPressed(60_000);
        write(CURSOR_ON);
    }

    public String getTerminalType() {
        return "minitel";
    }

    protected void banner() {
        write(readBinaryFile("minitel/retrocampus-logo.vdt"));
    }

    public void displayMenu() throws Exception {
        write(SCROLL_OFF);
        if (isSanremo()) {
            write(readBinaryFile("minitel/menu-retrocampus-sanremo.vdt"));
        } else {
            write(readBinaryFile("minitel/menu-retrocampus.vdt"));
        }
        write(SCROLL_ON);
        flush(); resetInput();
    }

    public int readSingleKey() throws IOException {
        write(CURSOR_OFF);
        int ch = readKey();
        write(CURSOR_ON);
        return ch;
    }

    @Override
    public void initBbs() throws Exception {
        Thread.sleep(2300);
        resetInput();
    }


    public void showPatrons() throws Exception {
        List<String> patrons = readExternalTxt(System.getProperty("PATREON_LIST", System.getProperty("user.home") + File.separator + "patreon_list.txt"))
                .stream()
                .map(StringUtils::trim)
                .filter(StringUtils::isNotBlank)
                .filter(str -> !str.startsWith(";"))
                .sorted(comparing(String::toLowerCase))
                .collect(toList());

        cls();
        banner();
        println("You can support the development of this");
        println("BBS through Patreon starting with 3$ or");
        println("3.50eur per month:");
        println();
        println("https://patreon.com/FrancescoSblendorio");
        println();
        println("Patrons of this BBS");
        println("-------------------");

        final int PAGESIZE = getScreenRows()-2;
        int pages = patrons.size() / PAGESIZE + (patrons.size() % PAGESIZE == 0 ? 0 : 1);
        for (int p = 0; p < pages; ++p) {
            for (int i=0; i<PAGESIZE; ++i) {
                int index = (p*PAGESIZE + i);
                if (index < patrons.size())
                    println(patrons.get(index));
            }
            flush(); resetInput(); int ch = readKey();
            if (ch == '.' || ch == 27) return;
            println();
        }
        println();
        print("Press any key.");
        flush(); resetInput(); readKey();
    }

    public void textDemo() throws Exception {
        List<String> drawings = Utils.getDirContent("apple1/demo30th")
                .stream()
                .map(Path::toString)
                .map(x -> x.startsWith("/") ? x.substring(1) : x)
                .sorted(comparing(String::toLowerCase))
                .collect(toList());
        cls();
        int i = 0;
        while (i < drawings.size()) {
            String filename = drawings.get(i);
            log("Viewing text file: " + filename);
            final String content = new String(readBinaryFile(filename), UTF_8);
            boolean firstRow = true;
            for (String row: content.split("\n")) {
                if (!firstRow) println();
                firstRow = false;
                print(row);
            }
            flush(); resetInput();
            int ch = keyPressed(60_000);
            if (ch == '-' && i > 0) {
                i--;
                println();
                println();
                continue;
            }
            if (ch == '.' || ch == 27) break;
            println();
            println();
            i++;
        }
    }

    public void wifiModem() throws Exception {
        cls();
        banner();
        println("Once upon a a time,  there were dial up");
        println("BBSes. Nowadays we have Internet but we");
        println("recreate such an experience.");
        println();
        println("www.museo-computer.it/en/wifi-modem/");
        println();
        println("Get here your brand new WiFi modem, it");
        println("uses your Internet connection to allow");
        println("you to telnet BBSes around the world");
        println();
        println("Press a key to go back");
        println("----------------------");
        flush(); resetInput(); readKey();
    }

    public void videotelVault() throws Exception {
        write(0x1b, 0x3a, 0x6a, 0x43); // scroll off
        List<String> drawings = Utils.getDirContent("minitel/slideshow")
                .stream()
                .map(Path::toString)
                .map(x -> x.startsWith("/") ? x.substring(1) : x)
                .sorted(comparing(String::toLowerCase))
                .collect(toList());
        int i = 0;
        while (i < drawings.size()) {
            String filename = drawings.get(i);
            log("Viewing Minitel file: " + filename);
            byte[] content = readBinaryFile(filename);
            cls();
            write(content);
            flush(); resetInput();
            int ch = keyPressed(60_000);
            if (ch == '-') {
                i--;
                continue;
            }
            if (ch == '.' || ch == 27) break;
            i = (i+1) % drawings.size();
        }
        write(0x1b, 0x3a, 0x69, 0x43); // scroll on
        cls();
    }

    protected void bannerPatronsPublishers() {
        println("Patrons - Publisher subscribers");
        println();
    }

    public String readChoice() throws IOException {
        return readLine(setOfChars(STR_ALPHANUMERIC, "."));
    }

    public void patronsPublishers() throws Exception {
        do {
            cls();
            bannerPatronsPublishers();
            println("1 - Syncroweb (Fulvio Ieva)");
            println(". - Back");
            println();
            resetInput();
            String choice;
            print("> ");
            choice = readChoice();
            resetInput();
            choice = StringUtils.lowerCase(choice);
            if (".".equals(choice)) break;
            BbsThread subThread = null;
            if ("1".equals(choice)) subThread = new SyncroWebAscii();
            if (subThread == null) continue;

            if (subThread instanceof AsciiThread) {
                ((AsciiThread) subThread).clsBytes = this.clsBytes;
            }

            launch(subThread);
        } while (true);
    }

}
