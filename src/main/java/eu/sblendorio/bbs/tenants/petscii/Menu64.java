package eu.sblendorio.bbs.tenants.petscii;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import eu.sblendorio.bbs.core.*;
import eu.sblendorio.bbs.tenants.mixed.SwBasicBridge;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.TriConsumer;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import static eu.sblendorio.bbs.core.BlockGraphicsPetscii.getRenderedMidres;
import static eu.sblendorio.bbs.core.PetsciiColors.*;
import static eu.sblendorio.bbs.core.PetsciiKeys.*;
import static eu.sblendorio.bbs.core.Utils.readExternalTxt;
import static eu.sblendorio.bbs.tenants.mixed.GeolocationCommons.isItaly;
import static eu.sblendorio.bbs.tenants.mixed.GeolocationCommons.isLocalhost;
import static eu.sblendorio.bbs.tenants.mixed.HolidayCommons.*;
import static java.util.Comparator.comparing;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.apache.commons.lang3.StringUtils.startsWith;

public class Menu64 extends PetsciiThread {

    final static byte[] SPLASH_CASTELLO_ITA = readBinaryFile("petscii/avventura-nel-castello.seq");
    final static byte[] SPLASH_CASTELLO_ENG = readBinaryFile("petscii/castle-adventure.seq");

    @Override
    public void doLoop() throws Exception {
        resetInput();
        if (
                isAscanioDay() && (isItaly(ipAddress.getHostAddress()) || isLocalhost(ipAddress.getHostAddress()))
        ) {
            write(CLR, UPPERCASE, CASE_LOCK, HOME);
            String[] matrix = BlockGraphicsMinitel.stringToQr("t.ly/yjubs", ErrorCorrectionLevel.M);
            String[] matrixStr = new String[matrix.length+1];
            matrixStr[0] = StringUtils.repeat('.', matrix[0].length());
            for (int i=0; i<matrix.length; i++) matrixStr[i+1] = "." + matrix[i];
            write(WHITE);
            write(BlockGraphicsPetscii.getRenderedMidres(28, matrixStr, true, true));
            write(HOME);
            write(readBinaryFile("petscii/ascanio.seq"));
            flush(); resetInput();
            keyPressed(60_000L);
        }

        while (true) {
            write(CLR, CASE_UNLOCK, LOWERCASE, CASE_LOCK, HOME);
            log("Starting Main Menu BBS");
            String logoFilename;
            if (isXmasTime()) {
                logoFilename = "petscii/bbs-menu-main-christmas.seq";
            } else if (isSanremo()) {
                logoFilename = "petscii/bbs-menu-main-sanremo.seq";
            } else {
                logoFilename = "petscii/bbs-menu-main.seq";
            }
            byte[] logoseq = readBinaryFile(logoFilename);
            String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
            logoseq = new String(logoseq, StandardCharsets.ISO_8859_1)
                    .replace("9999", currentYear)
                    .getBytes(StandardCharsets.ISO_8859_1);
            write(logoseq);
            write(HOME);
            drawLogo();
            write(GREY3);
            gotoXY(39, 24);

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
                else if (key == '1') menuNewsEng();
                else if (key == '2') menuNewsIta();
                else if (key == '3') menuGames();
                else if (key == '4') menuDownloads();
                else if (key == '5') launch(new Chat64());
                else if (key == '6') launch(new UserLogon());
                else if (key == '7') launch(new InternetBrowser());
                else if (key == '8') launch(new ElizaPetscii());
                else if (key == '9') launch(new ClientChatGptPetscii());
                else if (key == 'g') launch(new PetsciiArtGallery());
                // else if (key == 'l') launch(new Ossa());
                else if (key == 'x') about();
                else if (key == 'a') patrons();
                else if (key == 'b') patronsLogo();
                else if (key == 'c') patronsPublishers();
                else if (key == 'y') wifiModem();
                else if (key == 'w') launch(new WikipediaPetscii());
                else if (key == 'p') menuBasicPrograms();
                else if (key == 'e') launch(new EnigmaPetscii());
                else if (isSanremo() && key == 's') launch(new SanremoPetscii());
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
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '2', REVOFF, 161); print("Open Online    "); write(REVON, 161, 'm', REVOFF, 161); println("Fanpage");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '3', REVOFF, 161); print("Valigia Blu    "); write(REVON, 161, 'n', REVOFF, 161); println("Sportal.it");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '4', REVOFF, 161); print("Linkiesta      "); write(REVON, 161, 'o', REVOFF, 161); println("Indie Campus");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '5', REVOFF, 161); print("Il Fatto quot. "); write(REVON, 161, 'p', REVOFF, 161); println("Retrocommodore");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '6', REVOFF, 161); print("TI Watch Museum"); write(REVON, 161, 'q', REVOFF, 161); println("Valoroso.IT");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '7', REVOFF, 161); print("Lercio         "); write(REVON, 161, 'r', REVOFF, 161); println("Mupin");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '8', REVOFF, 161); print("Bitold.eu      "); write(REVON, 161, 's', REVOFF, 161); println("Query Online");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '9', REVOFF, 161); print("Medbunker      "); write(REVON, 161, 't', REVOFF, 161); println("Formiche.net");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '0', REVOFF, 161); print("FNOMCeO        "); write(REVON, 161, 'u', REVOFF, 161); println("Infosec News");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, 'a', REVOFF, 161); print("Medical Facts  "); write(REVON, 161, 'v', REVOFF, 161); println("Giano News");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, 'b', REVOFF, 161); print("Retrocampus    "); write(REVON, 161, 'w', REVOFF, 161); println("Red Hot Cyber");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, 'c', REVOFF, 161); print("RetroAcademy   "); write(REVON, 161, 'x', REVOFF, 161); println("MCC Blog");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, 'd', REVOFF, 161); print("C.H.P.D.B.     "); write(REVON, 161, 'y', REVOFF, 161); println("Ready 64");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, 'e', REVOFF, 161); println("Attivissimo");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, 'f', REVOFF, 161); println("Commesso Perplesso");
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
                switch (key) {
                    case '1' -> launch(new TelevideoRaiPetscii());
                    case '2' -> launch(new OneRssOpenOnline());
                    case '3' -> launch(new ValigiaBlu());
                    case '4' -> launch(new Linkiesta());
                    case '5' -> launch(new IlFattoQuotidiano());
                    case '6' -> launch(new OneTexasInstrumentsItaliaPetscii());
                    case '7' -> launch(new Lercio());
                    case '8' -> launch(new BitoldPetscii());
                    case '9' -> launch(new Medbunker());
                    case '0' -> launch(new DottoreMaEVeroChe());
                    case 'a' -> launch(new MedicalFacts());
                    case 'b' -> launch(new RetroCampus());
                    case 'c' -> launch(new RetroAcademy());
                    case 'd' -> launch(new ChpdbPetscii());
                    case 'e' -> launch(new Disinformatico());
                    case 'f' -> launch(new CommessoPerplessoPetscii());
                    case 'g' -> launch(new OneApuliaRetrocomputingPetscii());
                    case 'h' -> launch(new ArcheologiaInformatica());
                    case 'i' -> launch(new AlessandroAlbano());
                    case 'j' -> launch(new BufaleNet());
                    case 'k' -> launch(new Butac());
                    case 'l' -> launch(new FactaNews());
                    case 'm' -> launch(new LiteFanpagePetscii());
                    case 'n' -> launch(new Sportal());
                    case 'o' -> launch(new IndieCampus());
                    case 'p' -> launch(new Retrocommodore());
                    case 'q' -> launch(new ValorosoIt());
                    case 'r' -> launch(new Mupin());
                    case 's' -> launch(new QueryOnline());
                    case 't' -> launch(new FormichePetscii());
                    case 'u' -> launch(new InfosecNewsPetscii());
                    case 'v' -> launch(new GianoNewsPetscii());
                    case 'w' -> launch(new RedHotCyberPetscii());
                    case 'x' -> launch(new MccPetscii());
                    case 'y' -> launch(new OneRssReady64Petscii());
                    case '.' -> { return; }
                    default -> validKey = false;
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
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '5', REVOFF, 161); println("Fox News");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '6', REVOFF, 161); println("Wired");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '7', REVOFF, 161); println("Vintage Computer Federation");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '8', REVOFF, 161); println("Indie Retro News");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '9', REVOFF, 161); println("The 8-Bit Guy");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '0', REVOFF, 161); println("Vintage is the new old");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, 'a', REVOFF, 161); println("2600 News");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, 'b', REVOFF, 161); println("Hackaday Blog");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, 'c', REVOFF, 161); println("Amedeo Valoroso - English");
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
                switch (key) {
                    case '1' -> launch(new LiteCnnPetscii()); //launch(new CnnPetscii());
                    case '2' -> launch(new BbcPetscii());
                    case '3' -> launch(new OneRssPoliticoPetscii());
                    case '4' -> launch(new OneRssAJPlusPetscii());
                    case '5' -> launch(new OneRssFoxnewsPetscii());
                    case '6' -> launch(new WiredCom());
                    case '7' -> launch(new Vcfed());
                    case '8' -> launch(new IndieRetroNews());
                    case '9' -> launch(new The8BitGuy());
                    case '0' -> launch(new Vitno());
                    case 'a' -> launch(new OneRss2600Petscii());
                    case 'b' -> launch(new HackadayPetscii());
                    case 'c' -> launch(new OneRssAmedeoValorosoEngPetscii());
                    case '.' -> { return; }
                    default -> validKey = false;
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

    public void menuBasicPrograms() throws Exception {
        do {
            write(CLR, LOWERCASE, CASE_LOCK, HOME);
            drawLogo();

            gotoXY(23, 8);  write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, 'z', REVOFF, 161); println("BBS BASIC");

            gotoXY(0,4);
            write(GREEN);
            write(getRenderedMidres(4,
                    new String[]{
                            ".1111...111...111...1..111.",
                            ".1...1.1...1.1...1..1.1...1",
                            ".1...1.1...1.1......1.1....",
                            ".1111..11111.11111..1.1....",
                            ".1...1.1...1.....1..1.1....",
                            ".1...1.1...1.1...1..1.1...1",
                            ".1111..1...1..111...1..111."                    }
                    , false, false));

            //write(GREY3);
            //gotoXY(18, 6);
            //write(WHITE);
            //print("Programs");
            //newline();
            //write(RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, GREY3);
            //println(repeat((char) 163, 8));
            newline();
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '1', REVOFF, 161); println("Star Trek");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '2', REVOFF, 161); println("Star Trek 2003");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '3', REVOFF, 161); println("Lunar Lander");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '4', REVOFF, 161); println("Hamurabi");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '5', REVOFF, 161); println("Checkers");
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '6', REVOFF, 161); println("Angela Game (P101)");

            newline();
            write(RIGHT, RIGHT, RIGHT, ' ', GREY3, REVON, 161, '.', REVOFF, 161);
            print("Exit ");
            write(GREY3);
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
                switch (key) {
                    case '1' -> SwBasicBridge.run("Star Trek", "basic/startrek-40-1.bas", this, locate());
                    case '2' -> SwBasicBridge.run("Star Trek 2003", "basic/startrek-40-2.bas", this, locate());
                    case '3' -> SwBasicBridge.run("Lunar Lander", "basic/lunar-lander-40.bas", this, locate());
                    case '4' -> SwBasicBridge.run("Hamurabi", "basic/hamurabi-40.bas", this, locate());
                    case '5' -> SwBasicBridge.run("Checkers", "basic/checkers-40.bas", this, locate());
                    case '6' -> SwBasicBridge.run("Angela", "basic/angela.bas", this, locate());
                    case 'z' -> launch(new BasicIdePetscii(locate()));
                    case '.' -> { return; }
                    default -> validKey = false;
                }
                // if (validKey) return;
            } while (!validKey);
        } while (true);
    }

    public TriConsumer<BbsThread, Integer, Integer> locate() {
        return (bbs, y, x) -> {
            if (x>0 && y>0) {
                bbs.write(19);
                for (int i=1; i<y; i++) bbs.write(17);
                for (int i=1; i<x; i++) bbs.write(29);
            } else if (y>0) {
                for (int i=0; i<25; i++) bbs.write(145);
                for (int i=1; i<y; i++) bbs.write(17);
            } else if (x>0) {
                bbs.write(13, 145);
                for (int i=1; i<x; i++) bbs.write(29);
            }
        };
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
                switch (key) {
                    case '1' -> launch(new TicTacToe());
                    case '2' -> launch(new ConnectFour());
                    case '3' -> launch(new Magic15());
                    case '4' -> launch(new ZorkMachine("zork1", "zmpp/zork1.z3", readBinaryFile("petscii/zork1.seq")));
                    case '5' -> launch(new ZorkMachine("zork2", "zmpp/zork2.z3", readBinaryFile("petscii/zork2.seq")));
                    case '6' -> launch(new ZorkMachine("zork3", "zmpp/zork3.z3", readBinaryFile("petscii/zork3.seq")));
                    case '7' -> launch(new ZorkMachine("hitchhikers", "zmpp/hitchhiker-r60.z3", readBinaryFile("petscii/dontpanic.seq")));
                    case '8' -> launch(new ZorkMachine("planetfall", "zmpp/planetfall-r39.z3", readBinaryFile("petscii/planetfall.seq")));
                    case '9' -> launch(new ZorkMachine("stationfall", "zmpp/stationfall-r107.z3", readBinaryFile("petscii/stationfall.seq")));
                    case '0' -> launch(new AvventuraNelCastelloPetscii(SPLASH_CASTELLO_ENG, "en-gb"));
                    case 'a' -> launch(new ZorkMachine("zork1ita", "zmpp/Zork-1-ITA-v7.z5", readBinaryFile("petscii/zork1.seq")));
                    case 'b' -> launch(new AvventuraNelCastelloPetscii(SPLASH_CASTELLO_ITA, "it-it"));
                    case '.' -> { return; }
                    default -> validKey = false;
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
        gotoXY(22,21);
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
                .toList();

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
            "petscii/patreon-sponsor-02.seq"/*,
             "petscii/patreon-sponsor-03.seq"*/
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
        if (isXmasTime()) {
            write(readBinaryFile("petscii/christmas-ribbon.seq"));
        } else {
            write(LOGO_BYTES);
        }
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

        write(UPPERCASE);
        if (isXmasTime()) {
            writeRawFile("petscii/goodbye/santa-kody.seq");
        } else {
            writeRawFile(filename);
        }

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
}
