package eu.sblendorio.bbs.tenants.mixed;

import eu.sblendorio.bbs.core.AsciiThread;

public class DemoStart extends AsciiThread {
    @Override
    public void doLoop() throws Exception {
        int ch;
        do {
            newline();
            newline();
            newline();
            newline();
            println("Choose option:");
            println("--------------");
            println();
            println("1- Demo PETSCII");
            println("2- Demo ASCII");
            println(".- Exit");
            resetInput();
            ch = readKey();
            if (ch == '1') {
                launch(new DemoPetscii());
            } else if (ch == '2') {
                launch(new DemoAscii());
            }
        } while (ch != '.');
    }
}
