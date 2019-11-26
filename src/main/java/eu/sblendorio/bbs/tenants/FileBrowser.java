package eu.sblendorio.bbs.tenants;

import eu.sblendorio.bbs.core.Colors;
import eu.sblendorio.bbs.core.Keys;
import eu.sblendorio.bbs.core.PetsciiThread;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;

import static eu.sblendorio.bbs.core.Colors.*;
import static eu.sblendorio.bbs.core.Keys.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.repeat;

public class FileBrowser extends PetsciiThread {

    protected static final String SHARED_FOLDER = System.getProperty("user.home") + "/shared";
    private String currentDir = "/";
    private static final int ROWS = 10;

    @Override
    public void doLoop() throws Exception {
        write(WHITE, LOWERCASE, CASE_LOCK, HOME);
        cls();
        write(LOGO_BYTES);
        write(BLUE);
        box(0,5,38,24);
        write(GREY3);
        displayFiles();
    }

    private void displayFiles() {
        File file = Paths.get(SHARED_FOLDER).toFile();
        File[] files = file.listFiles();
        int count = files.length;
        gotoXY(0, 6); // INSERIRE IL CASO NUMERO DI FILE DISPARI
        for (int i=0; i<count / 2; ++i) {
            for (int j=0; j<1; ++j) write(RIGHT);
            String entry = files[i].getName()+(files[i].isDirectory() ? "/" : EMPTY);
            print(entry);
            for (int j=0; j<20-(1+entry.length()+1); ++j) write(RIGHT);
            entry = files[count/2 + i].getName()+(files[count/2 + i].isDirectory() ? "/" : EMPTY);
            println(entry);
        }
    }

    public void box(int x1, int y1, int x2, int y2) {
        if (x1==x2 || y1==y2) return;
        if (x1>x2) { int t=x1; x1=x2; x2=t; }
        if (y1>y2) { int t=y1; y1=y2; y2=t; }
        gotoXY(x1, y1);
        write(176);
        for (int i=0; i<x2-x1-1; ++i) write(192);
        write(174);
        for (int j=0; j<y2-y1-1; ++j) {
            write(RETURN);
            for (int i = 0; i < x1; ++i) write(RIGHT);
            write(221);
            for (int i = 0; i < x2 - x1 - 1; ++i) write(RIGHT);
            write(221);
        }
        write(RETURN);
        for (int i = 0; i < x1; ++i) write(RIGHT);
        write(173);
        for (int i=0; i<x2-x1-1; ++i) write(192);
        write(189);

    }

    public static void main(String[] s) {
        File file = Paths.get(SHARED_FOLDER).toFile();
        Arrays.stream(file.listFiles()).forEach(x -> System.out.println(x.getName() + (x.isDirectory() ? "/" : "")));
    }

    private final byte[] LOGO_BYTES = {
            32, 18, 31, -66, 32, 32, -110, 32, 32, 32, -97, -84, -94, -94, -84, -69,
            -94, 32, 32, -94, -94, -69, 32, 32, -127, -84, -69, 32, 32, 32, 32, -69,
            32, 32, -69, 13, 18, 31, -95, -84, -110, 32, 32, 18, 32, -84, -110, 32,
            18, -97, -95, -84, -94, -95, -110, -95, 18, 32, -110, 32, 32, 18, 32, -94,
            -110, -66, 32, 32, -127, -95, 18, -95, -110, 32, 32, 32, 32, -68, -69, 18,
            -65, -110, 13, 18, 31, -95, -110, -95, 32, 32, 28, -94, -69, 32, 18, -97,
            -95, -84, -110, -66, 18, -95, -110, -95, 18, 32, -110, 32, 32, 18, 32, -94,
            -110, 32, 32, 32, 18, -127, -68, -94, -110, -65, -84, 18, -94, -110, -69, 32,
            18, -65, -110, -69, 13, 31, -68, 18, 32, -110, -94, -94, 18, 28, -94, -94,
            -110, 32, 18, -97, -95, -110, -95, 32, 18, -95, -110, -95, 18, 32, 32, -110,
            -95, 18, 32, 32, -110, -95, 32, -127, -65, 18, -68, -110, -94, 18, -65, -110,
            -68, -94, -66, 18, -65, -110, 32, -68, -69, 13, 32, 31, -68, 18, -94, -94,
            -110, 13
    };
}
