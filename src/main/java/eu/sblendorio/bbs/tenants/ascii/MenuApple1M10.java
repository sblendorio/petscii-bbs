package eu.sblendorio.bbs.tenants.ascii;

import static eu.sblendorio.bbs.core.Utils.STR_ALPHANUMERIC;
import static eu.sblendorio.bbs.core.Utils.setOfChars;
import java.io.IOException;

public class MenuApple1M10 extends MenuApple1 {

    public MenuApple1M10() {
        super();
        setLocalEcho(true);
        clsBytes = new byte[] {
            12
        };
        screenColumns = 40;
        screenRows = 15;
    }

    @Override
    public void initBbs() throws Exception {
        Thread.sleep(2000);
        resetInput();
    }

    @Override
    protected void banner() {
        println("BBS for M10 - by F. Sblendorio 2023");
        newline();
    }

    @Override
    public void logo() throws Exception {}

    @Override
    public String rssPropertyTimeout() { return "rss.m10.timeout"; }

    @Override
    public String rssPropertyTimeoutDefault() { return "60000"; }

    @Override
    public void displayMenu() {
        banner();
        println("International News---  Game Room-------");
        println("A - CNN News           N - TIC TAC TOE");
        println("B - BBC News           O - Connect Four");
        println("C - Indie Retro News   P - Zork I");
        println("D - VCF News           Q - Zork II");
        println("E - The 8-Bit Guy      R - Zork III");
        println("Italian News---------  S - Hitchhiker's");
        println("F - Televideo RAI");
        println("G - Lercio");
        println("H - Disinformatico");
        println("I - Mupin.it            Services");
        println("J - Fatto Quotidiano    ----------");
        println("K - Indie Campus        T - Chat");
        println("L - Butac.it            U - Private Msg");
        println("M - Alessandro Albano   . - Logout");
    }

    @Override
    public String readChoice() throws IOException {
        return readLine(setOfChars(STR_ALPHANUMERIC, "."));
    }

}
