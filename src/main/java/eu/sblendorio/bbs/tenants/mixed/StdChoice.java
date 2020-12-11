package eu.sblendorio.bbs.tenants.mixed;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.tenants.ascii.MenuApple1;
import eu.sblendorio.bbs.tenants.petscii.Menu64;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

public class StdChoice extends AsciiThread {
    private static final String IP_FOR_ALTERNATE_LOGO = System.getProperty("alternate.logo.ip", "none");
    private static final int PORT_FOR_ALTERNATE_LOGO = toInt(System.getProperty("alternate.logo.port", "-1"));

    @Override
    public void doLoop() throws Exception {
        int ch;
        String banner = "Welcome to " + (alternateLogo() ? "Retroacademy" : "Retrocampus") + " BBS:";
        newline();
        newline();
        println(banner);
        println(StringUtils.repeat('-', banner.length()));
        println("Choose your system:");
        println();
        println("1- Commodore 64 - PETSCII    - 40x25");
        println("2- Apple I/II/+ - Pure ASCII - 40x25");
        println(".- Exit");
        resetInput();
        ch = readKey();
        if (ch == '1') {
            launch(new Menu64());
        } else if (ch == '2') {
            launch(new MenuApple1());
        }
    }

    private boolean alternateLogo() {
        return IP_FOR_ALTERNATE_LOGO.equals(serverAddress.getHostAddress())
            || serverPort == PORT_FOR_ALTERNATE_LOGO;
    }

}
