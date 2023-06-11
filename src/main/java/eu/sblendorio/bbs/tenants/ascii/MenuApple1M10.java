package eu.sblendorio.bbs.tenants.ascii;

import static eu.sblendorio.bbs.core.Utils.STR_ALPHANUMERIC;
import static eu.sblendorio.bbs.core.Utils.setOfChars;
import java.io.IOException;
import java.util.Calendar;

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
        println("BBS for M10 - by F. Sblendorio " + Calendar.getInstance().get(Calendar.YEAR));
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
        println("1 - CNN News           Game Room-------");
        println("2 - BBC News           N - TIC TAC TOE");
        println("3 - Al Jazeera         O - Connect Four");
        println("4 - Indie Retro News   P - Zork I");
        println("5 - VCF News           Q - Zork II");
        println("6 - The 8-Bit Guy      R - Zork III");
        println("Italian News---------  S - Hitchhiker's");
        println("F - Televideo RAI      Services--------");
        println("G - Lercio             T - Chat");
        println("H - Disinformatico     U - Private Msg");
        println("I - Mupin.it           V - Eliza");
        println("J - Fatto Quotidiano   " + (alternateLogo() ? "" : "W - ChatGPT"));
        println("K - Amedeo Valoroso    " + (alternateLogo() ? "" : "X - Patron list"));
        println("L - Butac.it           " + (alternateLogo() ? "" : "Y - Wifi Modem"));
        println("M - Alessandro Albano  . - Logout");
    }

    @Override
    public String readChoice() throws IOException {
        return readLine(setOfChars(STR_ALPHANUMERIC, "."));
    }

}
