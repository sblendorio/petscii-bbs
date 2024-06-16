package eu.sblendorio.bbs.tenants.minitel;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import eu.sblendorio.bbs.core.*;
import eu.sblendorio.bbs.tenants.ascii.*;
import eu.sblendorio.bbs.tenants.mixed.HolidayCommons;
import eu.sblendorio.bbs.tenants.mixed.SwBasicBridge;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.TriConsumer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static eu.sblendorio.bbs.core.MinitelControls.*;
import static eu.sblendorio.bbs.core.Utils.*;
import static eu.sblendorio.bbs.tenants.mixed.GeolocationCommons.isItaly;
import static eu.sblendorio.bbs.tenants.mixed.GeolocationCommons.isLocalhost;
import static eu.sblendorio.bbs.tenants.mixed.HolidayCommons.isSanremo;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Comparator.comparing;

public class MenuMinitelWithEcho extends MinitelThread {
    final static byte[] SPLASH_CASTELLO_ITA = readBinaryFile("minitel/avventura-nel-castello.vdt");
    final static byte[] SPLASH_CASTELLO_ENG = readBinaryFile("minitel/castle-adventure.vdt");

    final static byte[] COPYRIGHT_CASTELLO_ITA = readBinaryFile("minitel/copyright-castello-ita.txt");
    final static byte[] COPYRIGHT_CASTELLO_ENG = readBinaryFile("minitel/copyright-castello-eng.txt");

    private byte[] clsBytes = new byte[] { 12 };

    public TriConsumer<BbsThread, Integer, Integer> locate() {
        return (bbs, y, x) -> {
            if (x>0 && y>0) {
                bbs.write(MOVEXY, 0x40 + y, 0x40 + x);
            } else if (y>0) {
                write(0x1e);
                for (int i=1; i<y; i++) bbs.write(0x0a);
            } else if (x>0) {
                bbs.write(0x0d, 0x0a, 0x0b);
                for (int i=1; i<x; i++) bbs.write(0x09);
            }
        };
    }

    public MenuMinitelWithEcho() {
        super();
        setLocalEcho(true);
    }

    public byte[] initializingBytes() {
        return bytes(CAPSLOCK_OFF, SCROLL_ON, CURSOR_ON);
    }

    public String rssPropertyTimeout() { return "rss.a1.timeout"; }

    public String rssPropertyTimeoutDefault() { return "40000"; }

    public void menuInternationalNews() throws Exception {
        while (true) {
            cls();
            write(SCROLL_OFF);
            write(readBinaryFile("minitel/menu-international-news.vdt"));
            write(SCROLL_ON);
            flush(); resetInput();
            boolean validKey;
            do {
                validKey = true;
                resetInput();
                String choice;
                int key = readSingleKey();
                choice = String.valueOf((char) key);
                resetInput();
                choice = StringUtils.lowerCase(choice);
                BbsThread subThread = null;
                if (".".equals(choice)) {
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
                else if ("9".equals(choice)) subThread = new OneRssAmedeoValorosoEngAscii();
                else {
                    validKey = false;
                    subThread = null;
                }

                execute(subThread);
            } while (!validKey);
        }
    }

    public void menuItalianNews() throws Exception {
        while (true) {
            cls();
            write(SCROLL_OFF);
            write(readBinaryFile("minitel/menu-italian-news.vdt"));
            write(SCROLL_ON);
            flush(); resetInput();
            boolean validKey;
            do {
                validKey = true;
                resetInput();
                String choice;
                int key = readSingleKey();
                choice = String.valueOf((char) key);
                resetInput();
                choice = StringUtils.lowerCase(choice);
                BbsThread subThread = null;
                if (".".equals(choice)) {
                    return;
                }
                else if ("1".equals(choice)) subThread = new TelevideoRaiAscii(
                        io,
                        rssPropertyTimeout(),
                        rssPropertyTimeoutDefault(),
                        getTerminalType(),
                        readBinaryFile("minitel/menu-televideo.vdt"),
                        bytes(31, 64+23, 64+1, 32, 32, 32, 32, 32, 32, 31, 64+23, 64+1)
                );
                else if ("2".equals(choice)) subThread = new LercioMinitel();
                else if ("3".equals(choice)) subThread = new DisinformaticoAscii(io);
                else if ("4".equals(choice)) subThread = new MupinAscii(io);
                else if ("5".equals(choice)) subThread = new IlFattoQuotidianoMinitel();
                else if ("6".equals(choice)) subThread = new AmedeoValorosoAscii(io);
                else if ("7".equals(choice)) subThread = new ButacMinitel();
                else if ("8".equals(choice)) subThread = new AlessandroAlbanoAscii(io);
                else if ("9".equals(choice)) subThread = new OneRssReady64Ascii(io);
                else if ("0".equals(choice)) subThread = new CommessoPerplessoMinitel();
                else {
                    validKey = false;
                    subThread = null;
                }

                execute(subThread);
            } while (!validKey);
        }
    }

    public void menuGames() throws Exception {
        while (true) {
            cls();
            write(SCROLL_OFF);
            write(readBinaryFile("minitel/menu-games.vdt"));
            write(SCROLL_ON);
            flush(); resetInput();
            boolean validKey;
            do {
                validKey = true;
                resetInput();
                String choice;
                int key = readSingleKey();
                choice = String.valueOf((char) key);
                resetInput();
                choice = StringUtils.lowerCase(choice);
                BbsThread subThread = null;
                if (".".equals(choice)) {
                    return;
                }
                else if ("1".equals(choice)) subThread = new TicTacToeAscii();
                else if ("2".equals(choice)) subThread = new Connect4Ascii();
                else if ("3".equals(choice)) subThread = new ZorkMachineMinitel("zork1", "zmpp/zork1.z3");
                else if ("4".equals(choice)) subThread = new ZorkMachineMinitel("zork2", "zmpp/zork2.z3");
                else if ("5".equals(choice)) subThread = new ZorkMachineMinitel("zork3", "zmpp/zork3.z3");
                else if ("6".equals(choice)) subThread = new ZorkMachineMinitel("hitchhikers", "zmpp/hitchhiker-r60.z3", readBinaryFile("minitel/hitchhikers.vdt"));
                else if ("7".equals(choice)) subThread = new ZorkMachineMinitel("planetfall", "zmpp/planetfall-r39.z3", readBinaryFile("minitel/planetfall.vdt"));
                else if ("8".equals(choice)) subThread = new AvventuraNelCastelloMinitel(SPLASH_CASTELLO_ENG, COPYRIGHT_CASTELLO_ENG, "en-gb");
                else if ("9".equals(choice)) subThread = new ZorkMachineMinitel("zork1ita", "zmpp/Zork-1-ITA-v7.z5", null, () -> attributes(CHAR_WHITE), () -> attributes(CHAR_GREEN));
                else if ("0".equals(choice)) subThread = new AvventuraNelCastelloMinitel(SPLASH_CASTELLO_ITA, COPYRIGHT_CASTELLO_ITA, "it-it");
                else {
                    validKey = false;
                    subThread = null;
                }

                execute(subThread);
            } while (!validKey);
        }
    }

    public void menuBasicPrograms() throws Exception {
        while (true) {
            cls();
            write(SCROLL_OFF);
            write(readBinaryFile("minitel/menu-basic-programs.vdt"));
            write(SCROLL_ON);
            attributes(CHAR_WHITE);
            flush(); resetInput();
            boolean validKey;
            do {
                validKey = true;
                resetInput();
                String choice;
                int key = readSingleKey();
                choice = String.valueOf((char) key);
                resetInput();
                choice = StringUtils.lowerCase(choice);
                BbsThread subThread = null;
                if (".".equals(choice)) {
                    return;
                }
                else if ("1".equals(choice)) { SwBasicBridge.run("Star Trek", "basic/startrek-40-1.bas", this, locate()); subThread = null; }
                else if ("2".equals(choice)) { SwBasicBridge.run("Star Trek 2003", "basic/startrek-40-2.bas", this, locate()); subThread = null; }
                else if ("3".equals(choice)) { SwBasicBridge.run("Lunar Lander", "basic/lunar-lander-40.bas", this, locate()); subThread = null; }
                else if ("4".equals(choice)) { SwBasicBridge.run("Hamurabi", "basic/hamurabi-40.bas", this, locate()); subThread = null; }
                else if ("5".equals(choice)) { SwBasicBridge.run("Checkers", "basic/checkers-40.bas", this, locate()); subThread = null; }
                else if ("6".equals(choice)) { SwBasicBridge.run("Angela", "basic/angela.bas", this, locate()); subThread = null; }
                else if ("z".equals(choice)) { subThread = new BasicIdeMinitel(locate()); }
                else {
                    validKey = false;
                    subThread = null;
                }

                execute(subThread);
            } while (!validKey);
        }
    }

    public void execute(BbsThread subThread) throws Exception {
        if (subThread == null) return;
        if (subThread instanceof AsciiThread t) {
            t.clsBytes = this.clsBytes;
            t.screenColumns = this.getScreenColumns();
            t.screenRows = this.getScreenRows();
        }
        launch(subThread);
    }

    @Override
    public void doLoop() throws Exception {
        attributes(BACKGROUND_BLACK);
        attributes(CHAR_WHITE);
        attributes(TEXTSIZE_NORMAL);
        write(0x1F, 0x40, 0x41, 0x18, 0x0F); // blank first line
        resetInput();
        logo();
        while (true) {
            log("Starting Minitel / main menu");
            attributes(BACKGROUND_BLACK);
            attributes(CHAR_WHITE);
            attributes(TEXTSIZE_NORMAL);
            write(0x1F, 0x40, 0x41, 0x18, 0x0F); // blank first line
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
                else if ("1".equals(choice)) { menuInternationalNews(); subThread = null; }
                else if ("2".equals(choice)) { menuItalianNews(); subThread = null; }
                else if ("3".equals(choice)) { menuGames(); subThread = null; }
                else if ("4".equals(choice)) { showPatrons(); subThread = null; }
                else if ("5".equals(choice)) { patronsPublishers(); subThread = null; }
                else if ("a".equals(choice)) subThread = new ChatA1(io, getTerminalType());
                else if ("b".equals(choice)) subThread = new PrivateMessagesAscii(io);
                else if ("c".equals(choice)) subThread = new ElizaAscii(io);
                else if ("d".equals(choice)) subThread = new ClientChatGptAscii(io, readBinaryFile("minitel/chatgpt-mainlogo.vdt"));
                else if ("e".equals(choice)) subThread = new WikipediaMinitel();
                else if ("f".equals(choice)) { videotelVault(); subThread = null; }
                else if ("g".equals(choice)) { textDemo(); subThread = null; }
                else if ("h".equals(choice)) { wifiModem(); subThread = null; }
                else if ("i".equals(choice)) { menuBasicPrograms(); subThread = null; }
                else if ("j".equals(choice)) subThread = new EnigmaAscii(io);
                // else if (isSanremo() && "9".equals(choice)) subThread = new SanremoAscii(io);
                else if ("*".equals(choice)) subThread = new TestClientVideotex();
                else {
                    validKey = false;
                    subThread = null;
                }
                execute(subThread);
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
            write(readBinaryFile("minitel/menu-retrocampus-main.vdt"));
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
                .toList();

        cls();
        banner();

        gotoXY(0,4);
        println("You can support the development of this");
        println("BBS through Patreon starting with 3$ or");
        println("3.50eur per month:");
        println();
        println("https://patreon.com/FrancescoSblendorio");
        println();
        println("Patrons of this BBS");
        println("-------------------");

        final int PAGESIZE = getScreenRows()-2;

        boolean firstPage = true;
        int line = 12;
        for  (int i = 0; i<patrons.size(); i++) {
            line++;
            println(patrons.get(i));

            if (line % PAGESIZE == 0 || i == patrons.size()-1) {
                println();
                print("Press any key.");

                if (firstPage) {
                    String[] matrix = BlockGraphicsMinitel.stringToQr("t.ly/c1ryZ", ErrorCorrectionLevel.L);
                    int len = matrix.length;
                    List<String> listMatrix = new ArrayList<>();
                    listMatrix.add(StringUtils.repeat('.', matrix[0].length()));
                    Collections.addAll(listMatrix, matrix);
                    String[] strMatrix = listMatrix.toArray(new String[len]);
                    gotoXY(0, firstPage ? 9 : 0);
                    write(CURSOR_OFF);
                    write(MinitelControls.GRAPHICS_MODE);
                    write(BlockGraphicsMinitel.getRenderedMidres(29, strMatrix, false, true));
                    write(TEXT_MODE);
                }
                flush(); resetInput(); readKey();
                write(CURSOR_ON);
                cls();
                firstPage = false;
            }
        }

    }

    public void textDemo() throws Exception {
        List<String> drawings = Utils.getDirContent("apple1/demo30th")
                .stream()
                .map(Path::toString)
                .map(x -> x.startsWith("/") ? x.substring(1) : x)
                .sorted(comparing(String::toLowerCase))
                .toList();
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
        write(CURSOR_OFF);
        List<String> drawings = Utils.getDirContent("minitel/slideshow")
                .stream()
                .map(Path::toString)
                .map(x -> x.startsWith("/") ? x.substring(1) : x)
                .sorted(comparing(String::toLowerCase))
                .toList();
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
        write(CURSOR_ON);
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
            execute(subThread);
        } while (true);
    }

}
