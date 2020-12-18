package eu.sblendorio.bbs.tenants.mixed;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.tenants.ascii.MenuApple1;
import eu.sblendorio.bbs.tenants.ascii.MenuApple1M10;
import eu.sblendorio.bbs.tenants.ascii.MenuApple1Telnet;
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
        String banner = "WELCOME TO " + (alternateLogo() ? "RETROACADEMY" : "RETROCAMPUS") + " BBS";
        write(12);
        println(banner);
        println(StringUtils.repeat('-', banner.length()));
        newline();
        println("CHOOSE SYSTEM:");
        newline();
        println("1- COMMODORE PETSCII   40X25  (6510)");
        println("2- APPLE-1/II   NOECHO 40X24  (6502)");
        println("3- APPLE-1/II   W/ECHO 40X24  (6503)");
        println("4- TELNET       W/ECHO 80X24  (8086)");
        println("5- OLIVETTI M10 W/ECHO 40X15  (8085)");
        newline();
        println("PLEASE SELECT WITH NUMBERS FROM 1 TO 5");
        println("PRESS ENTER TO CLOSE CONNECTION");
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
        else if (ch == '5') launch(new MenuApple1M10());
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
