package eu.sblendorio.bbs.tenants.ascii;

public class MenuTelnetPureAsciiNoEcho extends MenuTelnetPureAscii {

    public MenuTelnetPureAsciiNoEcho() {
        super();
        setLocalEcho(false);
    }
}
