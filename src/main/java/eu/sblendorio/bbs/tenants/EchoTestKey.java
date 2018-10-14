package eu.sblendorio.bbs.tenants;

import eu.sblendorio.bbs.core.*;

public class EchoTestKey extends PetsciiThread {

    @Override
    public void doLoop() throws Exception {
        cls();
        newline();
        while (true) {
            print("Command> ");
            flush(); int key = readKey();
            println("You wrote: "+key);
            if (key == 46) break;
        }
    }
}