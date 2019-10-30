package eu.sblendorio.bbs.tenants;

import eu.sblendorio.bbs.core.*;

public class EchoTestKey extends PetsciiThread {

    @Override
    public void doLoop() throws Exception {
        cls();
        newline();
        int key;
        do {
            print("Command> ");
            flush(); key = readKey();
            println("You wrote: "+key);
        } while (key == 46);
    }
}