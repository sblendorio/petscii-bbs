package eu.sblendorio.bbs.tenants.prestel;

import eu.sblendorio.bbs.core.PrestelControls;
import eu.sblendorio.bbs.core.PrestelThread;

public class TestPrestelInput extends PrestelThread {
    @Override
    public void doLoop() throws Exception {
        String line;
        write(PrestelControls.CURSOR_ON);
        do {
            line = readLine();
        } while (!".".equals(line));
    }
}
// 27,88
// 27,93
