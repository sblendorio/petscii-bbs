package eu.sblendorio.bbs.tenants;

import com.google.common.collect.ImmutableMap;
import eu.sblendorio.bbs.core.PetsciiThread;

import java.net.URL;
import java.nio.file.*;
import java.util.*;

import static eu.sblendorio.bbs.core.Colors.GREY3;
import static eu.sblendorio.bbs.core.Colors.WHITE;
import static eu.sblendorio.bbs.core.Keys.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public class PetsciiArtGallery extends PetsciiThread {

    public static String rootPath = "petscii-art-gallery";

    public static Map<String, String> authorDetails = ImmutableMap.of(
      "John Canady", "commodore4ever.com"
    );


    public List<Path> getDirContent(String path) throws Exception {
        List<Path> result = new ArrayList<>();
        URL jar = getClass().getProtectionDomain().getCodeSource().getLocation();
        Path jarFile = Paths.get(jar.toURI());
        FileSystem fs = FileSystems.newFileSystem(jarFile, null);
        DirectoryStream<Path> directoryStream = Files.newDirectoryStream(fs.getPath(path));
        for (Path p : directoryStream) result.add(p);
        Collections.sort(result, new Comparator<Path>() {
            @Override
            public int compare(Path o1, Path o2) {
                return o1 == null || o2 == null ? 0 : o1.getFileName().toString().compareTo(o2.getFileName().toString());
            }
        });
        return result;
    }

    @Override
    public void doLoop() throws Exception {
        List<Path> authors = getDirContent(rootPath);
        boolean randomize = false;
        int key;
        int choice;
        do {
            write(CLR, LOWERCASE, CASE_LOCK);
            write(RetroAcademy.LOGO);
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
                println(" " + authors.get(i).getFileName().toString().replaceAll("/", EMPTY));
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

    public void displayAuthor(Path p, boolean randomize) throws Exception {
        boolean statusLine = false;
        cls();
        List<Path> drawings = getDirContent(p.toString());
        if (randomize) Collections.shuffle(drawings);
        int size = drawings.size();
        int i = 0;
        while (i < size) {
            write(CLR, RETURN, CLR, UPPERCASE, REVOFF);
            String filename = drawings.get(i).toString().substring(1);
            log("PETSCII ("+i+") FILENAME=" + filename);
            writeRawFile(filename);
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

}
