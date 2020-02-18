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
        short translatedChar;
        try {
            int key  = this.petsciiThread.readKey();
            switch (key){
                case Keys.RETURN:
                    this.petsciiThread.readKey(); // SBLEND FIX TODO ELIMINARE
                    translatedChar = ZsciiEncoding.NEWLINE;
                    break; //skip the carriage return
                case Keys.DEL : translatedChar = Keys.DEL;
                    break;
                default :
                    translatedChar = machine.getGameData().getZsciiEncoding().getZsciiChar((char)key);
                    if (Character.isLowerCase(translatedChar))
                        translatedChar = (short) Character.toUpperCase(translatedChar);
                    else if (Character.isUpperCase(translatedChar))
                        translatedChar = (short) Character.toLowerCase(translatedChar);
                    break;
            }
            
        } catch (IOException e) {
            throw new java.lang.UnsupportedOperationException("unsupported character exception");
        }
        return translatedChar;
       
    }

    @Override
    public void close() {
        throw new java.lang.UnsupportedOperationException("close not yet implemented");

    }

}