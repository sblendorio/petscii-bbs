package eu.sblendorio.bbs.tenants.mixed;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.tenants.ascii.MenuApple1;
import eu.sblendorio.bbs.tenants.ascii.MenuApple1M10;
import eu.sblendorio.bbs.tenants.ascii.MenuApple1Telnet;
import eu.sblendorio.bbs.tenants.ascii.MenuApple1TelnetAnsi;
import eu.sblendorio.bbs.tenants.ascii.MenuApple1TelnetUtf8Ansi;
import eu.sblendorio.bbs.tenants.ascii.MenuApple1Vic20;
import eu.sblendorio.bbs.tenants.petscii.Menu64;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

public class StdChoice extends AsciiThread {
    private static final String IP_FOR_ALTERNATE_LOGO = System.getProperty("alternate.logo.ip", "none");
    private static final int PORT_FOR_ALTERNATE_LOGO = toInt(System.getProperty("alternate.logo.port", "-1"));

    @Override
    public void initBbs() throws Exception {
        Thread.sleep(2000);
        resetInput();
    }

    @Override
    public void doLoop() throws Exception {
        int ch;
        String banner = (alternateLogo() ? "RETROACADEMY" : "RETROCAMPUS") + " BBS";
        write(12);
        println(banner);
        println(StringUtils.repeat('-', banner.length()));
        newline();
        println("SUPPORTED SYSTEMS:");
        newline();
        println("1- CBM PETSCII  W/ECHO   40X25 (6510)");
        println("2- APPLE-1/II   NOECHO   40X24 (6502)");
        println("3- APPLE-1/II   W/ECHO   40X24 (6503)");
        println("4- PURE ASCII   W/ECHO   80X24 (8000)");
        println("5- DOS CP 437   W/ECHO   80X24 (8088)");
        println("6- TELNET LINUX W/ECHO   80X24 (8086)");
        println("7- VIC-20 ASCII W/ECHO   22X23 (6561)");
        println("8- OLIVETTI M10 W/ECHO   40X15 (8085)");
        newline();
        println("PLEASE SELECT A NUMBER FROM 1 TO 8");
        println("PRESS ENTER TO CLOSE  CONNECTION");
        newline();
        print(">");
        flush();
        resetInput();
        do {
            ch = readKey();
        } while (!isValidKey(ch));
        newline();
        if (ch == '1') launch(new Menu64());
        else if (ch == '2') launch(new MenuApple1(false));
        else if (ch == '3') launch(new MenuApple1(true));
        else if (ch == '4') launch(new MenuApple1Telnet());
        else if (ch == '5') launch(new MenuApple1TelnetAnsi());
        else if (ch == '6') launch(new MenuApple1TelnetUtf8Ansi());
        else if (ch == '7') launch(new MenuApple1Vic20());
        else if (ch == '8') launch(new MenuApple1M10());
    }

    private boolean isValidKey(int ch) {
        return (ch >= '#' && ch <= 127)
            || ch == 13 || ch == 10;
    }

    private boolean alternateLogo() {
        return IP_FOR_ALTERNATE_LOGO.equals(serverAddress.getHostAddress())
            || serverPort == PORT_FOR_ALTERNATE_LOGO;
    }

}
