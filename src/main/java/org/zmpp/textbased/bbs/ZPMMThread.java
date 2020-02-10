package org.zmpp.textbased.bbs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.zmpp.textbased.VirtualConsole;

import eu.sblendorio.bbs.core.PetsciiThread;

public class ZPMMThread extends PetsciiThread {

    public  byte[] readBinaryFile(String filename) throws IOException {
        try (InputStream is = ZPMMThread.class.getClassLoader().getResourceAsStream(filename);
             ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[2048];
            for (int len = is.read(buffer); len != -1; len = is.read(buffer)) os.write(buffer, 0, len);
            return os.toByteArray();
        }
    }

    @Override
    public void doLoop() throws Exception {
        final byte[] story = readBinaryFile("zmpp/minizork.z3");
        BBSMachineFactory factory;
        factory = new BBSMachineFactory(story,this);
        factory.buildMachine();
        VirtualConsole console = factory.getUI();
        console.runTheGame();
    }


}