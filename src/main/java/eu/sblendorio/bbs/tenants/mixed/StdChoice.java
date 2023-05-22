package eu.sblendorio.bbs.tenants.mixed;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.tenants.ascii.MenuApple1;
import eu.sblendorio.bbs.tenants.ascii.MenuApple1M10;
import eu.sblendorio.bbs.tenants.ascii.MenuApple1Telnet;
import eu.sblendorio.bbs.tenants.ascii.MenuApple1TelnetAnsi;
import eu.sblendorio.bbs.tenants.ascii.MenuApple1TelnetAnsiNoEcho;
import eu.sblendorio.bbs.tenants.ascii.MenuApple1TelnetNoEcho;
import eu.sblendorio.bbs.tenants.ascii.MenuApple1TelnetUtf8Ansi;
import eu.sblendorio.bbs.tenants.ascii.MenuApple1Vic20;
import eu.sblendorio.bbs.tenants.ascii.MenuApple1WithEcho;
import eu.sblendorio.bbs.tenants.petscii.Menu64;
import eu.sblendorio.bbs.tenants.minitel.MenuMinitelWithEcho;
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
        if (alternateLogo()) { println();println();println("moved to bbs.retrocampus.com");println(); keyPressed(10_000); return; }
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
        println("5- PURE ASCII   NOECHO   80X24 (8001)");
        println("6- DOS CP 437   W/ECHO   80X24 (8088)");
        println("7- DOS CP 437   NOECHO   80X24 (8089)");
        println("8- TELNET LINUX W/ECHO   80X24 (8086)");
        println("9- VIC-20 ASCII W/ECHO   22X23 (6561)");
        println("0- OLIVETTI M10 W/ECHO   40X15 (8085)");
        println("M- MINITEL      W/ECHO   40X24 (1651)");
        newline();
        println("PLEASE SELECT A SYSTEM");
        println("PRESS ENTER TO CLOSE  CONNECTION");
        newline();
        print(">");
        flush();
        resetInput();
        do {
            ch = keyPressed(60_000);
        } while (!isValidKey(ch));
        newline();
        ch = ch | 32; // lowercase;
        if (ch == '1') launch(new Menu64());
        else if (ch == '2') launch(new MenuApple1(false));
        else if (ch == '3') launch(new MenuApple1WithEcho());
        else if (ch == '4') launch(new MenuApple1Telnet());
        else if (ch == '5') launch(new MenuApple1TelnetNoEcho());
        else if (ch == '6') launch(new MenuApple1TelnetAnsi());
        else if (ch == '7') launch(new MenuApple1TelnetAnsiNoEcho());
        else if (ch == '8') launch(new MenuApple1TelnetUtf8Ansi());
        else if (ch == '9') launch(new MenuApple1Vic20());
        else if (ch == '0') launch(new MenuApple1M10());
        else if (ch == 'm') launch(new MenuMinitelWithEcho());
    }

    private boolean isValidKey(int ch) {
        return (ch >= '#' && ch <= 127)
            || ch == 13 || ch == 10 || ch == -1;
    }

    private boolean alternateLogo() {
        return IP_FOR_ALTERNATE_LOGO.equals(serverAddress.getHostAddress())
            || serverPort == PORT_FOR_ALTERNATE_LOGO;
    }

}
