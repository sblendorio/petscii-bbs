package eu.sblendorio.bbs.tenants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.zmpp.textbased.ConsoleMachineFactory;
import org.zmpp.textbased.ConsoleMain;
import org.zmpp.textbased.VirtualConsole;
import org.zmpp.textbased.bbs.BBSMachineFactory;

import eu.sblendorio.bbs.core.PetsciiThread;

public class Zork extends PetsciiThread {

    @Override
    public void doLoop() throws Exception {
        final byte[] story = readBinaryFile("zmpp/minizork.z3");
        runStoryFile(story);

    }

    public  void runStoryFile(byte[] story) throws Exception {
        BBSMachineFactory factory;
        factory = new BBSMachineFactory(story, this);
        factory.buildMachine();
        VirtualConsole console = factory.getUI();
        console.runTheGame();
    }
}
