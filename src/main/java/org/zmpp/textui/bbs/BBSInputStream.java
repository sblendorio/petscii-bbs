package org.zmpp.textui.bbs;

import java.io.IOException;

import org.zmpp.encoding.ZsciiEncoding;
import org.zmpp.io.InputStream;
import org.zmpp.vm.Machine;

import eu.sblendorio.bbs.core.PetsciiKeys;
import eu.sblendorio.bbs.core.bbstype.PetsciiThread;

public class BBSInputStream implements InputStream {

    PetsciiThread petsciiThread;
    Machine machine;

    public BBSInputStream(Machine machine, PetsciiThread petsciiThread) {
        this.petsciiThread = petsciiThread;
        this.machine = machine;
    }

    @Override
    public void cancelInput() {
        petsciiThread.log("cancelInput not yet implemented");
    }

    @Override
    public short getZsciiChar(boolean flushBeforeGet) {
        short translatedChar;
        try {
            int key  = this.petsciiThread.readKey();
            if (key >= 193 && key <= 218) key -= 96;
            switch (key){
                case PetsciiKeys.RETURN:
                    translatedChar = ZsciiEncoding.NEWLINE;
                    break; //skip the carriage return
                case PetsciiKeys.DEL : translatedChar = PetsciiKeys.DEL;
                    break;
                default :
                    if (key < 32 || key > 128) {
                        translatedChar = -1;
                    } else {
                        translatedChar = machine.getGameData().getZsciiEncoding().getZsciiChar((char) key);
                        if (Character.isLowerCase(translatedChar))
                            translatedChar = (short) Character.toUpperCase(translatedChar);
                        else if (Character.isUpperCase(translatedChar))
                            translatedChar = (short) Character.toLowerCase(translatedChar);
                    }
                    break;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return translatedChar;
    }

    @Override
    public void close() {
        throw new RuntimeException("Exit from ZMPP game");
    }
}
