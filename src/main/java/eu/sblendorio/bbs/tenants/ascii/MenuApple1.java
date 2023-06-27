package eu.sblendorio.bbs.tenants.ascii;

import com.fasterxml.jackson.databind.JsonNode;
import com.maxmind.db.Reader;
import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.BbsThread;

import static eu.sblendorio.bbs.core.Utils.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import eu.sblendorio.bbs.core.Utils;
import org.apache.commons.lang3.StringUtils;

public class MenuApple1 extends AsciiThread {

    public MenuApple1() {
        this(false);
    }

    public MenuApple1(boolean echo) {
        super();
        setLocalEcho(echo);

    }

    public String getCharset() {
        return "ascii";
    }

    public static class GeoData {
        public final String city;
        public final String cityGeonameId;
        public final String country;
        public final Double latitude;
        public final Double longitude;
        public final String timeZone;
        public GeoData(final String city, final String cityGeonameId, final String country, final Double latitude, final Double longitude, final String timeZone) {
            this.city = city;
            this.cityGeonameId = cityGeonameId;
            this.country = country;
            this.latitude = latitude;
            this.longitude = longitude;
            this.timeZone = timeZone;
        }
    }

    private static final String MAXMIND_DB = System.getProperty("user.home") + File.separator + "GeoLite2-City.mmdb";
    private Reader maxmindReader;
    private JsonNode maxmindResponse;
    private GeoData geoData;

    private static final String IP_FOR_ALTERNATE_LOGO = System.getProperty("alternate.logo.ip", "none");
    private static final int PORT_FOR_ALTERNATE_LOGO = toInt(System.getProperty("alternate.logo.port", "-1"));
    public boolean alternateLogo() {
        return IP_FOR_ALTERNATE_LOGO.equals(serverAddress.getHostAddress())
                || serverPort == PORT_FOR_ALTERNATE_LOGO;
    }

    public void init() throws IOException {
        try {
            File maxmindDb = new File(MAXMIND_DB);
            maxmindReader = new Reader(maxmindDb);
            maxmindResponse = maxmindReader.get(socket.getInetAddress());
            maxmindReader.close();

            geoData = new GeoData(
                maxmindResponse.get("city").get("names").get("en").asText(),
                maxmindResponse.get("city").get("geoname_id").asText(),
                maxmindResponse.get("country").get("names").get("en").asText(),
                maxmindResponse.get("location").get("latitude").asDouble(),
                maxmindResponse.get("location").get("longitude").asDouble(),
                maxmindResponse.get("location").get("time_zone").asText()
            );
            log("Location: " + geoData.city + ", " + geoData.country);
        } catch (Exception e) {
            maxmindResponse = null;
            geoData = null;
            log("Error retrieving GeoIP data: " + e.getClass().getName());
        }
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
        if (alternateLogo()) { println();println();println("Moved to BBS.RETROCAMPUS.COM");println(); keyPressed(10_000); return; }

        init();
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
                if ("minitel".equals(getCharset()) || "prestel".equals(getCharset())) {
                    int key = readSingleKey();
                    choice = String.valueOf((char) key);
                } else {
                    print("> ");
                    choice = readChoice();
                }
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
                        getCharset(),
                        "prestel".equals(getCharset()) ? bytes(readBinaryFile("prestel/cnn_home.cept3"),13,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,17) :
                        "XXXminitel".equals(getCharset()) ? readBinaryFile("minitel/cnn_home.vdt") : null,

                        "prestel".equals(getCharset()) ? bytes(11, 11, 13, 10, 32, 32, 32, 32, 32, 32, 13, 10, 11) :
                        "XXXminitel".equals(getCharset()) ? bytes(31, 64+23, 64+1, 32, 32, 32, 32, 32, 32, 31, 64+23, 64+1) : null
                );
                else if ("2".equals(choice)) subThread = new BbcAscii(
                        rssPropertyTimeout(),
                        rssPropertyTimeoutDefault(),
                        getCharset(),
                        "prestel".equals(getCharset()) ? bytes(readBinaryFile("prestel/bbc_home.cept3"),13,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,17) :
                        "XXXminitel".equals(getCharset()) ? readBinaryFile("minitel/bbc_home.vdt") : null,

                        "prestel".equals(getCharset()) ? bytes(11, 11, 13, 10, 32, 32, 32, 32, 32, 32, 13, 10, 11) :
                        "XXXminitel".equals(getCharset()) ? bytes(31, 64+23, 64+1, 32, 32, 32, 32, 32, 32, 31, 64+23, 64+1) : null
                );
                else if ("3".equals(choice)) subThread = new OneRssPoliticoAscii();
                else if ("4".equals(choice)) subThread = new OneRssAJPlusAscii();
                else if ("5".equals(choice)) subThread = new IndieRetroNewsAscii();
                else if ("6".equals(choice)) subThread = new VcfedAscii();
                else if ("7".equals(choice)) subThread = new The8BitGuyAscii();
                else if ("f".equals(choice)) subThread = new TelevideoRaiAscii(
                        rssPropertyTimeout(),
                        rssPropertyTimeoutDefault(),
                        getCharset(),
                            "prestel".equals(getCharset()) ? readBinaryFile("prestel/menu-televideo.cept3") :
                            "minitel".equals(getCharset()) ? readBinaryFile("minitel/menu-televideo.vdt") : null,

                            "prestel".equals(getCharset()) ? bytes(11, 11, 13, 10, 32, 32, 32, 32, 32, 32, 13, 10, 11) :
                            "minitel".equals(getCharset()) ? bytes(31, 64+23, 64+1, 32, 32, 32, 32, 32, 32, 31, 64+23, 64+1) : null
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
                else if ("p".equals(choice) & !"prestel".equals(getCharset())) subThread = new ZorkMachineAscii("zmpp/zork1.z3");
                else if ("q".equals(choice) & !"prestel".equals(getCharset())) subThread = new ZorkMachineAscii("zmpp/zork2.z3");
                else if ("r".equals(choice) & !"prestel".equals(getCharset())) subThread = new ZorkMachineAscii("zmpp/zork3.z3");
                else if ("s".equals(choice) & !"prestel".equals(getCharset())) subThread = new ZorkMachineAscii("zmpp/hitchhiker-r60.z3");
                else if ("t".equals(choice) & !"prestel".equals(getCharset())) subThread = new ChatA1(getCharset());
                else if ("u".equals(choice) & !"prestel".equals(getCharset())) subThread = new PrivateMessagesAscii();
                else if ("v".equals(choice) & !"prestel".equals(getCharset())) subThread = new ElizaAscii();
                else if ("w".equals(choice) & !"prestel".equals(getCharset())) subThread = new ChatGptAscii();
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
                if (subThread instanceof WordpressProxyAscii && (screenRows == 15 || screenColumns < 40)) {
                    ((WordpressProxyAscii) subThread).pageSize /= 2;
                } else if (subThread instanceof GoogleBloggerProxyAscii && (screenRows == 15 || screenColumns < 40)) {
                    ((GoogleBloggerProxyAscii) subThread).pageSize /= 2;
                } else if (subThread instanceof OneRssAscii && (screenRows == 15 || screenColumns < 40)) {
                    ((OneRssAscii) subThread).pageSize /= 2;
                } else if (subThread instanceof WordpressProxyAscii && screenColumns == 80) {
                    ((WordpressProxyAscii) subThread).pageSize *= 2;
                } else if (subThread instanceof GoogleBloggerProxyAscii && screenColumns == 80) {
                    ((GoogleBloggerProxyAscii) subThread).pageSize *= 2;
                } else if (subThread instanceof OneRssAscii && screenColumns == 80) {
                    ((OneRssAscii) subThread).pageSize *= 2;
                }
                if (subThread != null && "prestel".equals(getCharset())) {
                    subThread.keepAliveChar = 17; // 17 = cursor on
                }
                if (subThread != null && subThread instanceof DisinformaticoAscii && "prestel".equals(getCharset())) {
                    ((DisinformaticoAscii) subThread).pageSize = 4;
                }
                launch(subThread);
            } while (!validKey);
        }
    }

    public void displayMenu() {
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
        println("I - Mupin.it         "+ sp + (alternateLogo() ? "" : "  W - Chat GPT"));
        println("J - Fatto Quotidiano "+ sp + (alternateLogo() ? "" : "  X - Patrons list"));
        println("K - Amedeo Valoroso  "+ sp + (alternateLogo() ? "" : "  Y - Wifi Modem"));
        println("L - Butac.it         "+ sp +"  Z - Apple-1 Demo");
        println("M - Alessandro Albano"+ sp +"  . - Logout");
        println();
    }

    public String readChoice() throws IOException {
        return readLine(setOfChars(STR_ALPHANUMERIC, "."));
    }

    public void showPatrons() throws Exception {
        List<String> patrons = readTxt(System.getProperty("PATREON_LIST", System.getProperty("user.home") + File.separator + "patreon_list.txt"))
                .stream()
                .filter(StringUtils::isNotBlank)
                .map(StringUtils::trim)
                .sorted()
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
            if (ch == '.') return;
            println();
        }
        println();
        print("Press any key.");
        flush(); resetInput(); readKey();
    }

    public void textDemo() throws Exception {
        List<Path> drawings = Utils.getDirContent("apple1/demo30th");
        cls();
        for (Path drawing : drawings.stream().sorted(comparing(p -> p.toString().toLowerCase())).collect(toList())) {
            String filename = drawing.toString();
            if (startsWith(filename,"/")) filename = filename.substring(1);
            final String content = new String(readBinaryFile(filename), UTF_8);
            boolean firstRow = true;
            for (String row: content.split("\n")) {
                if (!firstRow) println();
                firstRow = false;
                print(row);
            }
            flush(); resetInput();
            int ch = keyPressed(60_000);
            if (ch == '.') break;
            println();
            println();
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

    public List<String> readTxt(String filename) {
        List<String> result = new LinkedList<>();
        try {
            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                result.add(myReader.nextLine());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return result;
    }

    public int readSingleKey() throws IOException {
        return readKey();
    }

}
