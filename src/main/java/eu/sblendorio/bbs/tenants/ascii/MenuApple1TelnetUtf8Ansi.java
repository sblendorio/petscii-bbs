package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.Utils;

public class MenuApple1TelnetUtf8Ansi extends MenuApple1Telnet {

    @Override
    protected void banner() {
        write(readBinaryFile("ansi/Retrocampus.utf8ans"));
    }

    @Override
    public String getCharset() {
        return "utf8";
    }

    public MenuApple1TelnetUtf8Ansi() {
        super();
        clsBytes = Utils.bytes("\033[H\033[2J");
    }

}
