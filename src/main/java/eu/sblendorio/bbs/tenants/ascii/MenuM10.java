package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.BbsThread;
import org.apache.commons.lang3.StringUtils;

import static eu.sblendorio.bbs.core.Utils.*;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

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


    private static final String IP_FOR_ALTERNATE_LOGO = System.getProperty("alternate.logo.ip", "none");
    private static final int PORT_FOR_ALTERNATE_LOGO = toInt(System.getProperty("alternate.logo.port", "-1"));
    public boolean alternateLogo() {
        return IP_FOR_ALTERNATE_LOGO.equals(serverAddress.getHostAddress())
                || serverPort == PORT_FOR_ALTERNATE_LOGO;
    }


    protected void banner() {
        println("BBS for M10 - by F.Sblendorio " + Calendar.getInstance().get(Calendar.YEAR));
    }

    public void logo() throws Exception {}

    public String rssPropertyTimeout() { return "rss.m10.timeout"; }

    public String rssPropertyTimeoutDefault() { return "60000"; }

    public void displayMenu() throws Exception {
        banner();
        println("1 - CNN News           Game Room-------");
        println("2 - BBC News           N - TIC TAC TOE");
        println("3 - Politico.com       O - Connect Four");
        println("4 - Al Jazeera         P - Zork I");
        println("5 - Indie Retro New    Q - Zork II");
        println("6 - VCF News           R - Zork III");
        println("7 - The 8-Bit Guy");
        println("Italian News---------  S - Hitchhiker's");
        println("F - Televideo RAI      Services--------");
        println("G - Lercio             T - Chat");
        println("H - Disinformatico     U - Private Msg");
        println("I - Mupin.it           V - Eliza");
        println("J - Fatto Quotidiano   W - ChatGPT");
        println("K - Amedeo Valoroso    X - Patron list");
        println("L - Butac.it           Y - Wifi Modem");
        println("M - Alessandro Albano  . - Logout");
    }

    public String readChoice() throws IOException {
        return readLine(setOfChars(STR_ALPHANUMERIC, "."));
    }


    @Override
    public void doLoop() throws Exception {
        if (alternateLogo()) { println();println();println("Moved to BBS.RETROCAMPUS.COM");println(); keyPressed(10_000); return; }

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
                else if ("t".equals(choice)) subThread = new ChatA1(getTerminalType());
                else if ("u".equals(choice)) subThread = new PrivateMessagesAscii();
                else if ("v".equals(choice)) subThread = new ElizaAscii();
                else if ("w".equals(choice)) subThread = new ChatGptAscii();
                else if ("x".equals(choice)) { showPatrons(); subThread = null; }
                else if ("y".equals(choice)) { wifiModem(); subThread = null; }
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
                .filter(StringUtils::isNotBlank)
                .map(StringUtils::trim)
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
        println("Once upon a a time, there where dial up");
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
