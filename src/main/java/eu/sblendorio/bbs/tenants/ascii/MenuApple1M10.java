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
    protected String banner() { return "BBS for M10 - by F. Sblendorio 2020"; }

    @Override
    public void logo() throws Exception {}

}
