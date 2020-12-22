package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.Utils;

public class MenuApple1TelnetAnsi extends MenuApple1Telnet {

    public String getCharset() {
        return "ascii";
    }

    public MenuApple1TelnetAnsi() {
        super();
        clsBytes = Utils.bytes("\033[H\033[2J");
    }

}
