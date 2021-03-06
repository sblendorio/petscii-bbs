package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.PetsciiThread;

public class WelcomeBBS extends PetsciiThread {

    public WelcomeBBS() {
        // No operation
    }

    @Override
    public void doLoop() throws Exception {

        // clear screen
        cls();

        println("This is your brand-new BBS");
        println();
        print("Enter your name: ");

        // flush output
        flush();

        // clear input buffer
        resetInput();

        String name = readLine();
        println();
        println("Welcome, " + name + "!");
        println("Press a key to exit");
        flush();
        readKey();
    }
}
