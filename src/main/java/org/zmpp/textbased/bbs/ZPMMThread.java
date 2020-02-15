package org.zmpp.textbased.bbs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.zmpp.textbased.VirtualConsole;

import eu.sblendorio.bbs.core.PetsciiThread;

public class ZPMMThread extends PetsciiThread {

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