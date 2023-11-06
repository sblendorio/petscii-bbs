package eu.sblendorio.bbs.tenants.petscii;

import com.fasterxml.jackson.databind.JsonNode;
import com.maxmind.db.Reader;
import eu.sblendorio.bbs.core.PetsciiKeys;
import eu.sblendorio.bbs.core.PetsciiThread;
import eu.sblendorio.bbs.core.Utils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static eu.sblendorio.bbs.core.PetsciiColors.*;
import static eu.sblendorio.bbs.core.PetsciiKeys.*;
import static eu.sblendorio.bbs.core.Utils.readExternalTxt;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.apache.commons.lang3.StringUtils.startsWith;
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
        if (alternateLogo()) { write(PetsciiKeys.LOWERCASE); println();println();println("Moved to BBS.RETROCAMPUS.COM");println(); keyPressed(10_000); return; }

        init();
        while (true) {
            write(CLR, LOWERCASE, CASE_LOCK, HOME);
            log("Starting Main Menu BBS");
            byte[] logoseq = readBinaryFile(
                    alternateLogo()
                            ? "petscii/bbs-menu-main-alternate.seq"
                            : "petscii/bbs-menu-main.seq");
            String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
            logoseq = new String(logoseq, StandardCharsets.ISO_8859_1)
                    .replace("9999", currentYear)
                    .getBytes(StandardCharsets.ISO_8859_1);
            write(logoseq);
            write(HOME);
            drawLogo();
            write(GREY3);
            gotoXY(39, alternateLogo() ? 23 : 24);

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
                    goodbye();
                    return;
                }
                else if (key == '1') menuNewsIta();
                else if (key == '2') menuNewsEng();
                else if (key == '3') menuGames();
                else if (key == '4') menuDownloads();
                else if (key == '5') launch(new Chat64());
                else if (key == '6') launch(new UserLogon());
                else if (key == '7') launch(new InternetBrowser());
                else if (key == '8') launch(new ElizaPetscii());
                else if (key == '9' && !alternateLogo()) launch(new ClientChatGptPetscii());
                else if (key == 'g') launch(new PetsciiArtGallery());
                // else if (key == 'l') launch(new Ossa());
                else if (key == 'x') about();
                else if (key == 'a' && !alternateLogo()) patrons();
                else if (key == 'b' && !alternateLogo()) patronsLogo();
                else if (key == 'c' && !alternateLogo()) patronsPublishers();
                else if (key == 'y' && !alternateLogo()) wifiModem();
                else {
                    validKey = false;
                }
            } while (!validKey);
        }
    }

    public void menuNewsIta() throws Exception {
        do {
            write(CLR, LOWERCASE, CASE_LOCK, HOME);
            drawLogo();
            write(GREY3);
            gotoXY(4,4);
            write(REVON, GREEN, 161, WHITE, ' ', RED, REVOFF, 161, CYAN); print("Italian News   "); write(GREY3, REVON, 161, 'j', REVOFF, 161); println("Bufale.net");
            write(RIGHT, RIGHT, RIGHT, ' ', ' ', ' ', ' ', LIGHT_BLUE); print(repeat((char) 163, 12)); print("   "); write(GREY3, REVON, 161, 'k', REVOFF, 161); println("Butac");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '1', REVOFF, 161); print("Televideo      "); write(REVON, 161, 'l', REVOFF, 161); println("Facta news");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '2', REVOFF, 161); print("Open Online    "); write(REVON, 161, 'm', REVOFF, 161); println("David Puente");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '3', REVOFF, 161); print("Valigia Blu    "); write(REVON, 161, 'n', REVOFF, 161); println("Sportal.it");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '4', REVOFF, 161); print("Linkiesta      "); write(REVON, 161, 'o', REVOFF, 161); println("Indie Campus");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '5', REVOFF, 161); print("Il Fatto quot. "); write(REVON, 161, 'p', REVOFF, 161); println("Retrocommodore");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '6', REVOFF, 161); print("TI Watch Museum"); write(REVON, 161, 'q', REVOFF, 161); println("Valoroso.IT");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '7', REVOFF, 161); print("Lercio         "); write(REVON, 161, 'r', REVOFF, 161); println("Mupin");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '8', REVOFF, 161); print("CICAP Lombardia"); write(REVON, 161, 's', REVOFF, 161); println("Query Online");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '9', REVOFF, 161); print("Medbunker      "); write(REVON, 161, 't', REVOFF, 161); println("Formiche.net");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '0', REVOFF, 161); print("FNOMCeO        "); write(REVON, 161, 'u', REVOFF, 161); println("Infosec News");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, 'a', REVOFF, 161); print("Medical Facts  "); write(REVON, 161, 'v', REVOFF, 161); println("Giano News");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, 'b', REVOFF, 161); print("Retrocampus    "); write(REVON, 161, 'w', REVOFF, 161); println("Red Hot Cyber");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, 'c', REVOFF, 161); print("RetroAcademy   "); write(REVON, 161, 'x', REVOFF, 161); println("MCC Blog");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, 'd', REVOFF, 161); println("D.E.T.A.");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, 'e', REVOFF, 161); println("Attivissimo");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, 'f', REVOFF, 161); println("Chi ha paura del buio?");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, 'g', REVOFF, 161); println("Apulia Retrocomputing");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, 'h', REVOFF, 161); println("Arch. Informatica");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, 'i', REVOFF, 161); print("Alessandro Albano        "); write(REVON, 161, '.', REVOFF, 161); print("Exit");
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
                else if (key == '2') launch(new OneRssOpenOnline());
                else if (key == '3') launch(new ValigiaBlu());
                else if (key == '4') launch(new Linkiesta());
                else if (key == '5') launch(new IlFattoQuotidiano());
                else if (key == '6') launch(new OneTexasInstrumentsItaliaPetscii());
                else if (key == '7') launch(new Lercio());
                else if (key == '8') launch(new CicapLombardia());
                else if (key == '9') launch(new Medbunker());
                else if (key == '0') launch(new DottoreMaEVeroChe());
                else if (key == 'a') launch(new MedicalFacts());
                else if (key == 'b') launch(new RetroCampus());
                else if (key == 'c') launch(new RetroAcademy());
                else if (key == 'd') launch(new DetaPetscii());
                else if (key == 'e') launch(new Disinformatico());
                else if (key == 'f') launch(new ChpdbPetscii());
                else if (key == 'g') launch(new OneApuliaRetrocomputingPetscii());
                else if (key == 'h') launch(new ArcheologiaInformatica());
                else if (key == 'i') launch(new AlessandroAlbano());
                else if (key == 'j') launch(new BufaleNet());
                else if (key == 'k') launch(new Butac());
                else if (key == 'l') launch(new FactaNews());
                else if (key == 'm') launch(new DavidPuenteBlog());
                else if (key == 'n') launch(new Sportal());
                else if (key == 'o') launch(new IndieCampus());
                else if (key == 'p') launch(new Retrocommodore());
                else if (key == 'q') launch(new ValorosoIt());
                else if (key == 'r') launch(new Mupin());
                else if (key == 's') launch(new QueryOnline());
                else if (key == 't') launch(new FormichePetscii());
                else if (key == 'u') launch(new InfosecNewsPetscii());
                else if (key == 'v') launch(new GianoNewsPetscii());
                else if (key == 'w') launch(new RedHotCyberPetscii());
                else if (key == 'x') launch(new MccPetscii());
                else {
                    validKey = false;
                }
                // if (validKey) return;
            } while (!validKey);
        } while (true);
    }

    public void menuNewsEng() throws Exception {
        do {
            write(CLR, LOWERCASE, CASE_LOCK, HOME);
            drawLogo();
            write(GREY3);
            gotoXY(4, 5);
            write(32, 32, 32, BLUE);
            print("Interna");
            write(RED);
            print("tional");
            write(WHITE);
            print(" News");
            newline();
            write(RIGHT, RIGHT, RIGHT, ' ', ' ', ' ', ' ', GREY3);
            println(repeat((char) 163, 18));
            newline();
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '1', REVOFF, 161); println("CNN News");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '2', REVOFF, 161); println("BBC News");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '3', REVOFF, 161); println("Politico.com");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '4', REVOFF, 161); println("Al Jazeera");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '5', REVOFF, 161); println("Wired");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '6', REVOFF, 161); println("Vintage Computer Federation");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '7', REVOFF, 161); println("Indie Retro News");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '8', REVOFF, 161); println("The 8-Bit Guy");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '9', REVOFF, 161); println("Vintage is the new old");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '0', REVOFF, 161); println("2600 News");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, 'a', REVOFF, 161); println("Hackaday Blog");
            newline();
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '.', REVOFF, 161);
            print("Exit ");
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
                else if (key == '3') launch(new OneRssPoliticoPetscii());
                else if (key == '4') launch(new OneRssAJPlusPetscii());
                else if (key == '5') launch(new WiredCom());
                else if (key == '6') launch(new Vcfed());
                else if (key == '7') launch(new IndieRetroNews());
                else if (key == '8') launch(new The8BitGuy());
                else if (key == '9') launch(new Vitno());
                else if (key == '0') launch(new OneRss2600Petscii());
                else if (key == 'a') launch(new HackadayPetscii());
                else {
                    validKey = false;
                }
                // if (validKey) return;
            } while (!validKey);
        } while (true);
    }

    public void patronsPublishers() throws Exception {
        do {
            write(CLR, LOWERCASE, CASE_LOCK, HOME);
            drawLogo();
            write(GREY3);
            gotoXY(4, 5);
            write(32, 32, 32, WHITE);
            print("Patrons - Publishers");
            newline();
            write(RIGHT, RIGHT, RIGHT, ' ', ' ', ' ', ' ', GREY3);
            println(repeat((char) 163, 20));
            newline();
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '1', REVOFF, 161); println("Syncroweb - Fulvio Ieva");
            newline();
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '.', REVOFF, 161);
            print("Exit ");
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
                log("Patrons-Publishers. Pressed: '" + (key < 32 || key > 127 ? "chr(" + key + ")" : ((char) key)) + "' (code=" +
                        key + ")");
                if (key == '.') {
                    return;
                }
                else if (key == '1') launch(new SyncroWebPetscii());
                else {
                    validKey = false;
                }
                // if (validKey) return;
            } while (!validKey);
        } while (true);
    }

    public void menuGames() throws Exception {
        do {
            write(CLR, LOWERCASE, CASE_LOCK, HOME);
            write(readBinaryFile("petscii/games.seq"));
            write(HOME);
            drawLogo();
            write(GREY3);
            gotoXY(39, 24);

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
                } else if (key == '1') launch(new TicTacToe());
                else if (key == '2') launch(new ConnectFour());
                else if (key == '3') launch(new Magic15());
                else if (key == '4') launch(new ZorkMachine("zmpp/zork1.z3", readBinaryFile("petscii/zork1.seq"))); // Zork-1-ITA-v7.z5
                else if (key == '5') launch(new ZorkMachine("zmpp/zork2.z3", readBinaryFile("petscii/zork2.seq")));
                else if (key == '6') launch(new ZorkMachine("zmpp/zork3.z3", readBinaryFile("petscii/zork3.seq")));
                else if (key == '7') launch(new ZorkMachine("zmpp/hitchhiker-r60.z3", readBinaryFile("petscii/dontpanic.seq")));
                else {
                    validKey = false;
                }
                // if (validKey) return;
            } while (!validKey);
        } while (true);
    }

    public void menuDownloads() throws Exception {
        do {
            write(CLR, LOWERCASE, CASE_LOCK, HOME);
            drawLogo();
            write(GREY3);
            gotoXY(4, 5);
            write(32, 32, 32, LIGHT_GREEN);
            print("Downloads");
            newline();
            write(RIGHT, RIGHT, RIGHT, ' ', ' ', ' ', ' ', GREEN);
            println(repeat((char) 163, 9));
            newline();
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '1', REVOFF, 161);
            println("CSDb (recommended)");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '2', REVOFF, 161);
            println("CSDb - SD2IEC (experimental)");
            /// write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '3', REVOFF, 161);
            // println("Arnold C64");
            newline();
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '.', REVOFF, 161);
            print("Exit ");
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
                log("Menu-Downloads. Pressed: '" + (key < 32 || key > 127 ? "chr(" + key + ")" : ((char) key)) +
                    "' (code=" +
                    key + ")");
                if (key == '.') {
                    return;
                } else if (key == '1') launch(new CsdbReleases());
                else if (key == '2') launch(new CsdbReleasesSD2IEC());
                // else if (key == '3') launch(new ArnoldC64());
                else {
                    validKey = false;
                }
                // if (validKey) return;
            } while (!validKey);
        } while (true);
    }

    public void about() throws Exception {
        write(CLR, LOWERCASE, CASE_LOCK, HOME);
        write(readBinaryFile("petscii/about.seq"));
        write(HOME);
        drawLogo();
        write(GREY3);
        gotoXY(22,19);
        flush();
        resetInput();
        readKey();

    }

    public void patrons() throws Exception {
        int PAGING_LINES = 12;

        write(CLR, LOWERCASE, CASE_LOCK, HOME);
        write(readBinaryFile("petscii/patreon.seq"));
        write(HOME);
        drawLogo();
        write(GREY3, REVOFF);
        gotoXY(20, 12);
        List<String> patrons =
                readExternalTxt(System.getProperty("PATREON_LIST", System.getProperty("user.home") + File.separator + "patreon_list.txt"))
                .stream()
                .map(StringUtils::trim)
                .filter(StringUtils::isNotBlank)
                .filter(str -> !str.startsWith(";"))
                .map(x -> x.replaceAll(" - .*$", ""))
                .map(x -> StringUtils.substring(x, 0, 20))
                .sorted(comparing(String::toLowerCase))
                .collect(toList());

        int count = 0;
        for (String name: patrons) {
            count++;

            print(name);
            if ((count % PAGING_LINES) +1 >1 && count % PAGING_LINES != 0) {
                write(DOWN);
                for (int i = 0; i < name.length(); i++) write(LEFT);
            }
            if (count % PAGING_LINES == 0 && count < patrons.size()) {
                flush(); resetInput();
                int key = readKey();
                if (key == '.') return;
                for (int j=0; j<patrons.get(count-1).length(); j++) write(LEFT);

                for (int i = count - 1; i>= count -PAGING_LINES; i--) {
                    for (int j=0; j<patrons.get(i).length(); j++) write(SPACE_CHAR);
                    for (int j=0; j<patrons.get(i).length(); j++) write(LEFT);
                    write(UP);
                }
                write(DOWN);
            }
        }

        flush();
        resetInput();
        readKey();
    }

    public void patronsLogo() throws Exception {
        List<String> files = Arrays.asList(
            "petscii/patreon-sponsor-01.seq",
            "petscii/patreon-sponsor-02.seq"
        );
        for (String file: files) {
            write(CLR, LOWERCASE, CASE_LOCK, HOME);
            write(readBinaryFile(file));
            write(HOME);
            drawLogo();
            write(GREY3, REVOFF);
            flush();
            resetInput();
            readKey();
        }
    }
    public void wifiModem() throws Exception {
        write(CLR, LOWERCASE, CASE_LOCK, HOME);
        write(readBinaryFile("petscii/rs232modem.seq"));
        write(HOME);
        drawLogo();
        write(GREY3, REVOFF);
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

    private void goodbye() throws Exception {
        List<Path> files = Utils.getDirContent("petscii/goodbye");
        if (files == null || files.size() == 0) {
            newline();
            newline();
            println("GOODBYE");
            newline();
            return;
        }
        Collections.shuffle(files);
        String filename = files.get(0).toString();
        if (startsWith(filename,"/")) filename = filename.substring(1);
        for (int i=0; i<25; ++i) newline();
        writeRawFile(filename);
        write(PetsciiKeys.CASE_UNLOCK);
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
