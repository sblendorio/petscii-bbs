package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.PetsciiThread;

public class EchoTestKey extends PetsciiThread {

    public EchoTestKey() {
    }

    @Override
    public void doLoop() throws Exception {
        cls();
        newline();
        int key;
        do {
            print("Command> ");
            flush(); key = readKey();
            println("You wrote: "+key);
        } while (key != 46);
    }
}
