package eu.sblendorio.bbs.tenants.ascii;

public class MenuApple1M10 extends MenuApple1 {

    public MenuApple1M10() {
        super();
        setLocalEcho(true);
        clsBytes = new byte[] {
            13, 10,
            13, 10,
            13, 10
        };
        screenColumns = 40;
        screenRows = 15;
    }

    @Override
    public void initBbs() throws Exception {
        Thread.sleep(2000L);
        resetInput();
    }

    @Override
    protected String banner() { return "BBS for M10 - by F. Sblendorio 2020"; }

    @Override
    public void logo() throws Exception {}

    @Override
    public String rssPropertyTimeout() { return "rss.m10.timeout"; }

    @Override
    public String rssPropertyTimeoutDefault() { return "60000"; }

    @Override
    public void displayMenu() {
        println(banner());
        println();
        println("International News---  Game Room-------");
        println("A - CNN News           N - TIC TAC TOE");
        println("B - BBC News           O - Connect Four");
        println("C - Indie Retro News   P - Zork I");
        println("D - VCF News           Q - Zork II");
        println("E - The 8-Bit Guy      R - Zork III");
        println("Italian News---------  S - Hitchhiker's");
        println("F - Televideo RAI");
        println("G - Wired Italia");
        println("H - Disinformatico");
        println("I - Il Post             Services");
        println("J - Fatto Quotidiano    ----------");
        println("K - Retrocampus         T - Chat");
        println("L - Butac.it            U - Private Msg");
        println("M - Facta.news          . - Logout");
    }

}
