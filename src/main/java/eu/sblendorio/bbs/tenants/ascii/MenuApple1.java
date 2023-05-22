package eu.sblendorio.bbs.tenants.ascii;

import com.fasterxml.jackson.databind.JsonNode;
import com.maxmind.db.Reader;
import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.BbsThread;
import static eu.sblendorio.bbs.core.Utils.STR_ALPHANUMERIC;
import static eu.sblendorio.bbs.core.Utils.setOfChars;
import static java.nio.charset.StandardCharsets.UTF_8;
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
                print("> ");
                resetInput();
                String choice = readChoice();
                resetInput();
                choice = StringUtils.lowerCase(choice);
                log("Menu. Choice = "+ choice);
                BbsThread subThread;
                if (".".equals(choice)) {
                    newline();
                    newline();
                    println("Disconnected.");
                    return;
                }
                else if ("a".equals(choice)) subThread = new CnnAscii(rssPropertyTimeout(), rssPropertyTimeoutDefault(), getCharset());
                else if ("b".equals(choice)) subThread = new BbcAscii(rssPropertyTimeout(), rssPropertyTimeoutDefault(), getCharset());
                else if ("c".equals(choice)) subThread = new IndieRetroNewsAscii();
                else if ("d".equals(choice)) subThread = new VcfedAscii();
                else if ("e".equals(choice)) subThread = new The8BitGuyAscii();
                else if ("f".equals(choice)) subThread = new TelevideoRaiAscii(rssPropertyTimeout(), rssPropertyTimeoutDefault(), getCharset());
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
                else if ("t".equals(choice)) subThread = new ChatA1(getCharset());
                else if ("u".equals(choice)) subThread = new PrivateMessagesAscii();
                else if ("v".equals(choice)) subThread = new ElizaAscii();
                else if ("w".equals(choice) && !alternateLogo()) subThread = new ChatGptAscii();
                else if ("x".equals(choice) && !alternateLogo()) { showPatrons(); subThread = null; }
                else if ("y".equals(choice) && !alternateLogo()) { wifiModem(); subThread = null; }
                else if ("z".equals(choice) && !alternateLogo()) { apple1Demo(); subThread = null; }
                else {
                    validKey = false;
                    subThread = null;
                }
                if (subThread != null) {
                    if (subThread instanceof AsciiThread) {
                        ((AsciiThread) subThread).clsBytes = this.clsBytes;
                        ((AsciiThread) subThread).screenColumns = this.screenColumns;
                        ((AsciiThread) subThread).screenRows = this.screenRows;
                    }
                    if (subThread instanceof WordpressProxyAscii && (screenRows == 15 || screenColumns < 40)) {
                        ((WordpressProxyAscii) subThread).pageSize /= 2;
                    } else if (subThread instanceof GoogleBloggerProxyAscii && (screenRows == 15 || screenColumns < 40)) {
                        ((GoogleBloggerProxyAscii) subThread).pageSize /= 2;
                    } else if (subThread instanceof WordpressProxyAscii && screenColumns == 80) {
                        ((WordpressProxyAscii) subThread).pageSize *= 2;
                    } else
                    if (subThread instanceof GoogleBloggerProxyAscii && screenColumns == 80) {
                        ((GoogleBloggerProxyAscii) subThread).pageSize *= 2;
                    }
                    launch(subThread);
                }
            } while (!validKey);
        }
    }

    public void displayMenu() {
        String sp = (getScreenColumns() > 40) ? "                    " : "";
        banner();
        println("International News   "+ sp +"  Game Room");
        println("------------------   "+ sp +"  ---------------");
        println("A - CNN News         "+ sp +"  N - TIC TAC TOE");
        println("B - BBC News         "+ sp +"  O - Connect Four");
        println("C - Indie Retro News "+ sp +"  P - Zork I");
        println("D - VCF News         "+ sp +"  Q - Zork II");
        println("E - The 8-Bit Guy    "+ sp +"  R - Zork III");
        println("                     "+ sp +"  S - Hitchhiker's");
        println("Italian News");
        println("-----------------    "+ sp +"  Services-------");
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
        patrons.forEach(this::println);
        println();
        print("Press any key.");
        flush(); resetInput(); readKey();
    }

    public void apple1Demo() throws Exception {
        List<Path> drawings = Utils.getDirContent("apple1/demo30th");
        for (Path drawing : drawings) {
            String filename = drawing.toString();
            if (startsWith(filename,"/")) filename = filename.substring(1);
            final String content = new String(readBinaryFile(filename), UTF_8);
            cls();
            for (String row: content.split("\n")) {
                println();
                print(row);
            }
            flush(); resetInput();
            int ch = keyPressed(60_000);
            if (ch == '.') return;
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

    private List<String> readTxt(String filename) {
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


}
