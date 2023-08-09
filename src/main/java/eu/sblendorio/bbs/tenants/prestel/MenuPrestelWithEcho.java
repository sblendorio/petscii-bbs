package eu.sblendorio.bbs.tenants.prestel;

import com.fasterxml.jackson.databind.JsonNode;
import com.maxmind.db.Reader;
import eu.sblendorio.bbs.core.*;
import eu.sblendorio.bbs.tenants.ascii.*;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static eu.sblendorio.bbs.core.Utils.*;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

public class MenuPrestelWithEcho extends PrestelThread {

    private byte[] clsBytes = new byte[] {12, 17};

    public MenuPrestelWithEcho() {
        super();
        setLocalEcho(true);
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
    private MenuApple1.GeoData geoData;

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

            geoData = new MenuApple1.GeoData(
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
        if (alternateLogo()) { println();println();println("Moved to BBS.RETROCAMPUS.COM");println(); keyPressed(10_000); return; }

        init();
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
                else if ("5".equals(choice)) subThread = new IndieRetroNewsAscii(io);
                else if ("6".equals(choice)) subThread = new VcfedAscii(io);
                else if ("7".equals(choice)) subThread = new The8BitGuyAscii(io);
                else if ("f".equals(choice)) subThread = new TelevideoRaiAscii(
                        rssPropertyTimeout(),
                        rssPropertyTimeoutDefault(),
                        getTerminalType(),
                        bytes(20, readBinaryFile("prestel/menu-televideo.cept3")),
                        bytes(11, 11, 13, 10, 32, 32, 32, 32, 32, 32, 13, 10, 11, 17)
                );
                else if ("g".equals(choice)) subThread = new LercioAscii(io);
                else if ("h".equals(choice)) subThread = new DisinformaticoAscii(io);
                else if ("i".equals(choice)) subThread = new MupinAscii(io);
                else if ("j".equals(choice)) subThread = new IlFattoQuotidianoAscii(io);
                else if ("k".equals(choice)) subThread = new AmedeoValorosoAscii(io);
                else if ("l".equals(choice)) subThread = new ButacAscii(io);
                else if ("m".equals(choice)) subThread = new AlessandroAlbanoAscii(io);
                else if ("n".equals(choice)) subThread = new TicTacToeAscii(io);
                else if ("o".equals(choice)) subThread = new Connect4Ascii();
                else if ("p".equals(choice)) subThread = new ZorkMachinePrestel("zmpp/zork1.z3");
                else if ("q".equals(choice)) subThread = new ZorkMachinePrestel("zmpp/zork2.z3");
                else if ("r".equals(choice)) subThread = new ZorkMachinePrestel("zmpp/zork3.z3");
                else if ("s".equals(choice)) subThread = new ZorkMachinePrestel("zmpp/hitchhiker-r60.z3");
                else if ("t".equals(choice) & !"prestel".equals(getTerminalType())) subThread = new ChatA1(io, getTerminalType());
                else if ("u".equals(choice)) subThread = new PrivateMessagesAscii(io);
                else if ("v".equals(choice)) subThread = new ElizaAscii(io);
                else if ("w".equals(choice)) subThread = new ChatGptAscii(io);
                else if ("x".equals(choice)) { showPatrons(); subThread = null; }
                else if ("y".equals(choice)) { wifiModem(); subThread = null; }
                else if ("z".equals(choice)) { textDemo(); subThread = null; }
                else {
                    validKey = false;
                    subThread = null;
                }
                if (subThread == null) continue;

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
            } while (!validKey);
        }
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
        write(readBinaryFile("prestel/menu-retrocampus.cept3"));
        /*
            String sp = (getScreenColumns() > 40) ? "                    " : "";
            banner();
            println("International News---"+ sp +" Game Room--------");
            println("1 - CNN News         "+ sp +" N - TIC TAC TOE");
            println("2 - BBC News         "+ sp +" O - Connect Four");
            println("3 - Politico.com");
            println("4 - Al Jazeera       "+ sp +" Services---------");
            println("5 - Indie Retro News "+ sp +" X - Patrons list");
            println("6 - VCF News         "+ sp +" Y - Wifi Modem");
            println("7 - The 8-Bit Guy    "+ sp +" Z - PrestelMuseum");
            println("                     ");
            println("Italian News---------");
            println("F - Televideo RAI    ");
            println("G - Lercio           ");
            println("H - Disinformatico   ");
            println("I - Mupin.it         ");
            println("J - Fatto Quotidiano ");
            println("K - Amedeo Valoroso  ");
            println("L - Butac.it         ");
            println("M - Alessandro Albano"+ sp +"        . - Logout");
            println();
        */
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
            i++;
        }
        cls();
    }

    public void showPatrons() throws Exception {
        List<String> patrons = readExternalTxt(System.getProperty("PATREON_LIST", System.getProperty("user.home") + File.separator + "patreon_list.txt"))
                .stream()
                .filter(StringUtils::isNotBlank)
                .map(StringUtils::trim)
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
