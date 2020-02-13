package org.zmpp.textbased.cli;

import java.io.Console;
import java.io.IOException;

import org.zmpp.io.InputStream;
import org.zmpp.vm.Machine;

public class CLIInputStream implements InputStream {

    Console console;
    Machine machine;

    public CLIInputStream(Machine machine, Console console) {
        this.console = console;
        this.machine = machine;
    }

    @Override
    public void cancelInput() {
        throw new java.lang.UnsupportedOperationException("cancelInput not yet implemented");

    }

    @Override
    public short getZsciiChar(boolean flushBeforeGet) {
        char c;
        try {
            c = (char) this.console.reader().read();
        } catch (IOException e) {
            throw new java.lang.UnsupportedOperationException("unsupported character exception");
        }
        return machine.getGameData().getZsciiEncoding().getZsciiChar(c);
       
    }

    @Override
    public void close() {
        throw new java.lang.UnsupportedOperationException("close not yet implemented");

    }

}