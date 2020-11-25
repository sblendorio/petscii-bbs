package org.zmpp.textui.bbs;

import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.PetsciiKeys;
import java.io.IOException;
import org.zmpp.encoding.ZsciiEncoding;
import org.zmpp.io.InputStream;
import org.zmpp.vm.Machine;

public class BBSInputStream implements InputStream {

    BbsThread bbsThread;
    Machine machine;

    public BBSInputStream(Machine machine, BbsThread bbsThread) {
        this.bbsThread = bbsThread;
        this.machine = machine;
    }

    @Override
    public void cancelInput() {
        bbsThread.log("cancelInput not yet implemented");
    }

    @Override
    public short getZsciiChar(boolean flushBeforeGet) {
        short translatedChar;
        try {
            int key  = this.bbsThread.readKey();
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
                        translatedChar = (short) bbsThread.convertToAscii(translatedChar);
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
