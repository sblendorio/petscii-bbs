package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.Utils;
import static eu.sblendorio.bbs.core.Utils.bytes;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

public class MenuApple1TelnetAnsi extends MenuApple1Telnet {

    private static final String IP_FOR_ALTERNATE_LOGO = System.getProperty("alternate.logo.ip", "none");
    private static final int PORT_FOR_ALTERNATE_LOGO = toInt(System.getProperty("alternate.logo.port", "-1"));

    private boolean alternateLogo() {
        return IP_FOR_ALTERNATE_LOGO.equals(serverAddress.getHostAddress())
            || serverPort == PORT_FOR_ALTERNATE_LOGO;
    }

    @Override
    protected void banner() {
        write(readBinaryFile(alternateLogo()
            ? "ansi/RetroAcademyOnlineBbs.ans"
            : "ansi/Retrocampus.ans"
        ));
        write(bytes("\033[0m"));
    }

    @Override
    public String getCharset() {
        return "ansi";
    }

    public MenuApple1TelnetAnsi() {
        super();
        clsBytes = bytes("\033[H\033[2J");
    }

}
