package eu.sblendorio.bbs.tenants.ascii;

import static eu.sblendorio.bbs.core.Utils.bytes;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

public class MenuApple1TelnetAnsi extends MenuApple1Telnet {

    @Override
    protected void banner() {
        write(readBinaryFile(alternateLogo()
            ? "ansi/RetroAcademyOnlineBbs.ans"
            : "ansi/Retrocampus.ans"
        ));
        write(bytes("\033[0m"));
    }

    @Override
    public String getTerminalType() {
        return "ansi";
    }

    public MenuApple1TelnetAnsi() {
        super();
        clsBytes = bytes("\033[H\033[2J");
    }

}
