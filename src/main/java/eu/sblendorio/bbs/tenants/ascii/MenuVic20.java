package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.BbsThread;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static eu.sblendorio.bbs.core.Utils.*;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public class MenuVic20 extends AsciiThread {

    public MenuVic20() {
        super();
        setLocalEcho(true);
        clsBytes = new byte[] {
            12
        };
        screenColumns = 22;
        screenRows = 23;
    }

    @Override
    public void initBbs() throws Exception {
        Thread.sleep(300);
        resetInput();
    }

    protected void banner() {
        println("RetrocampusBBS - Vic20");
        newline();
    }

    public void logo() throws Exception {}

    public String rssPropertyTimeout() { return "rss.vic20.timeout"; }

    public String rssPropertyTimeoutDefault() { return "60000"; }

    public void displayMenu() throws Exception {
        banner();
        println("Intl.News Game Room");
        println("--------- -----------");
        println("1-CNN     N-TicTacToe");
        println("2-BBC     O-Connect 4");
        println("3-PoliticoP-Zork I");
        println("4-AJPlus  Q-Zork II");
        println("5-IRNews  R-Zork III");
        println("6-VCFNews S-Hitchhikr");
        println("7-8bitGuy 8-Planetfal");
        println("Italian News  Service");
        println("------------  -------");
        println("F-Televideo   T-Chat");
        println("G-Wired       U-Msgs");
        println("H-Disinfor    U-Eliza");
        println("I-IlPost      W-ChGPT");
        println("J-F.Quot      X-Patre");
        println("K-A. Valoroso Y-Modem");
        println("L-Butac       Z-WikiP");
        println("M-A.Albano    .-Exit");
    }

    public void wifiModem() throws Exception {
        cls();
        banner();
        println("Once upon a a time,");
        println("there were dial up");
        println("BBSes. Nowadays we");
        println("have Internet but we");
        println("recreate such an");
        println("experience.");
        println();
        println("www.museo-computer.it");
        println("      /en/wifi-modem/");
        println();
        println("Get here your brand");
        println("new WiFi modem, it");
        println("uses your Internet");
        println("connection to allow");
        println("you to telnet BBSes");
        println("around the world");
        println();
        println("Press any key");
        println("-------------");
        flush(); resetInput(); readKey();
    }

    public String readChoice() throws IOException {
        return readLine(setOfChars(STR_ALPHANUMERIC, "."));
    }

    public void doLoop() throws Exception {
        logo();
        while (true) {
            log("Starting Apple1 / main menu");
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
                else if ("f".equals(choice)) subThread = new TelevideoRaiAscii(
                        rssPropertyTimeout(),
                        rssPropertyTimeoutDefault(),
                        getTerminalType(),
                        null,
                        null
                );
                else if ("g".equals(choice)) subThread = new LercioAscii();
                else if ("h".equals(choice)) subThread = new DisinformaticoAscii();
                else if ("i".equals(choice)) subThread = new MupinAscii();
                else if ("j".equals(choice)) subThread = new IlFattoQuotidianoAscii();
                else if ("k".equals(choice)) subThread = new AmedeoValorosoAscii();
                else if ("l".equals(choice)) subThread = new ButacAscii();
                else if ("m".equals(choice)) subThread = new AlessandroAlbanoAscii();
                else if ("n".equals(choice)) subThread = new TicTacToeAscii();
                else if ("o".equals(choice)) subThread = new Connect4Ascii();
                else if ("p".equals(choice)) subThread = new ZorkMachineAscii("zmpp/zork1.z3");
                else if ("q".equals(choice)) subThread = new ZorkMachineAscii("zmpp/zork2.z3");
                else if ("r".equals(choice)) subThread = new ZorkMachineAscii("zmpp/zork3.z3");
                else if ("s".equals(choice)) subThread = new ZorkMachineAscii("zmpp/hitchhiker-r60.z3");
                else if ("8".equals(choice)) subThread = new ZorkMachineAscii("zmpp/planetfall-r39.z3");
                else if ("t".equals(choice)) subThread = new ChatA1(getTerminalType());
                else if ("u".equals(choice)) subThread = new PrivateMessagesAscii();
                else if ("v".equals(choice)) subThread = new ElizaAscii();
                else if ("w".equals(choice)) subThread = new ClientChatGptAscii();
                else if ("x".equals(choice)) { showPatrons(); subThread = null; }
                else if ("y".equals(choice)) { wifiModem(); subThread = null; }
                else if ("z".equals(choice)) subThread = new WikipediaAscii();
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
                    ((WordpressProxyAscii) subThread).pageSize /= 2;
                } else if (subThread instanceof GoogleBloggerProxyAscii) {
                    ((GoogleBloggerProxyAscii) subThread).pageSize /= 2;
                } else if (subThread instanceof OneRssAscii) {
                    ((OneRssAscii) subThread).pageSize /= 2;
                }
                launch(subThread);
            } while (!validKey);
        }
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
}
