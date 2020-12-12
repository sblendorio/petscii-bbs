package eu.sblendorio.bbs.tenants.mixed;

import eu.sblendorio.bbs.core.AsciiThread;

public class DemoAscii extends AsciiThread {

    public DemoAscii() {
        super();
    }

    @Override
    public void doLoop() throws Exception {
        newline();
        newline();
        println("Esempio di ASCII Thread");
        newline();
        newline();
    }
}
