package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.PetsciiThread;
import org.apache.commons.lang3.StringUtils;

public class EchoQuoteModeTest extends PetsciiThread {

    public EchoQuoteModeTest() {
        this.keepAliveInterval = 1000;
        this.keepAlive = true;
        this.keepAliveChar = 'x';
    }

    @Override
    public void doLoop() throws Exception {
        cls();
        newline();
        int key;
        do {
            flush(); key = readKey();
            String s = StringUtils.repeat((char) key, 1);
            print(s);
            System.out.print(quoteMode() ? '1' : '0');
        } while (key != 46);
    }
}
