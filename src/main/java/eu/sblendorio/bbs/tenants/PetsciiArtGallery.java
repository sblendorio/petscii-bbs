package eu.sblendorio.bbs.tenants;

import com.google.common.collect.ImmutableMap;
import eu.sblendorio.bbs.core.CbmInputOutput;
import eu.sblendorio.bbs.core.PetsciiThread;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static eu.sblendorio.bbs.core.Keys.LOWERCASE;
import static eu.sblendorio.bbs.core.Keys.UPPERCASE;
import static eu.sblendorio.bbs.core.Keys.CASE_LOCK;
import static eu.sblendorio.bbs.core.Keys.CLR;
import static org.apache.commons.lang3.StringUtils.countMatches;

public class PetsciiArtGallery extends PetsciiThread {


    public static Map<String, String> authorDetails = ImmutableMap.of(
      "John Canady", "commodore4ever.com"
    );

    public static void main(String s[]) throws Exception {
        PetsciiArtGallery m= new PetsciiArtGallery();
        List<Path> res = m.getDirContent("petscii-art-gallery/John Canady");
        for (Path p: res) System.out.println("* "+p);
    }

    public List<Path> getDirContent(String path) throws Exception {
        List<Path> result = new LinkedList<>();
        URL jar = getClass().getProtectionDomain().getCodeSource().getLocation();
        Path jarFile = Paths.get(jar.toURI());
        FileSystem fs = FileSystems.newFileSystem(jarFile, null);
        DirectoryStream<Path> directoryStream = Files.newDirectoryStream(fs.getPath(path));
        for (Path p : directoryStream) result.add(p);
        Collections.sort(result, new Comparator<Path>() {
            @Override
            public int compare(Path o1, Path o2) {
                return o1 == null || o2 == null ? 0 :
                        o1.getFileName().toString().compareTo(o2.getFileName().toString());
            }
        });
        return result;
    }



    @Override
    public void doLoop() throws Exception {
        List<String> authors = new LinkedList<>();
        write(CLR, UPPERCASE, CASE_LOCK);
        System.out.println("authors="+authors);
        for (String author: authors) {
            cls();
            System.out.println("author="+author);
            println("author="+author);
            resetInput();
            int key = readKey();
            if (key == '.') break;
        }
        write(CLR, LOWERCASE);
    }



}
