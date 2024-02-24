package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.tenants.mixed.HolidayCommons;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import static eu.sblendorio.bbs.core.Utils.*;
import static eu.sblendorio.bbs.tenants.mixed.HolidayCommons.isSanremo;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public class MenuM10 extends AsciiThread {

    public MenuM10() {
        super();
        setLocalEcho(true);
        clsBytes = new byte[] {
            12
        };
        screenColumns = 40;
        screenRows = 15;
    }

    @Override
    public void initBbs() throws Exception {
        Thread.sleep(2000);
        resetInput();
    }

    protected void banner() {
        println("BBS for M10      '.' to logout");
    }

    public void logo() throws Exception {}

    public String rssPropertyTimeout() { return "rss.m10.timeout"; }

    public String rssPropertyTimeoutDefault() { return "60000"; }

    public void displayMenu() throws Exception {
        banner();
        println("1-CNN      2-BBC       Game Room-------");
        println("3-Politico 4-Aljazeera I-TIC TAC TOE");
        println("5-FoxNews              J-Connect Four");
        println("6-Indie Retro New      K-Zork I   Y-ITA");
        println("7-VCF News             L-Zork II");
        println("8-The 8-Bit Guy        M-Zork III");
        println("Italian News---------  X-Planetfall");
        println(isSanremo()
              ? "A-Televideo 9=Sanremo24 Services-------"
              : "A-Televideo RAI        Services--------");
        println("B-Lercio               O-Chat");
        println("C-Disinformatico       P-Private Msg");
        println("D-Mupin.it             Q-Eliza");
        println("E-Fatto Quotidiano     R-ChatGPT");
        println("F-A.Valoroso (Z=ENG)   S-Patron list");
        println("G-Butac.it             T-Wifi Modem");
        println("H-Alessandro Albano    U-Wikipedia");
    }

    public String readChoice() throws IOException {
        return readLine(setOfChars(STR_ALPHANUMERIC, "."));
    }


    @Override
    public void doLoop() throws Exception {
        logo();
        while (true) {
            log("Starting M10 / main menu");
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
                else if ("5".equals(choice)) subThread = new OneRssFoxNewsAscii();
                else if ("6".equals(choice)) subThread = new IndieRetroNewsAscii();
                else if ("7".equals(choice)) subThread = new VcfedAscii();
                else if ("8".equals(choice)) subThread = new The8BitGuyAscii();
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
                else if ("z".equals(choice)) subThread = new OneRssAmedeoValorosoEngAscii();
                else if ("g".equals(choice)) subThread = new ButacAscii();
                else if ("h".equals(choice)) subThread = new AlessandroAlbanoAscii();
                else if ("i".equals(choice)) subThread = new TicTacToeAscii();
                else if ("j".equals(choice)) subThread = new Connect4Ascii();
                else if ("k".equals(choice)) subThread = new ZorkMachineAscii("zmpp/zork1.z3");
                else if ("y".equals(choice)) subThread = new ZorkMachineAscii("zmpp/Zork-1-ITA-v7.z5");
                else if ("l".equals(choice)) subThread = new ZorkMachineAscii("zmpp/zork2.z3");
                else if ("m".equals(choice)) subThread = new ZorkMachineAscii("zmpp/zork3.z3");
                else if ("n".equals(choice)) subThread = new ZorkMachineAscii("zmpp/hitchhiker-r60.z3");
                else if ("x".equals(choice)) subThread = new ZorkMachineAscii("zmpp/planetfall-r39.z3");
                else if ("o".equals(choice)) subThread = new ChatA1(getTerminalType());
                else if ("p".equals(choice)) subThread = new PrivateMessagesAscii();
                else if ("q".equals(choice)) subThread = new ElizaAscii();
                else if ("r".equals(choice)) subThread = new ClientChatGptAscii();
                else if ("s".equals(choice)) { showPatrons(); subThread = null; }
                else if ("t".equals(choice)) { wifiModem(); subThread = null; }
                else if ("u".equals(choice)) subThread = new WikipediaAscii();
                else if (isSanremo() && "9".equals(choice)) subThread = new SanremoAscii();
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
}
