package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.Hidden;
import static eu.sblendorio.bbs.core.PetsciiColors.BLACK;
import static eu.sblendorio.bbs.core.PetsciiColors.BLUE;
import static eu.sblendorio.bbs.core.PetsciiColors.BROWN;
import static eu.sblendorio.bbs.core.PetsciiColors.CYAN;
import static eu.sblendorio.bbs.core.PetsciiColors.GREEN;
import static eu.sblendorio.bbs.core.PetsciiColors.GREY1;
import static eu.sblendorio.bbs.core.PetsciiColors.GREY2;
import static eu.sblendorio.bbs.core.PetsciiColors.GREY3;
import static eu.sblendorio.bbs.core.PetsciiColors.LIGHT_BLUE;
import static eu.sblendorio.bbs.core.PetsciiColors.LIGHT_GREEN;
import static eu.sblendorio.bbs.core.PetsciiColors.LIGHT_RED;
import static eu.sblendorio.bbs.core.PetsciiColors.ORANGE;
import static eu.sblendorio.bbs.core.PetsciiColors.PURPLE;
import static eu.sblendorio.bbs.core.PetsciiColors.RED;
import static eu.sblendorio.bbs.core.PetsciiColors.WHITE;
import static eu.sblendorio.bbs.core.PetsciiColors.YELLOW;
import eu.sblendorio.bbs.core.PetsciiKeys;
import static eu.sblendorio.bbs.core.PetsciiKeys.CASE_LOCK;
import static eu.sblendorio.bbs.core.PetsciiKeys.CLR;
import static eu.sblendorio.bbs.core.PetsciiKeys.HOME;
import static eu.sblendorio.bbs.core.PetsciiKeys.LOWERCASE;
import static eu.sblendorio.bbs.core.PetsciiKeys.RETURN;
import static eu.sblendorio.bbs.core.PetsciiKeys.REVOFF;
import static eu.sblendorio.bbs.core.PetsciiKeys.REVON;
import static eu.sblendorio.bbs.core.PetsciiKeys.UPPERCASE;
import eu.sblendorio.bbs.core.PetsciiThread;
import eu.sblendorio.bbs.core.Utils;
import java.io.IOException;
import java.net.URISyntaxException;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.ObjectUtils.notEqual;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.startsWith;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Hidden
public class PetsciiArtGallery extends PetsciiThread {

    private static final String ROOT_PATH = "petscii-art-gallery";
    private static final long TIMEOUT = NumberUtils.toLong(System.getProperty("petscii_art_timeout", "15000"));

    @Override
    public void doLoop() throws Exception {
        List<Path> authors = Utils.getDirContent(ROOT_PATH);
        boolean randomize = false;
        boolean slideshow = false;
        int key;
        int choice;
        do {
            write(CLR, LOWERCASE, CASE_LOCK, HOME);
            write(LOGO_BYTES);
            write(GREY3);
            newline();
            print("Press "); write(REVON); print(" R "); write(REVOFF); print(" to toggle randomize (now ");
            write(WHITE); print(randomize ? "ON" : "OFF"); write(GREY3);
            println(")" + (randomize ? " " : ""));
            print("Press "); write(REVON); print(" S "); write(REVOFF); print(" to toggle slideshow (now ");
            write(WHITE); print(slideshow ? "ON" : "OFF"); write(GREY3);
            println(")" + (slideshow ? " " : ""));
            newline();
            println("During show, use:");
            print("  key "); write(REVON); print(" . "); write(REVOFF); println(" to STOP");
            print("  use "); write(REVON); print(" - "); write(REVOFF); println(" to go to previous picture,");
            print("  and "); write(REVON); print(" X "); write(REVOFF); println(" to toggle statusline.");
            newline();
            println("Select your favourite artist:");
            newline();
            print(" "); write(REVON, WHITE); print(" 0 "); write(REVOFF, GREY3); println(" Le Ossa - Mantrasoft");
            for (int i = 0; i < authors.size(); ++i) {
                print(" ");
                write(REVON, WHITE);
                print(" " + (i + 1) + " ");
                write(REVOFF, GREY3);
                println(" " + authors.get(i).getFileName().toString().replace("/", EMPTY));
            }
            print(" "); write(REVON, WHITE); print(" . "); write(REVOFF, GREY3); println(" Go back");
            print("> ");
            do {
                flush(); resetInput(); key = readKey();
                choice = -1;
                if (key == 's' || key == 'S') slideshow = !slideshow;
                if (key == 'r' || key == 'R') randomize = !randomize;
                if (key >= '0' && key <= '9') choice = key - '0';
                if (choice > authors.size()) choice = -1;
            } while (choice == -1 && key != '.' && key != 'r' && key != 'R' && key != 's' && key != 'S');
            if (choice == 0) launch(new Ossa());
            else if (choice > 0) displayAuthor(authors.get(choice - 1), randomize, slideshow);
        } while (key != '.');
    }

    private void displayAuthor(Path p, boolean randomize, boolean slideshow)
            throws IOException, URISyntaxException, ParseException {
        long timeout = slideshow ? TIMEOUT : -1;
        boolean statusLine = false;
        cls();
        List<Path> drawings = Utils.getDirContent(p.toString());
        if (randomize) Collections.shuffle(drawings);
        int size = drawings.size();
        int i = 0;
        while (i < size) {
            write(CLR, RETURN, CLR, UPPERCASE, REVOFF);
            String filename = drawings.get(i).toString();
            if (startsWith(filename,"/")) filename = filename.substring(1);
            log("PETSCII ("+i+") FILENAME=" + filename);
            if (lowerCase(filename).endsWith(".json")) {
                final String content = new String(readBinaryFile(filename), UTF_8);
                printPetmateJson(content);
            } else {
                writeRawFile(filename);
            }
            if (statusLine) {
                write(HOME, WHITE, REVOFF);
                print((i+1)+"/"+size);
            }
            flush();
            resetInput();
            int key = keyPressed(timeout);
            if (key == '.')
                break;
            else if (key == 'x' || key == 'X')
                statusLine = !statusLine;
            else if (key == '-' && i > 0)
                --i;
            else
                ++i;
            if (i >= size) {
                i = 0;
                if (randomize) Collections.shuffle(drawings);
            }
        }
        write(CLR, RETURN, CLR, LOWERCASE, REVOFF);
    }

    private void printPetmateJson(final String fileContent) throws ParseException {
        printPetmateJson(fileContent, 0, 0);
    }

    // This function "printPetmateJson" (C) CityXen
    private void printPetmateJson(final String fileContent, final Integer xLoc, final Integer yLoc) throws ParseException {
        final Map<Integer, Integer> codeHash = new HashMap<>();
        for (int i = 0; i < 32; i++)    { codeHash.put(i, i + 64); }
        for (int i = 64; i < 96; i++)   { codeHash.put(i, i + 128); }
        for (int i = 96; i < 128; i++)  { codeHash.put(i, i + 64); }
        for (int i = 128; i < 160; i++) { codeHash.put(i, i - 64); }
        for (int i = 160; i < 192; i++) { codeHash.put(i, i - 128); }
        for (int i = 224; i < 256; i++) { codeHash.put(i, i - 64); }

        final int[] colorCodes = {
                BLACK, WHITE, RED, CYAN, PURPLE, GREEN, BLUE, YELLOW, ORANGE,
                BROWN, LIGHT_RED, GREY1, GREY2, LIGHT_GREEN, LIGHT_BLUE, GREY3
        };

        final Object jpars = new JSONParser().parse(fileContent);
        final JSONObject json = (JSONObject) jpars;
        final JSONArray framebufs = (JSONArray) json.get("framebufs");
        final Object[] screencodes = ((JSONArray) ((JSONObject) framebufs.get(0)).get("screencodes")).toArray();
        final Object[] colors = ((JSONArray) ((JSONObject) framebufs.get(0)).get("colors")).toArray();
        final String charset = (String) ((JSONObject) framebufs.get(0)).get("charset");
        final Long width = (Long) ((JSONObject) framebufs.get(0)).get("width");
        final Long height = (Long) ((JSONObject) framebufs.get(0)).get("height");

        write("upper".equals(charset) ? UPPERCASE : LOWERCASE);
        write(PetsciiKeys.CASE_LOCK, PetsciiKeys.HOME);
        gotoXY(xLoc, yLoc);

        Integer outColor;
        Integer lastColor = null;
        boolean reverseStatus = false;
        Integer outCode;
        Integer compCode;
        int lineCounter = 0;
        int lineCounter2 = 0;
        int heightCounter = 0;
        int drawSize = screencodes.length;

        for (int i = 0; i < drawSize; i++) {
            if (width < 40) {
                if (lineCounter == width) {
                    write(PetsciiKeys.DOWN);
                    for (int j=0; j<width; j++) write(PetsciiKeys.LEFT);
                    lineCounter=0;
                }
            }

            outColor = colorCodes[(int) (long) colors[i]];
            if (notEqual(lastColor, outColor)) {
                write(outColor);
                lastColor = outColor;
            }

            outCode = (int) (long) screencodes[i];
            if (outCode > 127) {
                if (!reverseStatus) {
                    write(PetsciiKeys.REVON);
                    reverseStatus = true;
                }
            } else {
                if (reverseStatus) {
                    write(PetsciiKeys.REVOFF);
                    reverseStatus = false;
                }
            }

            if (outCode == 34) {
                write(34, 34, 20);
            } else {
                compCode = codeHash.get(outCode);
                write(defaultIfNull(compCode, outCode));
            }

            lineCounter++;
            lineCounter2++;
            if (lineCounter2 == width) {
                lineCounter2 = 0;
                heightCounter++;
                if (heightCounter > height) {
                    break;
                }
            }
        }
    }

    protected static final byte[] LOGO_BYTES = new byte[] {
        18, 30, 32, -94, -68, 28, -95, -84, -94, -110, -66, 18, -97, -94, 32, -94,
        -110, 31, -84, 18, -84, -94, -68, -110, -98, -84, 18, -84, -94, -68, -110, -127,
        -68, 18, 32, -110, -66, 18, -104, -69, -84, -110, 32, 32, 32, -106, -84, -95,
        32, 32, 32, -102, -84, -94, -66, 32, 32, -101, -92, -92, -92, 13, 18, 30,
        32, -110, -94, 18, -84, 28, -95, -84, -94, -110, 32, 32, 18, -97, 32, -110,
        32, 32, 18, 31, -94, -68, -110, -69, 18, -98, -95, -110, -95, 32, 32, 32,
        18, -127, 32, -110, 32, 18, -104, -95, -110, -95, 32, 32, -106, -84, 18, -68,
        -110, -95, 18, -103, -65, -69, -110, -66, -102, -84, -66, 18, -94, -110, 32, -101,
        -92, -92, -92, -92, 13, 18, 30, 32, -110, 32, 32, 18, 28, -95, -68, -110,
        -94, -69, 32, 18, -97, 32, -110, 32, 31, -68, 18, -68, -110, -94, 18, -84,
        -110, -98, -68, 18, -68, -110, -94, 18, -84, -110, -127, -84, 18, 32, -110, -69,
        18, -104, -66, -68, -110, 32, -106, -94, -66, -84, 18, -68, -110, 32, 18, -103,
        -68, -110, -69, -102, -65, 18, -65, -110, 32, -101, -92, -92, -92, -92, -92, 13
    };

}
