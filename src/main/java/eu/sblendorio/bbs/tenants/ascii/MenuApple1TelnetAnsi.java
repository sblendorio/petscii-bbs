package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.Utils;

public class MenuApple1TelnetAnsi extends MenuApple1Telnet {

    @Override
    protected void banner() {
        write(readBinaryFile("ansi/Retrocampus.ans"));
    }

    @Override
    public String getCharset() {
        return "ansi";
    }

    public MenuApple1TelnetAnsi() {
        super();
        clsBytes = Utils.bytes("\033[H\033[2J");
    }

}
