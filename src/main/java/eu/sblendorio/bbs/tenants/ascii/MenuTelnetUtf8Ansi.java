package eu.sblendorio.bbs.tenants.ascii;

import static eu.sblendorio.bbs.core.Utils.bytes;

public class MenuTelnetUtf8Ansi extends MenuTelnetPureAscii {

    @Override
    protected void banner() {}

    @Override
    public String getTerminalType() {
        return "utf8";
    }

    @Override
    public void displayMenu() throws Exception {
        cls();
        write(readBinaryFile("ansi/RetrocampusBbsMainMenu.utf8ans"));
    }

    public MenuTelnetUtf8Ansi() {
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

}
