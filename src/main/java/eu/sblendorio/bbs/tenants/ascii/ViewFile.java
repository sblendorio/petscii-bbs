package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;

import javax.swing.text.View;
import java.util.List;

public class ViewFile extends AsciiThread {

    private String filename="ascii/ti.txt";
    private boolean invertCase;

    public ViewFile() {
        super();
    }

    public ViewFile(String filename) {
        this(filename, false);
    }
    public ViewFile(String filename, boolean invertCase) {
        this.filename = filename;
        this.invertCase = invertCase;
    }

    @Override
    public void doLoop() throws Exception {
        cls();
        List<String> rows = readTextFile(filename);
        for (int i=0; i<rows.size(); i++) {
            String row = rows.get(i);
            if (invertCase && row != null) {
                String result = "";
                for (int j=0; j<row.length(); j++) {
                    char ch = row.charAt(j);
                    if (ch >= 'a' && ch <= 'z') {
                        ch -= 32;
                    } else if (ch >= 'A' && ch <= 'Z') {
                        ch += 32;
                    }
                    result += ch;
                }
                row = result;
            }
            print(row);
            if (row.length() != getScreenColumns() && i < rows.size()-1)
                println();
        }
        flush(); resetInput();
        readKey();
        cls();
    }
}
