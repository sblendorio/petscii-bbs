package eu.sblendorio.bbs.tenants.ascii;

import static eu.sblendorio.bbs.core.Utils.bytes;

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
