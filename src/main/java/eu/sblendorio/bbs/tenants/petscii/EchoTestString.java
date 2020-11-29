package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.PetsciiThread;
import static org.apache.commons.lang3.StringUtils.lowerCase;

public class EchoTestString extends PetsciiThread {

    @Override
    public void doLoop() throws Exception {
        cls();
        newline();
        String input;
        do {
            print("Command> ");
            flush(); String inputRaw = readLine();
            input = lowerCase(inputRaw);
            println("You wrote: "+input+ " (len="+input.length()+")");
        } while ("end".equalsIgnoreCase(input));
    }
}
