package eu.sblendorio.bbs.tenants.ascii;

import com.fasterxml.jackson.databind.JsonNode;
import com.maxmind.db.Reader;
import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.BbsThread;
import java.io.File;
import java.io.IOException;

public class MenuApple1 extends AsciiThread {

    public MenuApple1() {
        this(false);
    }

    public MenuApple1(boolean echo) {
        super();
        setLocalEcho(echo);
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

    @Override
    public void doLoop() throws Exception {
        init();
        logo();
        while (true) {
            log("Starting Apple1 / main menu");
            println();
            println();
            println();
            println();
            println();
            println("BBS for Apple I - by F. Sblendorio 2020");
            println();
            println("International News     Game Room");
            println("------------------     ---------------");
            println("A - CNN News           N - TIC TAC TOE");
            println("B - BBC News           O - Connect Four");
            println("C - Indie Retro News   P - Zork I");
            println("D - VCF News           Q - Zork II");
            println("E - The 8-Bit Guy      R - Zork III");
            println("                       S - Hitchhiker's");
            println("Italian News");
            println("-----------------");
            println("F - Televideo RAI");
            println("G - Wired Italia");
            println("H - Disinformatico");
            println("I - Il Post             Services");
            println("J - Fatto Quotidiano    ----------");
            println("K - Retrocampus         T - Chat");
            println("L - Butac.it            U - Private Msg");
            println("M - Facta.news          . - Logout");

             //final String line = geoData != null ? "Connected from "+geoData.city+", "+geoData.country : EMPTY;
            final String line = "(C) F. Sblendorio in 2018, 2019";

            flush();
            boolean validKey;
            do {
                validKey = true;
                log("Menu. Waiting for key pressed.");
                println();
                print("> ");
                resetInput(); int key = readKey();
                resetInput();
                key = Character.toLowerCase(key);
                log("Menu. Pressed: '" + (key == 13 || key == 10 ? "chr("+key+")" : ((char) key)) + "' (code=" + key + ")");
                BbsThread subThread;
                if (key == '.') {
                    newline();
                    newline();
                    println("Disconnected.");
                    return;
                }
                else if (key == 'a') subThread = new CnnAscii();
                else if (key == 'b') subThread = new BbcAscii();
                else if (key == 'c') subThread = new IndieRetroNewsAscii();
                else if (key == 'd') subThread = new VcfedAscii();
                else if (key == 'e') subThread = new The8BitGuyAscii();
                else if (key == 'f') subThread = new TelevideoRaiAscii();
                else if (key == 'g') subThread = new WiredItaliaAscii();
                else if (key == 'h') subThread = new DisinformaticoAscii();
                else if (key == 'i') subThread = new IlPostAscii();
                else if (key == 'j') subThread = new IlFattoQuotidianoAscii();
                else if (key == 'k') subThread = new RetroCampusAscii();
                else if (key == 'l') subThread = new ButacAscii();
                else if (key == 'm') subThread = new FactaNewsAscii();
                else if (key == 'n') subThread = new TicTacToeAscii();
                else if (key == 'o') subThread = new Connect4Ascii();
                else if (key == 'p') subThread = new ZorkMachineAscii("zmpp/zork1.z3");
                else if (key == 'q') subThread = new ZorkMachineAscii("zmpp/zork2.z3");
                else if (key == 'r') subThread = new ZorkMachineAscii("zmpp/zork3.z3");
                else if (key == 's') subThread = new ZorkMachineAscii("zmpp/hitchhiker-r60.z3");
                else if (key == 't') subThread = new ChatA1();
                else if (key == 'u') subThread = new PrivateMessagesAscii();
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
                    launch(subThread);
                }
            } while (!validKey);
        }
    }
}