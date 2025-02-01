package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.Utils;
import eu.sblendorio.bbs.tenants.mixed.PatreonData;
import eu.sblendorio.bbs.tenants.mixed.SwBasicBridge;
import eu.sblendorio.bbs.tenants.mixed.HolidayCommons;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.TriConsumer;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static eu.sblendorio.bbs.core.Utils.*;
import static eu.sblendorio.bbs.tenants.CommonConstants.*;
import static eu.sblendorio.bbs.tenants.mixed.HolidayCommons.isSanremo;
import static java.lang.String.valueOf;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Comparator.comparing;

public class MenuTelnetPureAscii extends AsciiThread {
    public MenuTelnetPureAscii() {
        super();
        setLocalEcho(true);
        clsBytes = new byte[] {
            13, 10,
            13, 10,
            13, 10,
            13, 10
        };

        screenColumns = 80;
    }

    public TriConsumer<BbsThread, Integer, Integer> locate() {
        return null;
    }


    public void printText(byte[] bytes) {
        for (byte b : bytes)
            if (b != '\n')
                write(b);
            else
                println();
        flush();
    }

    public void showMainMenu() {
        cls();
        printText(readBinaryFile(
                HolidayCommons.isVcf()
                ? "ascii/menu80-main-vcfsw.txt"
                : "ascii/menu80-main.txt"
        ));
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
        printText(readBinaryFile("ascii/menu80-basic.txt"));
    }

    public void boldOn() {}

    public void boldOff() {}

    @Override
    public void initBbs() throws Exception {
        Thread.sleep(500L);
        resetInput();
    }

    protected void bannerPatronsPublishers() {
        println("Patrons - Publisher subscribers");
        println();
    }

    public void logo() throws Exception {
        if (HolidayCommons.isXmasTime()) {
            cls();
            final String NEW_YEAR = valueOf(HolidayCommons.xmasNewYear());
            readTextFile("ascii/xmas80cols.txt").stream()
                    .map(line -> line.replace("9999", NEW_YEAR))
                    .forEach(x -> {  println(); print(x); });
            flush();
            resetInput();
            keyPressed(30_000);
        //} else if (HolidayCommons.isHalloweenTime()) {
        //    readTextFile("ascii/halloween80.txt").forEach(this::println);
        //    flush();
        //    resetInput();
        //    keyPressed(30_000);
        }
    }

    @Override
    public byte[] initializingBytes() {
       return "\377\375\042\377\373\001".getBytes(ISO_8859_1);
    }

    /*
       IAC  = 255 = \377 https://tools.ietf.org/html/rfc854#page-14

      WILL (option code)  251    Indicates the desire to begin
                         \373    performing, or confirmation that
                                 you are now performing, the
                                 indicated option.
      WON'T (option code) 252    Indicates the refusal to perform,
                         \374    or continue performing, the
                                 indicated option.
      DO (option code)    253    Indicates the request that the
                         \375    other party perform, or
                                 confirmation that you are expecting
                                 the other party to perform, the
                                 indicated option.
      DON'T (option code) 254    Indicates the demand that the
                         \376    other party stop performing,
                                 or confirmation that you are no
                                 longer expecting the other party
                                 to perform, the indicated option.
      IAC                 255    Data Byte 255.
     */

    public String rssPropertyTimeout() { return "rss.a1.timeout"; }

    public String rssPropertyTimeoutDefault() { return "40000"; }

    public void execute(BbsThread subThread) throws Exception {
        if (subThread == null) return;

        if (subThread instanceof AsciiThread t) {
            t.clsBytes = this.clsBytes;
            t.screenColumns = this.screenColumns;
            t.screenRows = this.screenRows;
        }
        if (subThread instanceof WordpressProxyAscii t) {
            if (t.resizeable()) t.pageSize *= 2;
        } else if (subThread instanceof GoogleBloggerProxyAscii t) {
            if (t.resizeable()) t.pageSize *= 2;
        } else if (subThread instanceof OneRssAscii t) {
            if (t.resizeable()) t.pageSize *= 2;
        }
        launch(subThread);
    }

    public void initTerminal() throws Exception {}

    @Override
    public void doLoop() throws Exception {
        resetInput();
        initTerminal();
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
                log("Menu. Choice = " + choice);
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
                else if ("a".equals(choice)) subThread = new ChatA1(getTerminalType());
                else if ("b".equals(choice)) subThread = new PrivateMessagesAscii();
                else if ("c".equals(choice)) subThread = new ElizaAscii();
                else if ("d".equals(choice)) subThread = new ClientChatGptAscii("ChatGPT", CHATGPT_API, "OPENAI_KEY", CHATGPT_MODEL, null, null);
                else if ("e".equals(choice)) subThread = new ClientChatGptAscii("Mistral", MISTRAL_API, "MISTRALAI_KEY", MISTRAL_API, null, null);
                else if ("f".equals(choice)) { wifiModem(); subThread = null; }
                else if ("g".equals(choice)) { textDemo(); subThread = null; }
                else if ("h".equals(choice)) subThread = new WikipediaAscii();
                else if ("i".equals(choice)) { menuBasicPrograms(); subThread = null; }
                else if ("j".equals(choice)) subThread = new EnigmaAscii();
                else if ("v".equals(choice) && HolidayCommons.isVcf()) { showVcfSw2025(); subThread = null;}
                else if (isSanremo() && "9".equals(choice)) subThread = new SanremoAscii();
                else {
                    validKey = false;
                    subThread = null;
                }
                execute(subThread);
            } while (!validKey);
        }
    }

    public void showVcfSw2025() throws Exception {
        cls();
        printText(readBinaryFile("ascii/vcfsw2025.txt"));
        flush(); resetInput();
        readKey();
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
                else if ("1".equals(choice)) subThread = new LiteCnnAscii80();
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
                else if ("d".equals(choice)) subThread = new LiteNprAscii80();
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
                else if ("a".equals(choice)) subThread = new LiteFanpageAscii80();
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
                    case "0" -> new ZorkMachineAscii("zork1ita", "zmpp/Zork-1-ITA-v7.z5", this::boldOn, this::boldOff);
                    case "a" -> createAvventuraNelCastello();
                    case "b" -> new ZorkMachineAscii("advent350", "zmpp/advent.z3");
                    case "c" -> new ZorkMachineAscii("advent77ita", "zmpp/avventura-colossal-ita.z5", this::boldOn, this::boldOff, Map.of(
                            "help", () -> { println("Help non disponibile"); println(); print(">"); }
                    ));
                    case "d" -> new ZorkMachineAscii("wishbringer", "zmpp/wishbringer-r69.z3");
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
                else if ("3".equals(choice)) { SwBasicBridge.run("Lunar Lander", "basic/lunar-lander-40.bas", this, locate()); subThread = null; }
                else if ("4".equals(choice)) { SwBasicBridge.run("Hamurabi", "basic_cc/hamurabi.bas", this, locate()); subThread = null; }
                else if ("5".equals(choice)) { SwBasicBridge.run("Checkers", "basic_cc/checkers.bas", this, locate()); subThread = null; }
                else if ("6".equals(choice)) { SwBasicBridge.run("Angela", "basic/angela.bas", this, locate()); subThread = null; }
                else if ("7".equals(choice)) { SwBasicBridge.run("Paper Cup Machine", "basic/pcm.bas", this, locate()); subThread = null; }
                else if ("8".equals(choice)) { SwBasicBridge.run("Orbit", "basic/orbit.bas", this, locate()); subThread = null; }
                else if ("9".equals(choice)) { SwBasicBridge.run("Melissa", "basic/melissa.bas", this, locate()); subThread = null; }
                else if ("0".equals(choice)) { SwBasicBridge.run("Dobble", "basic/dobble-dash.bas", this, locate()); subThread = null; }
                else if ("a".equals(choice)) { SwBasicBridge.run("Oregon Trail", "basic/oregon.bas", this, locate()); subThread = null; }
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

    public String readChoice() throws Exception {
        print(">");
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
        List<String> patrons = PatreonData.getPatronsWithTier();

        cls();
        println("You can support the development of this");
        println("BBS through Patreon starting with 3$ or");
        println("3.50eur per month:");
        println();
        println("https://patreon.com/FrancescoSblendorio");
        println();
        println("Patrons of this BBS");
        println("-------------------");

        final int PAGESIZE = getScreenRows()-2;

        int line = 0;
        for  (int i = 0; i<patrons.size(); i++) {
            line++;
            println(patrons.get(i));

            if (line % PAGESIZE == 0 || i == patrons.size()-1) {
                println();
                print("Press any key.");
                flush(); resetInput(); readKey();
                println();
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

}
