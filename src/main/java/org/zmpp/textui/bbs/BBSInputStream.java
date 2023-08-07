package org.zmpp.textui.bbs;

import eu.sblendorio.bbs.core.BbsThread;
import eu.sblendorio.bbs.core.GoBackException;
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
        short translatedChar; // CICCIO
        try {
            int key = this.bbsThread.readKey();
            if (bbsThread.isNewline(key)) {
                bbsThread.resetInput();
                translatedChar = ZsciiEncoding.NEWLINE;
                bbsThread.optionalCls();
            } else if (bbsThread.isBackspace(key)) {
                translatedChar = ZsciiEncoding.DELETE;
            } else {
                if (key < 32 || key > 128) {
                    translatedChar = -1;
                } else {
                    short ch = machine.getGameData().getZsciiEncoding().getZsciiChar((char) key);
                    translatedChar = (short) bbsThread.convertToAscii(ch);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return translatedChar;
    }

    @Override
    public void close() {
        throw new GoBackException("Exit from ZMPP game");
    }
}
