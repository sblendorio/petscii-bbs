
package eu.sblendorio.bbs.tenants;

import com.fasterxml.jackson.databind.JsonNode;
import com.maxmind.db.Reader;
import static eu.sblendorio.bbs.core.Colors.*;
import static eu.sblendorio.bbs.core.Keys.CASE_LOCK;
import static eu.sblendorio.bbs.core.Keys.CLR;
import static eu.sblendorio.bbs.core.Keys.HOME;
import static eu.sblendorio.bbs.core.Keys.LOWERCASE;
import static eu.sblendorio.bbs.core.Keys.REVOFF;
import static eu.sblendorio.bbs.core.Keys.REVON;
import eu.sblendorio.bbs.core.PetsciiThread;
import java.io.File;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;

public class MainMenu extends PetsciiThread {

    public static class GeoData {
        public final String city;
        public final String cityGeonameId;
        public final String country;
        public final Double latitude;
        public final Double longitude;
        public final String timeZone;

        public GeoData(final String city, final String cityGeonameId, final String country, final Double latitude,
                       final Double longitude, final String timeZone) {
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

    @Override
    public void doLoop() throws Exception {
        init();
        while (true) {
            int delta = 0;
            write(CLR, LOWERCASE, CASE_LOCK, HOME);
            log("Starting Main Menu BBS");
            drawLogo();

            gotoXY(21, delta + 4);
            write(CYAN);
            print("News");
            write(GREY3);
            gotoXY(21, delta + 5);
            write(LIGHT_BLUE);
            print(StringUtils.repeat(chr(163), 4));
            write(GREY3);
            gotoXY(5, delta + 4);
            write(REVON);
            print(" 1 ");
            write(REVOFF);
            print(" Wired");
            gotoXY(5, delta + 5);
            write(REVON);
            print(" 2 ");
            write(REVOFF);
            print(" MedBunker");
            gotoXY(5, delta + 6);
            write(REVON);
            print(" 3 ");
            write(REVOFF);
            print(" Next Quotidiano");
            gotoXY(5, delta + 7);
            write(REVON);
            print(" 4 ");
            write(REVOFF);
            print(" Disinformatico");
            gotoXY(5, delta + 8);
            write(REVON);
            print(" 5 ");
            write(REVOFF);
            print(" Fatto Quotidiano");
            gotoXY(5, delta + 9);
            write(REVON);
            print(" 6 ");
            write(REVOFF);
            print(" IndieRetroNews");
            gotoXY(5, delta + 10);
            write(REVON);
            print(" 7 ");
            write(REVOFF);
            print(" Retrocampus");
            gotoXY(5, delta + 11);
            write(REVON);
            print(" 8 ");
            write(REVOFF);
            print(" News FNOMCeO");
            gotoXY(5, delta + 12);
            write(REVON);
            print(" 9 ");
            write(REVOFF);
            print(" Il Post");
            gotoXY(5, delta + 13);
            write(REVON);
            print(" 0 ");
            write(REVOFF);
            print(" Puntoinformatico");
            gotoXY(5, delta + 14);
            write(REVON);
            print(" W ");
            write(REVOFF);
            print(" Archeologia Informatica");
            gotoXY(5, delta + 15);
            write(REVON);
            print(" B ");
            write(REVOFF);
            print(" Medical facts");
            gotoXY(5, delta + 16);
            write(REVON);
            print(" J ");
            write(REVOFF);
            print(" Bufale.net");
            gotoXY(5, delta + 17);
            write(REVON);
            print(" Q ");
            write(REVOFF);
            print(" Queryonline");
            gotoXY(5, delta + 18);
            write(REVON);
            print(" N ");
            write(REVOFF);
            print(" Facta");
            gotoXY(5, delta + 19);
            write(REVON);
            print(" U ");
            write(REVOFF);
            print(" Butac");

            gotoXY(34, delta + 14);
            write(CYAN);
            print("Games");
            write(GREY3);
            gotoXY(34, delta + 15);
            write(LIGHT_BLUE);
            print(StringUtils.repeat(chr(163), 5));
            write(GREY3);
            gotoXY(24, delta + 16);
            write(REVON);
            print(" E ");
            write(REVOFF);
            print(" TIC-TAC-TOE");
            gotoXY(24, delta + 17);
            write(REVON);
            print(" C ");
            write(REVOFF);
            print(" CONNECT-4");
            gotoXY(24, delta + 18);
            write(REVON);
            print(" F ");
            write(REVOFF);
            print(" MAGIC-15");
            gotoXY(24, delta + 19);
            write(REVON);
            print(" X ");
            write(REVOFF);
            print(" Zork I");
            gotoXY(24, delta + 20);
            write(REVON);
            print(" Y ");
            write(REVOFF);
            print(" Zork II");
            gotoXY(24, delta + 21);
            write(REVON);
            print(" Z ");
            write(REVOFF);
            print(" Zork III");
            gotoXY(24, delta + 22);
            write(REVON);
            print(" R ");
            write(REVOFF);
            print(" Hitchhikers");

            gotoXY(16, delta + 19);
            write(CYAN);
            print("Misc");
            write(GREY3);
            gotoXY(16, delta + 20);
            write(LIGHT_BLUE);
            print(StringUtils.repeat(chr(163), 4));
            write(GREY3);
            gotoXY(4, delta + 21);
            write(REVON);
            print(" S ");
            write(REVOFF);
            print(" Sportal.IT");
            gotoXY(4, delta + 22);
            write(REVON);
            print(" L ");
            write(REVOFF);
            print(" Le ossa");
            gotoXY(4, delta + 23);
            write(REVON);
            print(" P ");
            write(REVOFF);
            print(" PETSCII Art");
            gotoXY(4, delta + 24);
            write(REVON);
            print(" K ");
            write(REVOFF);
            print(" CSDb SD2IEC");

            gotoXY(32, delta + 4);
            write(CYAN);
            print("Servizi");
            write(GREY3);
            gotoXY(32, delta + 5);
            write(LIGHT_BLUE);
            print(StringUtils.repeat(chr(163), 7));
            write(GREY3);
            gotoXY(26, delta + 6);
            write(REVON);
            print(" M ");
            write(REVOFF);
            print(" Messaggi");
            gotoXY(26, delta + 7);
            write(REVON);
            print(" T ");
            write(REVOFF);
            print(" Televideo");
            gotoXY(26, delta + 8);
            write(REVON);
            print(" D ");
            write(REVOFF);
            print(" CSDb");
            gotoXY(26, delta + 9);
            write(REVON);
            print(" A ");
            write(REVOFF);
            print(" Arnold 64");
            gotoXY(26, delta + 10);
            write(REVON);
            print(" I ");
            write(REVOFF);
            print(" Internet");
            gotoXY(26, delta + 11);
            write(REVON);
            print(" H ");
            write(REVOFF);
            print(" Chat");
            gotoXY(26, delta + 12);
            write(REVON);
            print(" . ");
            write(REVOFF);
            print(" Logoff");

            //final String line = geoData != null ? "Connected from "+geoData.city+", "+geoData.country : EMPTY;
            gotoXY(22, 24);
            write(GREY2);
            print("(C) F. Sblendorio");
            write(GREY3);
            flush();
            boolean validKey;
            do {
                validKey = true;
                log("Menu. Waiting for key pressed.");
                resetInput();
                int key = readKey();
                if (key >= 193 && key <= 218) {
                    key -= 96;
                }
                key = Character.toLowerCase(key);
                log("Menu. Pressed: '" + (key < 32 || key > 127 ? "chr(" + key + ")" : ((char) key)) + "' (code=" +
                    key + ")");
                if (key == '.') {
                    newline();
                    newline();
                    println("Disconnected.");
                    return;
                } else if (key == '1') {
                    launch(new WiredItalia());
                } else if (key == '2') {
                    launch(new Medbunker());
                } else if (key == '3') {
                    launch(new NextQuotidiano());
                } else if (key == '4') {
                    launch(new Disinformatico());
                } else if (key == '5') {
                    launch(new IlFattoQuotidiano());
                } else if (key == '6') {
                    launch(new IndieRetroNews());
                } else if (key == '7') {
                    launch(new RetroCampus());
                } else if (key == '8') {
                    launch(new DottoreMaEVeroChe());
                } else if (key == '9') {
                    launch(new IlPost());
                } else if (key == '0') {
                    launch(new PuntoInformatico());
                } else if (key == 'w') {
                    launch(new ArcheologiaInformatica());
                } else if (key == 'b') {
                    launch(new MedicalFacts());
                } else if (key == 'j') {
                    launch(new BufaleNet());
                } else if (key == 'q') {
                    launch(new QueryOnline());
                } else if (key == 'n') {
                    launch(new FactaNews());
                } else if (key == 'u') {
                    launch(new Butac());
                } else if (key == 'e') {
                    launch(new TicTacToe());
                } else if (key == 'c') {
                    launch(new ConnectFour());
                } else if (key == 'f') {
                    launch(new Magic15());
                } else if (key == 's') {
                    launch(new Sportal());
                } else if (key == 'l') {
                    launch(new Ossa());
                } else if (key == 'p') {
                    launch(new PetsciiArtGallery());
                } else if (key == 'm') {
                    launch(new UserLogon());
                } else if (key == 't') {
                    launch(new TelevideoRai());
                } else if (key == 'd') {
                    launch(new CsdbReleases());
                } else if (key == 'a') {
                    launch(new ArnoldC64());
                } else if (key == 'i') {
                    launch(new InternetBrowser());
                } else if (key == 'h') {
                    launch(new Chat());
                } else if (key == 'k') {
                    launch(new CsdbReleasesSD2IEC());
                } else if (key == 'x') {
                    launch(new ZorkMachine("zmpp/zork1.z3"));
                } else if (key == 'y') {
                    launch(new ZorkMachine("zmpp/zork2.z3"));
                } else if (key == 'z') {
                    launch(new ZorkMachine("zmpp/zork3.z3"));
                } else if (key == 'r') {
                    launch(new ZorkMachine("zmpp/hitchhiker-r60.z3"));
                } else {
                    validKey = false;
                }
            } while (!validKey);
        }
    }

    public void drawLogo() {
        write(LOGO_BYTES);
    }

    private static final byte[] LOGO_BYTES = new byte[] {
        18, 5, -84, -94, -69, -84, -94, -69, -94, -94, -69, -94, -94, 32, -94, -94,
        -69, -110, 32, -94, -69, 32, -84, 32, -84, -94, -94, -94, -94, -84, -94, 32,
        -69, 32, -69, -84, -94, -69, 13, -95, 18, -94, -110, -68, -95, 18, -94, 32,
        32, -110, -68, 18, 32, -110, -68, -66, 18, -69, -95, -110, -95, 18, -95, -95,
        -110, 32, -68, -84, -66, -65, 32, -95, 18, -95, -110, 32, -95, 18, -95, -110,
        32, -95, -95, 32, -95, -65, -94, 13, -95, 18, -95, -110, -95, -95, -68, 18,
        -69, 32, -110, 32, 18, 32, -110, 32, 18, 32, -95, -110, -68, 18, -94, -95,
        -110, -68, -94, 18, -65, -95, -94, -69, -110, 32, -95, 18, -95, -110, 32, -95,
        18, -95, -94, -110, 32, -65, -94, -66, -94, -94, -66, -101, 46, -61, -49, -51,
        13, 18, 5, -94, -94, -94, -94, -94, -94, -94, -94, -94, -94, -94, -94, -94,
        -94, -94, -110, 13, 18, -97, 32, -94, -68, -110, 13, 18, 32, -110, -94, 18,
        -84, -110, 13, 18, 32, -110, 32, 18, 32, -110, 13, 18, -94, -94, -110, -66,
        13, 18, -102, 32, -94, -68, -110, 13, 18, 32, -110, -94, 18, -84, -110, 13,
        18, 32, -110, 32, 18, 32, -110, 13, 18, -94, -94, -110, -66, 13, 18, 31,
        -66, -94, -68, -110, 13, 18, -69, -110, -94, -69, 13, -94, 32, 18, 32, -110,
        13, -68, 18, -94, -110, -66, 13
    };
}
