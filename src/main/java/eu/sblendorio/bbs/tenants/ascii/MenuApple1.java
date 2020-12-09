package eu.sblendorio.bbs.tenants.ascii;

import com.fasterxml.jackson.databind.JsonNode;
import com.maxmind.db.Reader;
import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.tenants.petscii.The8BitGuy;
import java.io.File;
import java.io.IOException;

public class MenuApple1 extends AsciiThread {

    public MenuApple1() {
        setLocalEcho(false);
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
                key = Character.toLowerCase(key);
                log("Menu. Pressed: '" + (key == 13 || key == 10 ? "chr("+key+")" : ((char) key)) + "' (code=" + key + ")");
                if (key == '.') {
                    newline();
                    newline();
                    println("Disconnected.");
                    return;
                }
                else if (key == 'a') launch(new CnnAscii());
                else if (key == 'b') launch(new BbcAscii());
                else if (key == 'c') launch(new IndieRetroNewsAscii());
                else if (key == 'd') launch(new VcfedAscii());
                else if (key == 'e') launch(new The8BitGuyAscii());
                else if (key == 'f') launch(new TelevideoRaiAscii());
                else if (key == 'g') launch(new WiredItaliaAscii());
                else if (key == 'h') launch(new DisinformaticoAscii());
                else if (key == 'i') launch(new IlPostAscii());
                else if (key == 'j') launch(new IlFattoQuotidianoAscii());
                else if (key == 'k') launch(new RetroCampusAscii());
                else if (key == 'l') launch(new ButacAscii());
                else if (key == 'm') launch(new FactaNewsAscii());
                else if (key == 'n') launch(new TicTacToeAscii());
                else if (key == 'o') launch(new Connect4Ascii());
                else if (key == 'p') launch(new ZorkMachineAscii("zmpp/zork1.z3"));
                else if (key == 'q') launch(new ZorkMachineAscii("zmpp/zork2.z3"));
                else if (key == 'r') launch(new ZorkMachineAscii("zmpp/zork3.z3"));
                else if (key == 's') launch(new ZorkMachineAscii("zmpp/hitchhiker-r60.z3"));
                else if (key == 't') launch(new ChatA1());
                else if (key == 'u') launch(new PrivateMessagesAscii());
                else validKey = false;
            } while (!validKey);
        }
    }
}