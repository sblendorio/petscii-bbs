package eu.sblendorio.bbs.tenants.mixed;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.tenants.ascii.MenuApple1;
import eu.sblendorio.bbs.tenants.ascii.MenuApple1Telnet;
import eu.sblendorio.bbs.tenants.petscii.Menu64;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

public class StdChoice extends AsciiThread {
    private static final String IP_FOR_ALTERNATE_LOGO = System.getProperty("alternate.logo.ip", "none");
    private static final int PORT_FOR_ALTERNATE_LOGO = toInt(System.getProperty("alternate.logo.port", "-1"));

    @Override
    public void doLoop() throws Exception {
        int ch;
        String banner = "WELCOME TO " + (alternateLogo() ? "RETROACADEMY" : "RETROCAMPUS") + " BBS";
        cls();
        println(banner);
        println(StringUtils.repeat('-', banner.length()));
        newline();
        println("CHOOSE YOUR SYSTEM:");
        newline();
        println("1- COMMODORE PETSCII 40X25 -> PORT 6510");
        println("2- APPLE 1/II NOECHO 40X24 -> PORT 6502");
        println("2- APPLE 2/II W/ECHO 40X24 -> PORT 6503");
        println("4- TELNET     ECHO - 80X24 -> PORT 8086");
        newline();
        println("PRESS ENTER TO CLOSE CONNECTION");
        newline();
        print(">");
        resetInput();
        ch = readKey();
        newline();
        if (ch == '1') { launch(new Menu64());
        } else if (ch == '2') { launch(new MenuApple1(false));
        } else if (ch == '3') { launch(new MenuApple1(true));
        } else if (ch == '4') { launch(new MenuApple1Telnet());
        }
    }

    private boolean alternateLogo() {
        return IP_FOR_ALTERNATE_LOGO.equals(serverAddress.getHostAddress())
            || serverPort == PORT_FOR_ALTERNATE_LOGO;
    }

}
