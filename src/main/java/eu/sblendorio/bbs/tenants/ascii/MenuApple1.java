package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.Utils;
import eu.sblendorio.bbs.tenants.mixed.HolidayCommons;
import eu.sblendorio.bbs.tenants.mixed.SwBasicBridge;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.TriConsumer;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static eu.sblendorio.bbs.core.Utils.*;
import static eu.sblendorio.bbs.tenants.mixed.HolidayCommons.isSanremo;
import static java.lang.String.valueOf;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Comparator.comparing;

public class MenuApple1 extends AsciiThread {

    public MenuApple1() {
        this(false);
    }

    public MenuApple1(boolean echo) {
        super();
        setLocalEcho(echo);
    }

    public TriConsumer<BbsThread, Integer, Integer> locate() {
        return null;
    }

    public void logo() throws Exception {
        if (HolidayCommons.isXmasTime()) {
            final String NEW_YEAR = valueOf(HolidayCommons.xmasNewYear());
            readTextFile("ascii/xmas40cols.txt").stream()
                    .map(line -> line.replace("9999", NEW_YEAR))
                    .forEach(this::println);
            flush(); resetInput();
            keyPressed(60_000);
        } else {
            readTextFile("apple1/intro-menu.txt").forEach(this::println);
            flush();
        }
    }

    public String rssPropertyTimeout() { return "rss.a1.timeout"; }

    public String rssPropertyTimeoutDefault() { return "40000"; }

    public void showMainMenu() {
        cls();
        printText(readBinaryFile("ascii/menu80-main.txt"));
    }

    public void showInternationalNews() {
        cls();
        printText(readBinaryFile("ascii/menu80-international-news.txt"));
    }

    public void showItalianNews() {
        cls();
        printText(readBinaryFile("ascii/menu80-italian-news.txt"));
    }

    public void showGames() {
        cls();
        printText(readBinaryFile("ascii/menu80-games.txt"));
    }

    public void showBasicPrograms() {
        cls();
        printText(readBinaryFile("ascii/menu40-basic.txt"));
    }

    public void doLoop() throws Exception {
        logo();
        while (true) {
            showMainMenu();
            flush();
            boolean validKey;
            do {
                validKey = true;
                resetInput();
                String choice = readChoice();
                resetInput();
                choice = StringUtils.lowerCase(choice);
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
                else if ("a".equals(choice)) subThread = new ChatA1(null, getTerminalType(), true);
                else if ("b".equals(choice)) subThread = new PrivateMessagesAscii();
                else if ("c".equals(choice)) subThread = new ElizaAscii();
                else if ("d".equals(choice)) subThread = new ClientChatGptAscii();
                else if ("e".equals(choice)) { wifiModem(); subThread = null; }
                else if ("f".equals(choice)) { textDemo(); subThread = null; }
                else if ("g".equals(choice)) subThread = new WikipediaAscii();
                else if ("h".equals(choice)) { menuBasicPrograms(); subThread = null; }
                else if ("i".equals(choice)) subThread = new EnigmaAscii();
                else if (isSanremo() && "9".equals(choice)) subThread = new SanremoAscii();
                else {
                    validKey = false;
                    subThread = null;
                }
                execute(subThread);
            } while (!validKey);
        }
    }

    public void menuInternationalNews() throws Exception {
        while (true) {
            showInternationalNews();
            flush();
            boolean validKey;
            do {
                validKey = true;
                resetInput();
                String choice = readChoice();
                resetInput();
                choice = StringUtils.lowerCase(choice);
                log("Menu. Choice = " + choice);
                BbsThread subThread;
                if (".".equals(choice)) return;
                else if ("1".equals(choice)) subThread = new LiteCnnAscii40();
                        /*new CnnAscii(
                        rssPropertyTimeout(),
                        rssPropertyTimeoutDefault(),
                        getTerminalType(),
                        null,
                        null
                );*/
                else if ("2".equals(choice)) subThread = new BbcAscii(
                        rssPropertyTimeout(),
                        rssPropertyTimeoutDefault(),
                        getTerminalType(),
                        null,
                        null
                );
                else if ("3".equals(choice)) subThread = new OneRssPoliticoAscii();
                else if ("4".equals(choice)) subThread = new OneRssAJPlusAscii();
                else if ("5".equals(choice)) subThread = new OneRssFoxNewsAscii();
                else if ("6".equals(choice)) subThread = new WiredComAscii();
                else if ("7".equals(choice)) subThread = new VcfedAscii();
                else if ("8".equals(choice)) subThread = new IndieRetroNewsAscii();
                else if ("9".equals(choice)) subThread = new The8BitGuyAscii();
                else if ("0".equals(choice)) subThread = new VitnoAscii();
                else if ("a".equals(choice)) subThread = new OneRss2600Ascii();
                else if ("b".equals(choice)) subThread = new HackadayAscii();
                else if ("c".equals(choice)) subThread = new OneRssAmedeoValorosoEngAscii();
                else if ("d".equals(choice)) subThread = new LiteNprAscii40();
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
            showItalianNews();
            flush();
            boolean validKey;
            do {
                validKey = true;
                resetInput();
                String choice = readChoice();
                resetInput();
                choice = StringUtils.lowerCase(choice);
                log("Menu. Choice = " + choice);
                BbsThread subThread;
                if (".".equals(choice)) return;
                else if ("1".equals(choice)) subThread = new TelevideoRaiAscii(
                        rssPropertyTimeout(),
                        rssPropertyTimeoutDefault(),
                        getTerminalType(),
                        null,
                        null
                );
                else if ("2".equals(choice)) subThread = new LercioAscii();
                else if ("3".equals(choice)) subThread = new DisinformaticoAscii();
                else if ("4".equals(choice)) subThread = new MupinAscii();
                else if ("5".equals(choice)) subThread = new IlFattoQuotidianoAscii();
                else if ("6".equals(choice)) subThread = new AmedeoValorosoAscii();
                else if ("7".equals(choice)) subThread = new ButacAscii();
                else if ("8".equals(choice)) subThread = new AlessandroAlbanoAscii();
                else if ("9".equals(choice)) subThread = new OneRssReady64Ascii();
                else if ("0".equals(choice)) subThread = new CommessoPerplessoAscii();
                else if ("a".equals(choice)) subThread = new LiteFanpageAscii40();
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
            showGames();
            flush();
            boolean validKey;
            do {
                validKey = true;
                resetInput();
                String choice = readChoice();
                resetInput();
                choice = StringUtils.lowerCase(choice);
                log("Menu. Choice = " + choice);
                BbsThread subThread;
                if (".".equals(choice)) return;
                subThread = switch (choice) {
                    case "1" -> new TicTacToeAscii();
                    case "2" -> new Connect4Ascii();
                    case "3" -> new ZorkMachineAscii("zork1", "zmpp/zork1.z3");
                    case "4" -> new ZorkMachineAscii("zork2", "zmpp/zork2.z3");
                    case "5" -> new ZorkMachineAscii("zork3", "zmpp/zork3.z3");
                    case "6" -> new ZorkMachineAscii("hitchhikers", "zmpp/hitchhiker-r60.z3");
                    case "7" -> new ZorkMachineAscii("planetfall", "zmpp/planetfall-r39.z3");
                    case "8" -> new ZorkMachineAscii("stationfall", "zmpp/stationfall-r107.z3");
                    case "9" -> createCastleAdventure();
                    case "0" -> new ZorkMachineAscii("zork1ita", "zmpp/Zork-1-ITA-v7.z5");
                    case "a" -> createAvventuraNelCastello();
                    case "b" -> new ZorkMachineAscii("advent350", "zmpp/advent.z3");
                    case "c" -> new ZorkMachineAscii("advent77ita", "zmpp/avventura-colossal-ita.z5");
                    default -> {
                        validKey = false;
                        yield null;
                    }
                };
                execute(subThread);
            } while (!validKey);
        }
    }

    public void menuBasicPrograms() throws Exception {
        while (true) {
            showBasicPrograms();
            flush();
            boolean validKey;
            do {
                validKey = true;
                resetInput();
                String choice = readChoice();
                resetInput();
                choice = StringUtils.lowerCase(choice);
                log("Menu. Choice = " + choice);
                BbsThread subThread;
                if (".".equals(choice)) return;
                else if ("1".equals(choice)) { SwBasicBridge.run("Star Trek", "basic/startrek-40-1.bas", this, locate()); subThread = null; }
                else if ("2".equals(choice)) { SwBasicBridge.run("Star Trek 2003", "basic/startrek-40-2.bas", this, locate()); subThread = null; }
                else if ("3".equals(choice)) { SwBasicBridge.run("Lunar lander", "basic/lunar-lander-40.bas", this, locate()); subThread = null; }
                else if ("4".equals(choice)) { SwBasicBridge.run("Hamurabi", "basic/hamurabi-40.bas", this, locate()); subThread = null; }
                else if ("5".equals(choice)) { SwBasicBridge.run("Checkers", "basic/checkers-40.bas", this, locate()); subThread = null; }
                else if ("6".equals(choice)) { SwBasicBridge.run("Angela", "basic/angela.bas", this, locate()); subThread = null; }
                else if ("7".equals(choice)) { SwBasicBridge.run("Paper Cup machine", "basic/pcm.bas", this, locate()); subThread = null; }
                else if ("8".equals(choice)) { SwBasicBridge.run("Orbit", "basic/orbit.bas", this, locate()); subThread = null; }
                else if ("9".equals(choice)) { SwBasicBridge.run("Melissa", "basic/melissa.bas", this, locate()); subThread = null; }
                else if ("0".equals(choice)) { SwBasicBridge.run("Dobble", "basic/dobble.bas", this, locate()); subThread = null; }
                else if ("z".equals(choice)) { subThread = new BasicIdeAscii(locate()); }

                else {
                    validKey = false;
                    subThread = null;
                }
                execute(subThread);
            } while (!validKey);
        }
    }

    public BbsThread createAvventuraNelCastello() {
        return new AvventuraNelCastelloAscii("it-it");
    }

    public BbsThread createCastleAdventure() {
        return new AvventuraNelCastelloAscii("en-gb");
    }


    public void execute(BbsThread subThread) throws Exception {
        if (subThread == null) return;

        if (subThread instanceof AsciiThread t) {
            t.clsBytes = this.clsBytes;
            t.screenColumns = this.screenColumns;
            t.screenRows = this.screenRows;
        }
        launch(subThread);
    }

    public String readChoice() throws Exception {
        print(">");
        return readLine(setOfChars(STR_ALPHANUMERIC, "."));
    }

    public void patronsPublishers() throws Exception {
        do {
            cls();
            println("Patrons - Publisher subscribers");
            println();
            println("1 - Syncroweb (Fulvio Ieva)");
            println(". - Back");
            println();
            resetInput();
            String choice;
            choice = readChoice();
            resetInput();
            choice = StringUtils.lowerCase(choice);
            if (".".equals(choice)) break;
            BbsThread subThread = null;
            if ("1".equals(choice)) subThread = new SyncroWebAscii();
            execute(subThread);
        } while (true);
    }

    public void showPatrons() throws Exception {
        List<String> patrons = readExternalTxt(System.getProperty("PATREON_LIST", System.getProperty("user.home") + File.separator + "patreon_list.txt"))
                .stream()
                .filter(StringUtils::isNotBlank)
                .map(StringUtils::trim)
                .filter(str -> !str.startsWith(";"))
                .sorted(comparing(String::toLowerCase))
                .toList();

        cls();
        println("Retrocampus BBS pure text version");
        println();
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
        println("Retrocampus BBS pure text version");
        println();
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

    public void printText(byte[] bytes) {
        for (byte b : bytes)
            if (b != '\n')
                write(b);
            else
                println();
        flush();
    }
}
