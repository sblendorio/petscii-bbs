package eu.sblendorio.bbs.tenants.ascii;

import static eu.sblendorio.bbs.core.Utils.bytes;

public class MenuTelnetUtf8Ansi extends MenuTelnetPureAscii {

    @Override
    protected void banner() {
        write(readBinaryFile("ansi/Retrocampus.utf8ans"));
        write(bytes("\033[0m"));
    }

    @Override
    public String getTerminalType() {
        return "utf8";
    }

    public MenuTelnetUtf8Ansi() {
        super();
        clsBytes = bytes("\033[H\033[2J");
    }

}
