package eu.sblendorio.bbs.tenants;

import com.google.common.collect.ImmutableMap;
import eu.sblendorio.bbs.core.PetsciiThread;

import java.net.URL;
import java.nio.file.*;
import java.util.*;

import static eu.sblendorio.bbs.core.Colors.GREY3;
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
        int key;
        int choice;
        do {
            write(CLR, LOWERCASE, CASE_LOCK);
            write(RetroAcademy.LOGO);
            write(GREY3);
            newline();
            println("Select your favourite artist");
            print("During slideshow, press "); write(REVON); print(" . "); write(REVOFF); println(" to STOP");
            newline();
            for (int i = 0; i < authors.size(); ++i) {
                write(REVON);
                print(" " + (i + 1) + " ");
                write(REVOFF);
                println(" " + authors.get(i).getFileName().toString().replaceAll("/", EMPTY));
            }
            write(REVON); print(" . "); write(REVOFF); println(" Go back");
            newline();
            print("> ");
            do {
                flush(); resetInput(); key = readKey();
                choice = 0;
                if (key >= '1' && key <= '9') choice = key - '0';
                if (choice > authors.size()) choice = 0;
            } while (choice == 0 && key != '.');
            if (choice > 0) displayAuthor(authors.get(choice - 1));
        } while (key != '.');
    }

    public void displayAuthor(Path p) throws Exception {
        cls();
        List<Path> drawings = getDirContent(p.toString());
        int i = 0;
        while (i < drawings.size()) {
            write(CLR, UPPERCASE);
            String filename = drawings.get(i).toString().substring(1);
            log("PETSCII FILENAME=" + filename);
            writeRawFile(filename);
            flush();
            resetInput();
            int key = readKey();
            if (key == '.')
                break;
            else if (key == '-' && i > 0)
                --i;
            else
                ++i;
        }
    }

}
