package eu.sblendorio.bbs.tenants.minitel;

import eu.sblendorio.bbs.tenants.ascii.MenuApple1;

import java.io.IOException;

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
        flush(); resetInput();
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
    public void displayMenu() throws Exception {
        write(0x1b, 0x3a, 0x6a, 0x43); // scroll off
        write(readBinaryFile("minitel/menu-retrocampus.vdt"));
        write(0x1b, 0x3a, 0x69, 0x43); // scroll on
        flush(); resetInput();
    }

    @Override
    public int readSingleKey() throws IOException {
        write(20); // Cursor off
        int ch = readKey();
        write(17); // Cursor on
        return ch;
    }

    @Override
    public void initBbs() throws Exception {
        Thread.sleep(2300);
        resetInput();
    }
}
