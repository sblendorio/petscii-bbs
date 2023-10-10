package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.Utils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.List;

import static eu.sblendorio.bbs.core.Utils.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public class MenuApple1 extends AsciiThread {

    public MenuApple1() {
        this(false);
    }

    public MenuApple1(boolean echo) {
        super();
        setLocalEcho(echo);
    }

    public void logo() throws Exception {
        readTextFile("apple1/intro-menu.txt").forEach(this::println);
        flush();
    }

    public String rssPropertyTimeout() { return "rss.a1.timeout"; }

    public String rssPropertyTimeoutDefault() { return "40000"; }

    protected void banner() {
        println("BBS for Apple-1 - by F. Sblendorio " + Calendar.getInstance().get(Calendar.YEAR));
        println();
    }

    @Override
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
                else if ("t".equals(choice)) subThread = new ChatA1(getTerminalType());
                else if ("u".equals(choice)) subThread = new PrivateMessagesAscii();
                else if ("v".equals(choice)) subThread = new ElizaAscii();
                else if ("w".equals(choice)) subThread = new ClientChatGptAscii();
                else if ("x".equals(choice)) { showPatrons(); subThread = null; }
                else if ("y".equals(choice)) { wifiModem(); subThread = null; }
                else if ("z".equals(choice)) { textDemo(); subThread = null; }
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
                if (subThread instanceof WordpressProxyAscii && screenColumns == 80) {
                    ((WordpressProxyAscii) subThread).pageSize *= 2;
                } else if (subThread instanceof GoogleBloggerProxyAscii && screenColumns == 80) {
                    ((GoogleBloggerProxyAscii) subThread).pageSize *= 2;
                } else if (subThread instanceof OneRssAscii && screenColumns == 80) {
                    ((OneRssAscii) subThread).pageSize *= 2;
                }
                launch(subThread);
            } while (!validKey);
        }
    }

    public void displayMenu() throws Exception {
        String sp = (getScreenColumns() > 40) ? "                    " : "";
        banner();
        println("International News---"+ sp +"  Game Room");
        println("1 - CNN News         "+ sp +"  ---------------");
        println("2 - BBC News         "+ sp +"  N - TIC TAC TOE");
        println("3 - Politico.com     "+ sp +"  O - Connect Four");
        println("4 - Al Jazeera       "+ sp +"  P - Zork I");
        println("5 - Indie Retro News "+ sp +"  Q - Zork II");
        println("6 - VCF News         "+ sp +"  R - Zork III");
        println("7 - The 8-Bit Guy    "+ sp +"  S - Hitchhiker's");
        println();
        println("Italian News---------"+ sp +"  Services-------");
        println("F - Televideo RAI    "+ sp +"  T - Chat");
        println("G - Lercio           "+ sp +"  U - Private Msg");
        println("H - Disinformatico   "+ sp +"  V - Eliza");
        println("I - Mupin.it         "+ sp +"  W - Chat GPT");
        println("J - Fatto Quotidiano "+ sp +"  X - Patrons list");
        println("K - Amedeo Valoroso  "+ sp +"  Y - Wifi Modem");
        println("L - Butac.it         "+ sp +"  Z - Apple-1 Demo");
        println("M - Alessandro Albano"+ sp +"  . - Logout");
        println();
    }

    public String readChoice() throws IOException {
        return readLine(setOfChars(STR_ALPHANUMERIC, "."));
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

    public int readSingleKey() throws IOException {
        return readKey();
    }

}
