package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import static org.apache.commons.lang3.StringUtils.lowerCase;

public class AsciiTestString extends AsciiThread {

    public AsciiTestString() {
        super();
    }

    @Override
    public void doLoop() throws Exception {
        newline();
        newline();
        String input;
        do {
            print("Command> ");
            flush(); String inputRaw = readLine();
            input = lowerCase(inputRaw);
            println("You wrote: "+input+ " (len="+input.length()+")");
        } while (!"end".equalsIgnoreCase(input));
    }
}
