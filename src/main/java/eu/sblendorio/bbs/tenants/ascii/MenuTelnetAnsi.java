package eu.sblendorio.bbs.tenants.ascii;

import static eu.sblendorio.bbs.core.Utils.bytes;

public class MenuTelnetAnsi extends MenuTelnetPureAscii {

    @Override
    protected void banner() {}

    @Override
    public String getTerminalType() {
        return "ansi";
    }

    public MenuTelnetAnsi() {
        super();
        clsBytes = bytes("\033[H\033[2J");
    }

    @Override
    public void boldOn() {
        write(bytes("\033[1m"));
    }

    @Override
    public void boldOff() {
        write(bytes("\033[0m"));
    }

    public void displayMenu() throws Exception {
        cls();
        write(readBinaryFile("ansi/RetrocampusBbsMainMenu.ans"));
    }

}
