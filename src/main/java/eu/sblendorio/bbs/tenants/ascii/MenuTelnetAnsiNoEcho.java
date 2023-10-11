package eu.sblendorio.bbs.tenants.ascii;

public class MenuTelnetAnsiNoEcho extends MenuTelnetAnsi {

    public MenuTelnetAnsiNoEcho() {
        super();
        setLocalEcho(false);
    }
}
