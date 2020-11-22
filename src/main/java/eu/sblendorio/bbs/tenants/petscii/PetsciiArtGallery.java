package eu.sblendorio.bbs.tenants.petscii;

import static eu.sblendorio.bbs.core.PetsciiColors.*;
import static eu.sblendorio.bbs.core.PetsciiKeys.CASE_LOCK;
import static eu.sblendorio.bbs.core.PetsciiKeys.CLR;
import static eu.sblendorio.bbs.core.PetsciiKeys.HOME;
import static eu.sblendorio.bbs.core.PetsciiKeys.LOWERCASE;
import static eu.sblendorio.bbs.core.PetsciiKeys.RETURN;
import static eu.sblendorio.bbs.core.PetsciiKeys.REVOFF;
import static eu.sblendorio.bbs.core.PetsciiKeys.REVON;
import static eu.sblendorio.bbs.core.PetsciiKeys.UPPERCASE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.ObjectUtils.notEqual;
import static org.apache.commons.lang3.StringUtils.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import eu.sblendorio.bbs.core.PetsciiKeys;
import eu.sblendorio.bbs.core.bbstype.PetsciiThread;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class PetsciiArtGallery extends PetsciiThread {

    private static final String ROOT_PATH = "petscii-art-gallery";
    private static final ClassLoader NULL_CLASSLOADER = null;

    public List<Path> getDirContent(String path) throws URISyntaxException, IOException {
        List<Path> result = new ArrayList<>();
        URL jar = getClass().getProtectionDomain().getCodeSource().getLocation();
        Path jarFile = Paths.get(jar.toURI());
        try (FileSystem fs = FileSystems.newFileSystem(jarFile, NULL_CLASSLOADER);
            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(fs.getPath(path))) {
            for (Path p : directoryStream) {
                result.add(p);
            }

            result.sort((o1, o2) -> o1 == null || o2 == null ? 0 :
                    o1.getFileName().toString().compareTo(o2.getFileName().toString()));
            return result;
        }
    }

    @Override
    public void doLoop() throws Exception {
        List<Path> authors = getDirContent(ROOT_PATH);
        boolean randomize = false;
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
            newline();
            println("During slideshow, use:");
            print("  key "); write(REVON); print(" . "); write(REVOFF); println(" to STOP");
            print("  use "); write(REVON); print(" - "); write(REVOFF); println(" to go to previous picture,");
            print("  and "); write(REVON); print(" X "); write(REVOFF); println(" to toggle statusline.");
            newline();
            println("Select your favourite artist:");
            newline();
            for (int i = 0; i < authors.size(); ++i) {
                print(" ");
                write(REVON, WHITE);
                print(" " + (i + 1) + " ");
                write(REVOFF, GREY3);
                println(" " + authors.get(i).getFileName().toString().replace("/", EMPTY));
            }
            print(" "); write(REVON, WHITE); print(" . "); write(REVOFF, GREY3); println(" Go back");
            newline();
            print("> ");
            do {
                flush(); resetInput(); key = readKey();
                choice = 0;
                if (key == 'r' || key == 'R') randomize = !randomize;
                if (key >= '1' && key <= '9') choice = key - '0';
                if (choice > authors.size()) choice = 0;
            } while (choice == 0 && key != '.' && key != 'r' && key != 'R');
            if (choice > 0) displayAuthor(authors.get(choice - 1), randomize);
        } while (key != '.');
    }

    private void displayAuthor(Path p, boolean randomize) throws IOException, URISyntaxException, ParseException {
        boolean statusLine = false;
        cls();
        List<Path> drawings = getDirContent(p.toString());
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
            int key = readKey();
            if (key == '.')
                break;
            else if (key == 'x' || key == 'X')
                statusLine = !statusLine;
            else if (key == '-' && i > 0)
                --i;
            else
                ++i;
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
