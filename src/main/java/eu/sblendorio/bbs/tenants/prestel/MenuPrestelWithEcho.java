package eu.sblendorio.bbs.tenants.prestel;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.PrestelThread;
import eu.sblendorio.bbs.core.Utils;
import eu.sblendorio.bbs.tenants.ascii.*;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static eu.sblendorio.bbs.core.Utils.*;
import static eu.sblendorio.bbs.tenants.mixed.HolidayCommons.isSanremo;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public class MenuPrestelWithEcho extends PrestelThread {

    private byte[] clsBytes = new byte[] {12, 17};

    public MenuPrestelWithEcho() {
        super();
        setLocalEcho(true);
    }

    public byte[] initializingBytes() {
        return new byte[] { 17 };
    }

    public void logo() throws Exception {
        // write(0x14); // Cursor off
        cls();
        write(readBinaryFile("prestel/intro-retrocampus.cept3"));
        flush(); resetInput();
        keyPressed(12_000);
        // write(0x11); // Cursor on
    }


    protected void banner() {
        write(readBinaryFile("prestel/retrocampus-logo.cept3"));
    }

    @Override
    public void doLoop() throws Exception {
        resetInput();
        logo();
        while (true) {
            log("Starting Prestel / main menu");
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
                else if ("a".equals(choice)) subThread = new PrivateMessagesAscii(io);
                else if ("b".equals(choice)) subThread = new ElizaAscii(io);
                else if ("c".equals(choice)) subThread = new ClientChatGptAscii(io);
                else if ("d".equals(choice)) subThread = new WikipediaAscii(io);
                else if ("e".equals(choice)) { textDemo(); subThread = null; }
                else if ("f".equals(choice)) { wifiModem(); subThread = null; }
                else if (isSanremo() && "9".equals(choice)) subThread = new SanremoAscii(io);
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
            cls();
            write(readBinaryFile("prestel/menu-international-news.cept3"));
            flush();
            resetInput();
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
                        rssPropertyTimeout(),
                        rssPropertyTimeoutDefault(),
                        getTerminalType(),
                        bytes(readBinaryFile("prestel/cnn_home.cept3"),13,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,17),
                        bytes(11, 11, 13, 10, 32, 32, 32, 32, 32, 32, 13, 10, 11)
                );
                else if ("2".equals(choice)) subThread = new BbcAscii(
                        rssPropertyTimeout(),
                        rssPropertyTimeoutDefault(),
                        getTerminalType(),
                        bytes(readBinaryFile("prestel/bbc_home.cept3"),13,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,17),
                        bytes(11, 11, 13, 10, 32, 32, 32, 32, 32, 32, 13, 10, 11)
                );
                else if ("3".equals(choice)) subThread = new OneRssPoliticoAscii(io);
                else if ("4".equals(choice)) subThread = new OneRssAJPlusAscii(io);
                else if ("5".equals(choice)) subThread = new OneRssFoxNewsAscii(io);
                else if ("6".equals(choice)) subThread = new IndieRetroNewsAscii(io);
                else if ("7".equals(choice)) subThread = new VcfedAscii(io);
                else if ("8".equals(choice)) subThread = new The8BitGuyAscii(io);
                else if ("9".equals(choice)) subThread = new OneRssAmedeoValorosoEngAscii(io);
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
            write(readBinaryFile("prestel/menu-italian-news.cept3"));
            flush();
            resetInput();
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
                        rssPropertyTimeout(),
                        rssPropertyTimeoutDefault(),
                        getTerminalType(),
                        bytes(20, readBinaryFile("prestel/menu-televideo.cept3")),
                        bytes(11, 11, 13, 10, 32, 32, 32, 32, 32, 32, 13, 10, 11, 17)
                );
                else if ("2".equals(choice)) subThread = new LercioAscii(io);
                else if ("3".equals(choice)) subThread = new DisinformaticoAscii(io);
                else if ("4".equals(choice)) subThread = new MupinAscii(io);
                else if ("5".equals(choice)) subThread = new IlFattoQuotidianoAscii(io);
                else if ("6".equals(choice)) subThread = new AmedeoValorosoAscii(io);
                else if ("7".equals(choice)) subThread = new ButacAscii(io);
                else if ("8".equals(choice)) subThread = new AlessandroAlbanoAscii(io);
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
            write(readBinaryFile("prestel/menu-games.cept3"));
            flush();
            resetInput();
            boolean validKey;
            do {
                validKey = true;
                resetInput();
                String choice;
                int key = readSingleKey();
                choice = String.valueOf((char) key);
                resetInput();
                choice = StringUtils.lowerCase(choice);
                BbsThread subThread;
                if (".".equals(choice)) {
                    return;
                }
                else if ("1".equals(choice)) subThread = new TicTacToeAscii(io);
                else if ("2".equals(choice)) subThread = new Connect4Ascii();
                else if ("3".equals(choice)) subThread = new ZorkMachinePrestel("zork1", "zmpp/zork1.z3");
                else if ("4".equals(choice)) subThread = new ZorkMachinePrestel("zork2", "zmpp/zork2.z3");
                else if ("5".equals(choice)) subThread = new ZorkMachinePrestel("zork3", "zmpp/zork3.z3");
                else if ("6".equals(choice)) subThread = new ZorkMachinePrestel("hitchhikers", "zmpp/hitchhiker-r60.z3");
                else if ("7".equals(choice)) subThread = new ZorkMachinePrestel("planetfall", "zmpp/planetfall-r39.z3");
                else if ("8".equals(choice)) subThread = new AvventuraNelCastelloPrestel("en-gb");
                else if ("9".equals(choice)) subThread = new ZorkMachinePrestel("zork1ita", "zmpp/Zork-1-ITA-v7.z5");
                else if ("0".equals(choice)) subThread = new AvventuraNelCastelloPrestel("it-it");
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

        subThread.keepAliveChar = 17; // 17 = cursor on
        if (subThread instanceof AsciiThread) {
            ((AsciiThread) subThread).clsBytes = this.clsBytes;
            ((AsciiThread) subThread).screenColumns = this.getScreenColumns();
            ((AsciiThread) subThread).screenRows = this.getScreenRows();
        }
        if (subThread instanceof DisinformaticoAscii) {
            ((DisinformaticoAscii) subThread).setPageSize(4);
        }
        launch(subThread);
    }

    @Override
    public void initBbs() throws Exception {
        Thread.sleep(2300);
        resetInput();
        this.keepAliveChar = 20; // 20 = cursor off
    }

    public String rssPropertyTimeout() { return "rss.a1.timeout"; }

    public String rssPropertyTimeoutDefault() { return "40000"; }

    public void displayMenu() throws Exception {
        cls();
        if (isSanremo()) {
            write(readBinaryFile("prestel/menu-retrocampus-sanremo.cept3"));
        } else {
            write(readBinaryFile("prestel/menu-retrocampus-main-menu.cept3"));
        }
        flush(); resetInput();
    }

    public void textDemo() throws Exception {
        List<String> drawings = Utils.getDirContent("prestel/slideshow")
                .stream()
                .map(Path::toString)
                .map(x -> x.startsWith("/") ? x.substring(1) : x)
                .sorted(comparing(String::toLowerCase))
                .collect(toList());
        int i = 0;
        while (i < drawings.size()) {
            String filename = drawings.get(i);
            log("Viewing Prestel file: " + filename);
            byte[] content = readBinaryFile(filename);
            cls();
            write(content);
            flush(); resetInput();
            int ch = keyPressed(60_000);
            if (ch == '-' && i > 0) {
                i--;
                continue;
            }
            if (ch == '.' || ch == 27) break;
            i = (i+1) % drawings.size();
        }
        cls();
    }

    public void showPatrons() throws Exception {
        List<String> patrons = readExternalTxt(System.getProperty("PATREON_LIST", System.getProperty("user.home") + File.separator + "patreon_list.txt"))
                .stream()
                .map(StringUtils::trim)
                .filter(StringUtils::isNotBlank)
                .filter(str -> !str.startsWith(";"))
                .sorted(comparing(String::toLowerCase))
                .collect(toList());

        final int PAGESIZE = 11;
        int pages = patrons.size() / PAGESIZE + (patrons.size() % PAGESIZE == 0 ? 0 : 1);

        for (int p = 0; p < pages; ++p) {
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
            for (int i = 0; i < PAGESIZE; ++i) {
                int index = (p * PAGESIZE + i);
                if (index < patrons.size())
                    println(patrons.get(index));
            }
            flush();
            resetInput();
            int ch = readKey();
            if (ch == '.' || ch == 27) break;
        }
    }

    public int readSingleKey() throws IOException {
        write(20); // Cursor off
        int ch = readKey();
        write(17); // Cursor on
        return ch;
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

    public String readChoice() throws IOException {
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
