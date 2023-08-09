package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;

import javax.swing.text.View;
import java.util.List;

public class ViewFile extends AsciiThread {

    private String filename="ascii/ti.txt";

    public ViewFile() {
        super();
    }

    public ViewFile(String filename) {
        this.filename = filename;
    }

    @Override
    public void doLoop() throws Exception {
        cls();
        List<String> rows = readTextFile(filename);
        for (String row : rows) {
            print(row);
            if (row.length() != getScreenColumns())
                println();
        }
        flush(); resetInput();
        readKey();
        cls();
    }
}
