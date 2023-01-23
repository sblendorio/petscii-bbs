package eu.sblendorio.bbs.tenants.minitel;

import eu.sblendorio.bbs.tenants.ascii.MenuApple1;

public class MenuMinitelWithEcho extends MenuApple1 {

    public MenuMinitelWithEcho() {
        super();
        setLocalEcho(true);
        clsBytes = new byte[] {
                12
        };
    }

    @Override
    public String getCharset() {
        return "minitel";
    }

    protected void banner() {
        write(readBinaryFile("minitel/retrocampus-logo.vdt"));
    }

}
