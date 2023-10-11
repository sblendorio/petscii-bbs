package eu.sblendorio.bbs.tenants.ascii;

import static eu.sblendorio.bbs.core.Utils.bytes;

public class MenuTelnetAnsi extends MenuTelnetPureAscii {

    @Override
    protected void banner() {
        write(readBinaryFile("ansi/Retrocampus.ans"));
        write(bytes("\033[0m"));
    }

    @Override
    public String getTerminalType() {
        return "ansi";
    }

    public MenuTelnetAnsi() {
        super();
        clsBytes = bytes("\033[H\033[2J");
    }

}
