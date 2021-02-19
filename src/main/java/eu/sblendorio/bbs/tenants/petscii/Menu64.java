package eu.sblendorio.bbs.tenants.petscii;

import com.fasterxml.jackson.databind.JsonNode;
import com.maxmind.db.Reader;
import static eu.sblendorio.bbs.core.PetsciiColors.BLUE;
import static eu.sblendorio.bbs.core.PetsciiColors.CYAN;
import static eu.sblendorio.bbs.core.PetsciiColors.GREEN;
import static eu.sblendorio.bbs.core.PetsciiColors.GREY3;
import static eu.sblendorio.bbs.core.PetsciiColors.LIGHT_BLUE;
import static eu.sblendorio.bbs.core.PetsciiColors.LIGHT_GREEN;
import static eu.sblendorio.bbs.core.PetsciiColors.RED;
import static eu.sblendorio.bbs.core.PetsciiColors.WHITE;
import static eu.sblendorio.bbs.core.PetsciiKeys.CASE_LOCK;
import static eu.sblendorio.bbs.core.PetsciiKeys.CLR;
import static eu.sblendorio.bbs.core.PetsciiKeys.HOME;
import static eu.sblendorio.bbs.core.PetsciiKeys.LOWERCASE;
import static eu.sblendorio.bbs.core.PetsciiKeys.REVOFF;
import static eu.sblendorio.bbs.core.PetsciiKeys.REVON;
import static eu.sblendorio.bbs.core.PetsciiKeys.RIGHT;
import eu.sblendorio.bbs.core.PetsciiThread;
import java.io.File;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

public class Menu64 extends PetsciiThread {

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
    private static final String IP_FOR_ALTERNATE_LOGO = System.getProperty("alternate.logo.ip", "none");
    private static final int PORT_FOR_ALTERNATE_LOGO = toInt(System.getProperty("alternate.logo.port", "-1"));
    private Reader maxmindReader;
    private JsonNode maxmindResponse;
    private GeoData geoData;

    public void init() {
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
            write(CLR, LOWERCASE, CASE_LOCK, HOME);
            log("Starting Main Menu BBS");
            write(readBinaryFile("petscii/bbs-menu-main.seq"));
            write(HOME);
            drawLogo();
            write(GREY3);
            gotoXY(39,23);

            //final String line = geoData != null ? "Connected from "+geoData.city+", "+geoData.country : EMPTY;
            flush();
            boolean validKey;
            do {
                validKey = true;
                log("Menu. Waiting for key pressed.");
                resetInput();
                int key = readKey();
                resetInput();
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
                }
                else if (key == '1') menuNewsIta();
                else if (key == '2') menuNewsEng();
                else if (key == '3') menuGames();
                else if (key == '4') menuDownloads();
                else if (key == '5') launch(new Chat64());
                else if (key == '6') launch(new UserLogon());
                else if (key == '7') launch(new InternetBrowser());
                else if (key == '8') launch(new PetsciiArtGallery());
                else if (key == '9') launch(new Ossa());
                else if (key == '0') about();
                else {
                    validKey = false;
                }
            } while (!validKey);
        }
    }

    public void menuNewsIta() throws Exception {
        write(CLR, LOWERCASE, CASE_LOCK, HOME);
        drawLogo();
        write(GREY3);
        gotoXY(4,4);
        write(REVON, GREEN, 161, WHITE, ' ', RED, REVOFF, 161, CYAN); println("Italian News");
        write(RIGHT, RIGHT, RIGHT, ' ', ' ', ' ', ' ', LIGHT_BLUE); println(repeat((char) 163, 12));
        write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '1', REVOFF, 161); print("Televideo        "); write(REVON, 161, 'i', REVOFF, 161); println("Attivissimo");
        write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '2', REVOFF, 161); print("Il Post          "); write(REVON, 161, 'j', REVOFF, 161); println("Bufale.net");
        write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '3', REVOFF, 161); print("Valigia Blu      "); write(REVON, 161, 'k', REVOFF, 161); println("Butac");
        write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '4', REVOFF, 161); print("Linkiesta        "); write(REVON, 161, 'l', REVOFF, 161); println("Facta news");
        write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '5', REVOFF, 161); print("Fatto Quotidiano "); write(REVON, 161, 'm', REVOFF, 161); println("David Puente");
        write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '6', REVOFF, 161); print("Next Quotidiano  "); write(REVON, 161, 'n', REVOFF, 161); println("The Fool");
        write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '7', REVOFF, 161); println("Wired");
        write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '8', REVOFF, 161); println("Tpi");
        write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '9', REVOFF, 161); println("Medbunker");
        write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '0', REVOFF, 161); println("FNOMCeO");
        write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, 'a', REVOFF, 161); println("Medical Facts");
        write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, 'b', REVOFF, 161); println("Sys64738");
        write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, 'c', REVOFF, 161); println("Retrocampus");
        write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, 'd', REVOFF, 161); println("RetroAcademy");
        write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, 'e', REVOFF, 161); println("Arch. Informatica");
        write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, 'f', REVOFF, 161); println("Punto Informatico");
        write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, 'g', REVOFF, 161); println("Query Online");
        write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, 'h', REVOFF, 161); print("CICAP Lombardia          "); write(REVON, 161, '.', REVOFF, 161); print("Exit");
        flush();
        boolean validKey;
        do {
            validKey = true;
            resetInput();
            int key = readKey();
            resetInput();
            if (key >= 193 && key <= 218) {
                key -= 96;
            }
            key = Character.toLowerCase(key);
            log("Menu-NewsITA. Pressed: '" + (key < 32 || key > 127 ? "chr(" + key + ")" : ((char) key)) + "' (code=" +
                key + ")");
            if (key == '.') {
                return;
            }
            else if (key == '1') launch(new TelevideoRaiPetscii());
            else if (key == '2') launch(new IlPost());
            else if (key == '3') launch(new ValigiaBlu());
            else if (key == '4') launch(new Linkiesta());
            else if (key == '5') launch(new IlFattoQuotidiano());
            else if (key == '6') launch(new NextQuotidiano());
            else if (key == '7') launch(new WiredItalia());
            else if (key == '8') launch(new Tpi());
            else if (key == '9') launch(new Medbunker());
            else if (key == '0') launch(new DottoreMaEVeroChe());
            else if (key == 'a') launch(new MedicalFacts());
            else if (key == 'b') launch(new Sys64738());
            else if (key == 'c') launch(new RetroCampus());
            else if (key == 'd') launch(new RetroAcademy());
            else if (key == 'e') launch(new ArcheologiaInformatica());
            else if (key == 'f') launch(new PuntoInformatico());
            else if (key == 'g') launch(new QueryOnline());
            else if (key == 'h') launch(new CicapLombardia());
            else if (key == 'i') launch(new Disinformatico());
            else if (key == 'j') launch(new BufaleNet());
            else if (key == 'k') launch(new Butac());
            else if (key == 'l') launch(new FactaNews());
            else if (key == 'm') launch(new DavidPuenteBlog());
            else if (key == 'n') launch(new TheFoolBlog());
            else {
                validKey = false;
            }
            if (validKey) return;
        } while (!validKey);
    }

    public void menuNewsEng() throws Exception {
        write(CLR, LOWERCASE, CASE_LOCK, HOME);
        drawLogo();
        write(GREY3);
        gotoXY(4,5);
        write(32, 32, 32, BLUE); print("Eng"); write(RED); print("lish "); write(WHITE); print("News"); newline();
        write(RIGHT, RIGHT, RIGHT, ' ', ' ', ' ', ' ', GREY3); println(repeat((char) 163, 12));
        newline();
        write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '1', REVOFF, 161); println("CNN News");
        write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '2', REVOFF, 161); println("BBC News");
        write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '3', REVOFF, 161); println("Wired");
        write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '4', REVOFF, 161); println("Vintage Computer Federation");
        write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '5', REVOFF, 161); println("Indie Retro News");
        write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '6', REVOFF, 161); println("The 8-Bit Guy");
        newline();
        write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '.', REVOFF, 161); print("Exit ");
        flush();
        boolean validKey;
        do {
            validKey = true;
            resetInput();
            int key = readKey();
            resetInput();
            if (key >= 193 && key <= 218) {
                key -= 96;
            }
            key = Character.toLowerCase(key);
            log("Menu-NewsENG. Pressed: '" + (key < 32 || key > 127 ? "chr(" + key + ")" : ((char) key)) + "' (code=" +
                key + ")");
            if (key == '.') {
                return;
            }
            else if (key == '1') launch(new CnnPetscii());
            else if (key == '2') launch(new BbcPetscii());
            else if (key == '3') launch(new WiredCom());
            else if (key == '4') launch(new Vcfed());
            else if (key == '5') launch(new IndieRetroNews());
            else if (key == '6') launch(new The8BitGuy());
            else {
                validKey = false;
            }
            if (validKey) return;
        } while (!validKey);
    }

    public void menuGames() throws Exception {
        write(CLR, LOWERCASE, CASE_LOCK, HOME);
        write(readBinaryFile("petscii/games.seq"));
        write(HOME);
        drawLogo();
        write(GREY3);
        gotoXY(39,24);

        flush();
        boolean validKey;
        do {
            validKey = true;
            resetInput();
            int key = readKey();
            resetInput();
            if (key >= 193 && key <= 218) {
                key -= 96;
            }
            key = Character.toLowerCase(key);
            log("Menu-Games. Pressed: '" + (key < 32 || key > 127 ? "chr(" + key + ")" : ((char) key)) + "' (code=" +
                key + ")");
            if (key == '.') {
                return;
            }
            else if (key == '1') launch(new TicTacToe());
            else if (key == '2') launch(new ConnectFour());
            else if (key == '3') launch(new Magic15());
            else if (key == '4') launch(new ZorkMachine("zmpp/zork1.z3"));
            else if (key == '5') launch(new ZorkMachine("zmpp/zork2.z3"));
            else if (key == '6') launch(new ZorkMachine("zmpp/zork3.z3"));
            else if (key == '7') launch(new ZorkMachine("zmpp/hitchhiker-r60.z3"));
            else {
                validKey = false;
            }
            if (validKey) return;
        } while (!validKey);
    }

    public void menuDownloads() throws Exception {
        write(CLR, LOWERCASE, CASE_LOCK, HOME);
        drawLogo();
        write(GREY3);
        gotoXY(4,5);
        write(32, 32, 32, LIGHT_GREEN); print("Downloads"); newline();
        write(RIGHT, RIGHT, RIGHT, ' ', ' ', ' ', ' ', GREEN); println(repeat((char) 163, 9));
        newline();
        write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '1', REVOFF, 161); println("CSDb (recommended)");
        write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '2', REVOFF, 161); println("CSDb - SD2IEC (experimental)");
        write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '3', REVOFF, 161); println("Arnold C64");
        newline();
        write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '.', REVOFF, 161); print("Exit ");
        flush();
        boolean validKey;
        do {
            validKey = true;
            resetInput();
            int key = readKey();
            resetInput();
            if (key >= 193 && key <= 218) {
                key -= 96;
            }
            key = Character.toLowerCase(key);
            log("Menu-Downloads. Pressed: '" + (key < 32 || key > 127 ? "chr(" + key + ")" : ((char) key)) + "' (code=" +
                key + ")");
            if (key == '.') {
                return;
            }
            else if (key == '1') launch(new CsdbReleases());
            else if (key == '2') launch(new CsdbReleasesSD2IEC());
            else if (key == '3') launch(new ArnoldC64());
            else {
                validKey = false;
            }
            if (validKey) return;
        } while (!validKey);
    }

    public void about() throws Exception {
        write(CLR, LOWERCASE, CASE_LOCK, HOME);
        write(readBinaryFile("petscii/about.seq"));
        write(HOME);
        drawLogo();
        write(GREY3);
        gotoXY(24,14);

        flush();
        resetInput();
        readKey();
    }

    public void drawLogo() {
        write(alternateLogo()
            ? LOGO_BYTES_ALTERNATE
            : LOGO_BYTES
        );
    }

    private boolean alternateLogo() {
        return IP_FOR_ALTERNATE_LOGO.equals(serverAddress.getHostAddress())
            || serverPort == PORT_FOR_ALTERNATE_LOGO;
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

    private static final byte[] LOGO_BYTES_ALTERNATE = new byte[] {
        28, -69, -69, -84, -69, -84, 18, -68, -110, -84, -84, 32, -94, 32, -104, -84,
        -94, 32, -94, 32, -94, -69, -84, 18, -66, -110, 32, -94, 32, -94, -94, 32,
        -69, -84, 32, 32, 32, 32, 18, -101, -65, -110, -69, -95, 32, -66, 18, -68,
        -110, 13, 18, 28, -84, -110, 32, 18, -68, -94, -110, 32, -95, 18, -95, -110,
        -66, 18, -95, -110, 32, -95, -104, -95, 18, -95, -95, -110, 32, 18, -95, -110,
        32, -95, -95, 18, -95, -95, -65, -110, -66, -95, -95, -95, -95, 18, -95, -110,
        32, 32, 32, 32, 18, -101, -65, -110, -69, 18, -84, -110, -69, -95, -95, 13,
        28, -66, 32, -68, -66, 32, -68, -68, 32, 32, 18, -94, -110, 32, -104, -68,
        18, -94, -110, 32, 18, -94, -110, 32, 18, -94, -110, -66, -68, 18, -94, -110,
        32, 18, -94, -110, 32, -66, -66, -66, -68, 18, -69, -110, 30, -68, 18, -94,
        -110, -66, 32, -101, -68, 32, 18, -94, -110, 32, -66, -68, 13, 32, 32, 32,
        32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
        32, 32, 32, 32, 32, 32, 32, -104, -68, -66, 13, 18, -102, 32, -94, -68,
        -110, 13, 18, 32, -110, -94, 18, -84, -110, 13, 18, 32, -110, 32, 18, 32,
        -110, 13, 18, -94, -94, -110, -66, 13, 18, -103, 32, -94, -68, -110, 13, 18,
        32, -110, -94, 18, -84, -110, 13, 18, 32, -110, 32, 18, 32, -110, 13, 18,
        -94, -94, -110, -66, 13, 18, -106, -66, -94, -68, -110, 13, 18, -69, -110, -94,
        -69, 13, -94, 32, 18, 32, -110, 13, -68, 18, -94, -110, -66, 13
    };
}
