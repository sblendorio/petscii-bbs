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
        return new byte[] { 0x1B, 0x3A, 0x69, 0x43, 0x11 };
    }

    @Override
    public void logo() throws Exception {
        write(0x14); // Cursor off
        write(readBinaryFile("minitel/intro-retrocampus.vdt"));
        flush();
        keyPressed(12_000);
        write(0x11); // Cursor on
    }


    @Override
    public String getCharset() {
        return "minitel";
    }

    protected void banner() {
        write(readBinaryFile("minitel/retrocampus-logo.vdt"));
    }

    @Override
    public void initBbs() throws Exception {
        Thread.sleep(2300);
        resetInput();
    }
}
