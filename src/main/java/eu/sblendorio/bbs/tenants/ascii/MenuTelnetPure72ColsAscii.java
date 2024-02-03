package eu.sblendorio.bbs.tenants.ascii;

public class MenuTelnetPure72ColsAscii extends MenuTelnetPureAscii {

    public MenuTelnetPure72ColsAscii() {
        super();
        setLocalEcho(true);
        clsBytes = new byte[] {
            13, 10,
            13, 10,
            13, 10,
            13, 10
        };

        screenColumns = 72;
    }

}
