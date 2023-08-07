package eu.sblendorio.bbs.tenants.prestel;

import eu.sblendorio.bbs.core.PrestelThread;

import static eu.sblendorio.bbs.core.PrestelControls.*;

public class TestPrestel extends PrestelThread {
    @Override
    public void doLoop() throws Exception {
        for (int i=0; i<255; i++) {
            cls();
            gotoXY(0, i);
            print("12345678901234567890");
            gotoXY(3, i);
            log("i=" + i);
            write(0x1b, 0x58);
            resetInput();
            readKey();
        }
    }
}
// 27,88
// 27,93
