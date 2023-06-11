package eu.sblendorio.bbs.tenants.ascii;

public class MenuApple1Vic20 extends MenuApple1 {

    public MenuApple1Vic20() {
        super();
        setLocalEcho(true);
        clsBytes = new byte[] {
            12
        };
        screenColumns = 22;
        screenRows = 23;
    }

    @Override
    public void initBbs() throws Exception {
        Thread.sleep(300);
        resetInput();
    }

    @Override
    protected void banner() {
        println("RetrocampusBBS - Vic20");
        newline();
    }

    @Override
    public void logo() throws Exception {}

    @Override
    public String rssPropertyTimeout() { return "rss.vic20.timeout"; }

    @Override
    public String rssPropertyTimeoutDefault() { return "60000"; }

    @Override
    public void displayMenu() {
        banner();
        println("Intl.News Game Room");
        println("--------- -----------");
        println("1-CNN     N-TicTacToe");
        println("2-BBC     O-Connect 4");
        println("3-AJPlus  P-Zork I");
        println("4-IRNews  Q-Zork II");
        println("5-VCFNews R-Zork III");
        println("6-8bitGuy S-Hitchhikr");
        println();
        println("Italian News");
        println("------------  Service");
        println("F-Televideo   -------");
        println("G-Wired       T-Chat");
        println("H-Disinfor    U-Msgs");
        println("I-IlPost      U-Eliza");
        println("J-F.Quot      "+(alternateLogo() ? "" : "W-ChGPT"));
        println("K-A. Valoroso "+(alternateLogo() ? "" : "X-Patre"));
        println("L-Butac       "+(alternateLogo() ? "" : "Y-Modem"));
        println("M-A.Albano    .-Exit");
    }

    @Override
    public void wifiModem() throws Exception {
        cls();
        banner();
        println("Once upon a a time,");
        println("there where dial up");
        println("BBSes. Nowadays we");
        println("have Internet but we");
        println("recreate such an");
        println("experience.");
        println();
        println("www.museo-computer.it");
        println("      /en/wifi-modem/");
        println();
        println("Get here your brand");
        println("new WiFi modem, it");
        println("uses your Internet");
        println("connection to allow");
        println("you to telnet BBSes");
        println("around the world");
        println();
        println("Press any key");
        println("-------------");
        flush(); resetInput(); readKey();
    }

}
