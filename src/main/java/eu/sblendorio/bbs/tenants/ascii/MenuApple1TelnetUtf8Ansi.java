package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.Utils;

public class MenuApple1TelnetUtf8Ansi extends MenuApple1Telnet {

    public String getCharset() {
        return "utf8";
    }

    public MenuApple1TelnetUtf8Ansi() {
        super();
        clsBytes = Utils.bytes("\033[H\033[2J");
    }

}
