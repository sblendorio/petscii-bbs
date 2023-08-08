package eu.sblendorio.bbs.tenants.ascii;

import static eu.sblendorio.bbs.core.Utils.bytes;

public class MenuApple1TelnetUtf8Ansi extends MenuApple1Telnet {

    @Override
    protected void banner() {
        write(readBinaryFile(alternateLogo()
            ? "ansi/RetroAcademyOnlineBbs.utf8ans"
            : "ansi/Retrocampus.utf8ans"
        ));
        write(bytes("\033[0m"));
    }

    @Override
    public String getTerminalType() {
        return "utf8";
    }

    public MenuApple1TelnetUtf8Ansi() {
        super();
        clsBytes = bytes("\033[H\033[2J");
    }

}
