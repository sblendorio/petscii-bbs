package org.zmpp.textbased.bbs;

import java.io.IOException;

import org.zmpp.encoding.ZsciiEncoding;
import org.zmpp.io.InputStream;
import org.zmpp.vm.Machine;

import eu.sblendorio.bbs.core.Keys;
import eu.sblendorio.bbs.core.PetsciiThread;

public class BBSInputStream implements InputStream {

    PetsciiThread petsciiThread;
    Machine machine;

    public BBSInputStream(Machine machine, PetsciiThread petsciiThread) {
        this.petsciiThread = petsciiThread;
        this.machine = machine;
    }

    @Override
    public void cancelInput() {
        throw new java.lang.UnsupportedOperationException("cancelInput not yet implemented");

    }

    @Override
    public short getZsciiChar(boolean flushBeforeGet) {
        short traslatedChar;
        try {
            int key  = this.petsciiThread.readKey();
            System.out.println("getZsciiChar: input char: "+key);
            switch (key){
                case Keys.RETURN: this.petsciiThread.readKey(); traslatedChar = ZsciiEncoding.NEWLINE; //skip the carriage return 
                case Keys.DEL : traslatedChar = ZsciiEncoding.DELETE;
                default : traslatedChar = machine.getGameData().getZsciiEncoding().getZsciiChar((char)key);
            }
            
        } catch (IOException e) {
            throw new java.lang.UnsupportedOperationException("unsupported character exception");
        }
        return traslatedChar;
       
    }

    @Override
    public void close() {
        throw new java.lang.UnsupportedOperationException("close not yet implemented");

    }

}