package eu.sblendorio.bbs.tenants;

import eu.sblendorio.bbs.core.Colors;
import eu.sblendorio.bbs.core.Keys;
import eu.sblendorio.bbs.core.PetsciiThread;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;

import static eu.sblendorio.bbs.core.Colors.WHITE;
import static eu.sblendorio.bbs.core.Keys.*;
import static org.apache.commons.lang3.StringUtils.repeat;

public class FileBrowser extends PetsciiThread {

    protected static final String SHARED_FOLDER = System.getProperty("user.home") + "/shared";

    private String currentDir = "/";

    private static final int ROWS = 10;

    @Override
    public void doLoop() throws Exception {
        write(WHITE, LOWERCASE, CASE_LOCK, HOME);
        cls();
        box(0,3,38,24);
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

}
