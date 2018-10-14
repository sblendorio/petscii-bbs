package eu.sblendorio.bbs.tenants;

import eu.sblendorio.bbs.core.*;

import static org.apache.commons.lang3.StringUtils.lowerCase;

public class EchoTestString extends PetsciiThread {

    @Override
    public void doLoop() throws Exception {
        cls();
        newline();
        while (true) {
            print("Command> ");
            flush(); String inputRaw = readLine();
            String input = lowerCase(inputRaw);
            println("You wrote: "+input+ " (len="+input.length()+")");
        }
    }
}