package eu.sblendorio.bbs.tenants.ascii;

import com.fasterxml.jackson.databind.JsonNode;
import com.maxmind.db.Reader;
import eu.sblendorio.bbs.core.AsciiThread;
import java.io.File;
import java.io.IOException;

public class Apple1Menu extends AsciiThread {

    public Apple1Menu() {
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
            println("BBS for Apple I - by F. Sblendorio 2020");
            println();
            println("Blog/News              Game Room");
            println("-----------------      ---------------");
            println("A - Televideo RAI      R - TIC TAC TOE");
            println("B - Wired Italia       S - CONNECT FOUR");
            println("C - Disinformatico     T - ZORK 1");
            println("D - Next Quotidiano    U - ZORK 2");
            println("E - Medbunker          V - ZORK 3");
            println("F - Sportal            Z - Hitchhiker's");
            println("G - Il Post            . - Logout");
            println("H - Fatto Quotidiano");
            println("I - Sys 64738");
            println("J - Retrocampus");
            println("K - Retroacademy");
            println("L - Butac.it");
            println("M - Pro-Test Italia");
            println("N - Facta.news");
            println("O - Indie Retro News");
            println("P - Dottore, ma e' vero che...");
            println("Q - Vintage Computer Federation");
 /*
            gotoXY(24, delta + 13); write(REVON); print(" X "); write(REVOFF); print(" TIC-TAC-TOE");
            gotoXY(24, delta + 14); write(REVON); print(" C "); write(REVOFF); print(" CONNECT-4");
            gotoXY(24, delta + 15); write(REVON); print(" F "); write(REVOFF); print(" MAGIC-15");
            gotoXY(26, delta + 6); write(REVON); print(" M "); write(REVOFF); print(" Messaggi");
  */
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
                else if (key == 'a') launch(new TelevideoRaiAscii());
                else if (key == 'b') launch(new WiredItaliaAscii());
                else if (key == 'c') launch(new DisinformaticoAscii());
                else if (key == 'd') launch(new NextQuotidianoAscii());
                else if (key == 'e') launch(new MedbunkerAscii());
                else if (key == 'f') launch(new SportalAscii());
                else if (key == 'g') launch(new IlPostAscii());
                else if (key == 'h') launch(new IlFattoQuotidianoAscii());
                else if (key == 'i') launch(new Sys64738Ascii());
                else if (key == 'j') launch(new RetroCampusAscii());
                else if (key == 'k') launch(new RetroAcademyAscii());
                else if (key == 'l') launch(new ButacAscii());
                else if (key == 'm') launch(new ProTestItaliaAscii());
                else if (key == 'n') launch(new FactaNewsAscii());
                else if (key == 'o') launch(new IndieRetroNewsAscii());
                else if (key == 'p') launch(new DottoreMaEVeroCheAscii());
                else if (key == 'q') launch(new VcfedAscii());
                else if (key == 'r') launch(new TicTacToeAscii());
                else if (key == 's') launch(new Connect4Ascii());
                else if (key == 't') launch(new ZorkMachineAscii("zmpp/zork1.z3"));
                else if (key == 'u') launch(new ZorkMachineAscii("zmpp/zork2.z3"));
                else if (key == 'v') launch(new ZorkMachineAscii("zmpp/zork3.z3"));
                else if (key == 'z') launch(new ZorkMachineAscii("zmpp/hitchhiker-r60.z3"));
                else validKey = false;
            } while (!validKey);
        }
    }
}