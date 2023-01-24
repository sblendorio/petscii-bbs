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

    public byte[] initializingBytes() {
        return new byte[] { 0x1B, 0x3A, 0x69, 0x43 };
    }

    @Override
    public String getCharset() {
        return "minitel";
    }

    protected void banner() {
        write(readBinaryFile("minitel/retrocampus-logo.vdt"));
    }

}
