package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.Utils;
import eu.sblendorio.bbs.tenants.mixed.HolidayCommons;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.List;

import static eu.sblendorio.bbs.core.Utils.*;
import static java.lang.String.valueOf;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

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

    @Override
    public void initBbs() throws Exception {
        Thread.sleep(500L);
        resetInput();
    }

    protected void banner() {
        println("Retrocampus BBS for UNIX Telnet - by F. Sblendorio " + Calendar.getInstance().get(Calendar.YEAR));
        newline();
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

    @Override
    public void doLoop() throws Exception {
        logo();
        while (true) {
            log("Starting ASCII / main menu");
            cls();
            displayMenu();

            flush();
            boolean validKey;
            do {
                validKey = true;
                log("Menu. Waiting for key pressed.");
                resetInput();
                String choice;
                print("> ");
                choice = readChoice();
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
                        rssPropertyTimeout(),
                        rssPropertyTimeoutDefault(),
                        getTerminalType(),
                        null,
                        null
                );
                else if ("2".equals(choice)) subThread = new BbcAscii(
                        rssPropertyTimeout(),
                        rssPropertyTimeoutDefault(),
                        getTerminalType(),
                        null,
                        null
                );
                else if ("3".equals(choice)) subThread = new OneRssPoliticoAscii();
                else if ("4".equals(choice)) subThread = new OneRssAJPlusAscii();
                else if ("5".equals(choice)) subThread = new IndieRetroNewsAscii();
                else if ("6".equals(choice)) subThread = new VcfedAscii();
                else if ("7".equals(choice)) subThread = new The8BitGuyAscii();
                else if ("a".equals(choice)) subThread = new TelevideoRaiAscii(
                        rssPropertyTimeout(),
                        rssPropertyTimeoutDefault(),
                        getTerminalType(),
                        null,
                        null
                );
                else if ("b".equals(choice)) subThread = new LercioAscii();
                else if ("c".equals(choice)) subThread = new DisinformaticoAscii();
                else if ("d".equals(choice)) subThread = new MupinAscii();
                else if ("e".equals(choice)) subThread = new IlFattoQuotidianoAscii();
                else if ("f".equals(choice)) subThread = new AmedeoValorosoAscii();
                else if ("g".equals(choice)) subThread = new ButacAscii();
                else if ("h".equals(choice)) subThread = new AlessandroAlbanoAscii();
                else if ("i".equals(choice)) subThread = new TicTacToeAscii();
                else if ("j".equals(choice)) subThread = new Connect4Ascii();
                else if ("k".equals(choice)) subThread = new ZorkMachineAscii("zmpp/zork1.z3");
                else if ("l".equals(choice)) subThread = new ZorkMachineAscii("zmpp/zork2.z3");
                else if ("m".equals(choice)) subThread = new ZorkMachineAscii("zmpp/zork3.z3");
                else if ("n".equals(choice)) subThread = new ZorkMachineAscii("zmpp/hitchhiker-r60.z3");
                else if ("x".equals(choice)) subThread = new ZorkMachineAscii("zmpp/planetfall-r39.z3");
                else if ("o".equals(choice)) subThread = new ChatA1(getTerminalType());
                else if ("p".equals(choice)) subThread = new PrivateMessagesAscii();
                else if ("q".equals(choice)) subThread = new ElizaAscii();
                else if ("r".equals(choice)) subThread = new ClientChatGptAscii();
                else if ("s".equals(choice)) { showPatrons(); subThread = null; }
                else if ("t".equals(choice)) { patronsPublishers(); subThread = null; }
                else if ("u".equals(choice)) { wifiModem(); subThread = null; }
                else if ("v".equals(choice)) { textDemo(); subThread = null; }
                else if ("w".equals(choice)) subThread = new WikipediaAscii();
                else {
                    validKey = false;
                    subThread = null;
                }
                if (subThread == null) continue;

                if (subThread instanceof AsciiThread) {
                    ((AsciiThread) subThread).clsBytes = this.clsBytes;
                    ((AsciiThread) subThread).screenColumns = this.screenColumns;
                    ((AsciiThread) subThread).screenRows = this.screenRows;
                }
                if (subThread instanceof WordpressProxyAscii) {
                    ((WordpressProxyAscii) subThread).pageSize *= 2;
                } else if (subThread instanceof GoogleBloggerProxyAscii) {
                    ((GoogleBloggerProxyAscii) subThread).pageSize *= 2;
                } else if (subThread instanceof OneRssAscii) {
                    ((OneRssAscii) subThread).pageSize *= 2;
                }
                launch(subThread);
            } while (!validKey);
        }
    }

    public void displayMenu() throws Exception {
        banner();
        println("International News-----------------------  Game Room ------");
        println("1 - CNN News                               I - TIC TAC TOE");
        println("2 - BBC News                               J - Connect Four");
        println("3 - Politico.com                           K - Zork I");
        println("4 - Al Jazeera                             L - Zork II");
        println("5 - Indie Retro News                       M - Zork III");
        println("6 - VCF News                               N - Hitchhiker's");
        println("7 - The 8-Bit Guy                          X - Planetfall");
        println("                                           --------------Services");
        println("Italian News-----------------------------  O - Chat");
        println("A - Televideo RAI                          P - Private Msg");
        println("B - Lercio                                 Q - Eliza");
        println("C - Disinformatico                         R - Chat GPT");
        println("D - Mupin.it                               S - Patrons list");
        println("E - Fatto Quotidiano                       T - Patrons Publishers");
        println("F - Amedeo Valoroso                        U - Wifi Modem");
        println("G - Butac.it                               V - Apple-1 Demo");
        println("H - Alessandro Albano                      W - Wikipedia");
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
                ((AsciiThread) subThread).screenColumns = this.screenColumns;
                ((AsciiThread) subThread).screenRows = this.screenRows;
            }

            if (subThread instanceof WordpressProxyAscii) {
                ((WordpressProxyAscii) subThread).pageSize *= 2;
            } else if (subThread instanceof GoogleBloggerProxyAscii) {
                ((GoogleBloggerProxyAscii) subThread).pageSize *= 2;
            } else if (subThread instanceof OneRssAscii) {
                ((OneRssAscii) subThread).pageSize *= 2;
            }

            launch(subThread);
        } while (true);
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

    public int readSingleKey() throws IOException {
        return readKey();
    }

}
