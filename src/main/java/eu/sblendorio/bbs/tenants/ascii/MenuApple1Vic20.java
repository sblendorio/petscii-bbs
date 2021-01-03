package eu.sblendorio.bbs.tenants.ascii;

public class MenuApple1Vic20 extends MenuApple1 {

    public MenuApple1Vic20() {
        super();
        setLocalEcho(false);
        clsBytes = new byte[] {
            12
        };
        screenColumns = 22;
        screenRows = 23;
    }

    @Override
    public void initBbs() throws Exception {
        Thread.sleep(2000);
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
        println("Intl.News  Game Room");
        println("---------  -----------");
        println("A-CNN      N-TicTacToe");
        println("B-BBC      O-Connect 4");
        println("C-IRNews   P-Zork I");
        println("D-VCFNews  Q-Zork II");
        println("E-8bitGuy  R-Zork III");
        println("           S-Hitchhik.");
        println();
        println("Italian News");
        println("------------");
        println("F-Televideo");
        println("G-Wired");
        println("H-Disinfor");
        println("I-IlPost      Services");
        println("J-F.Quot      --------");
        println("K-Retrocampus T-Chat");
        println("L-Butac       U-Msgs.");
        println("M-Facta       .-Logout");
    }

}
