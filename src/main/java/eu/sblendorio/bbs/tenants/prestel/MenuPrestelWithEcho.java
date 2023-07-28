package eu.sblendorio.bbs.tenants.prestel;

import eu.sblendorio.bbs.core.Utils;
import eu.sblendorio.bbs.tenants.ascii.MenuApple1;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.startsWith;

public class MenuPrestelWithEcho extends MenuApple1 {

    public MenuPrestelWithEcho() {
        super();
        setLocalEcho(true);
        clsBytes = new byte[] {
                12, 17
        };
    }

    public byte[] initializingBytes() {
        return new byte[] { 17 };
    }

    @Override
    public void logo() throws Exception {
        // write(0x14); // Cursor off
        cls();
        write(readBinaryFile("prestel/intro-retrocampus.cept3"));
        flush(); resetInput();
        keyPressed(12_000);
        // write(0x11); // Cursor on
    }


    @Override
    public String getCharset() {
        return "prestel";
    }

    protected void banner() {
        write(readBinaryFile("prestel/retrocampus-logo.cept3"));
    }

    @Override
    public void initBbs() throws Exception {
        Thread.sleep(2300);
        resetInput();
        this.keepAliveChar = 20; // 20 = cursor off
    }

    @Override
    public void displayMenu() throws Exception {
        cls();
        write(readBinaryFile("prestel/menu-retrocampus-alt.cept3"));
        //write(readBinaryFile("prestel/menu-retrocampus.cept3"));
        /*
            String sp = (getScreenColumns() > 40) ? "                    " : "";
            banner();
            println("International News---"+ sp +" Game Room--------");
            println("1 - CNN News         "+ sp +" N - TIC TAC TOE");
            println("2 - BBC News         "+ sp +" O - Connect Four");
            println("3 - Politico.com");
            println("4 - Al Jazeera       "+ sp +" Services---------");
            println("5 - Indie Retro News "+ sp +" X - Patrons list");
            println("6 - VCF News         "+ sp +" Y - Wifi Modem");
            println("7 - The 8-Bit Guy    "+ sp +" Z - PrestelMuseum");
            println("                     ");
            println("Italian News---------");
            println("F - Televideo RAI    ");
            println("G - Lercio           ");
            println("H - Disinformatico   ");
            println("I - Mupin.it         ");
            println("J - Fatto Quotidiano ");
            println("K - Amedeo Valoroso  ");
            println("L - Butac.it         ");
            println("M - Alessandro Albano"+ sp +"        . - Logout");
            println();
        */
        flush(); resetInput();
    }

    @Override
    public void textDemo() throws Exception {
        List<Path> drawings = Utils.getDirContent("prestel/slideshow");
        for (Path drawing : drawings.stream().sorted(comparing(p -> p.toString().toLowerCase())).collect(toList())) {
            String filename = drawing.toString();
            if (startsWith(filename,"/")) filename = filename.substring(1);
            byte[] content = readBinaryFile(filename);
            cls();
            write(content);
            flush(); resetInput();
            int ch = keyPressed(60_000);
            if (ch == '.') break;
        }
        cls();
    }

    @Override
    public void showPatrons() throws Exception {
        List<String> patrons = readTxt(System.getProperty("PATREON_LIST", System.getProperty("user.home") + File.separator + "patreon_list.txt"))
                .stream()
                .filter(StringUtils::isNotBlank)
                .map(StringUtils::trim)
                .filter(str -> !str.startsWith(";"))
                .sorted(comparing(String::toLowerCase))
                .collect(toList());

        final int PAGESIZE = 11;
        int pages = patrons.size() / PAGESIZE + (patrons.size() % PAGESIZE == 0 ? 0 : 1);

        for (int p = 0; p < pages; ++p) {
            cls();
            banner();
            println("You can support the development of this");
            println("BBS through Patreon starting with 3$ or");
            println("3.50eur per month:");
            println();
            println("https://patreon.com/FrancescoSblendorio");
            println();
            println("Patrons of this BBS");
            println("-------------------");
            for (int i = 0; i < PAGESIZE; ++i) {
                int index = (p * PAGESIZE + i);
                if (index < patrons.size())
                    println(patrons.get(index));
            }
            flush();
            resetInput();
            int ch = readKey();
            if (ch == '.') break;
        }
    }

    @Override
    public int readSingleKey() throws IOException {
        write(20); // Cursor off
        int ch = readKey();
        write(17); // Cursor on
        return ch;
    }
}
