package eu.sblendorio.bbs.tenants.videotex;

import eu.sblendorio.bbs.tenants.ascii.MenuApple1;

public class MenuVideotexWithEcho  extends MenuApple1 {

    public MenuVideotexWithEcho() {
        super();
        setLocalEcho(true);
        clsBytes = new byte[] {
                12
        };
    }

    @Override
    public String getCharset() {
        return "videotex";
    }

    protected void banner() {
        write(readBinaryFile("videotex/retrocampus-logo.vdt"));
    }

}
