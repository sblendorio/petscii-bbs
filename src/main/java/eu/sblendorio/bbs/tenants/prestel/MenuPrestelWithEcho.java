package eu.sblendorio.bbs.tenants.prestel;

import eu.sblendorio.bbs.core.Utils;
import eu.sblendorio.bbs.tenants.ascii.MenuApple1;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
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
        flush();
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
    }

    public void displayMenu() {
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
    }

    @Override
    public void textDemo() throws Exception {
        List<Path> drawings = Utils.getDirContent("prestel/slideshow");
        for (Path drawing : drawings.stream().sorted(comparing(p -> p.toString().toLowerCase())).collect(toList())) {
            String filename = drawing.toString();
            if (startsWith(filename,"/")) filename = filename.substring(1);
            byte[] content = readBinaryFile(filename);
            cls();
            System.out.println(filename);
            write(content);
            flush(); resetInput();
            int ch = keyPressed(60_000);
            if (ch == '.') break;
        }
        cls();
    }
}
