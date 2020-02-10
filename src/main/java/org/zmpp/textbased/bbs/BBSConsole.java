package org.zmpp.textbased.bbs;

import java.io.Writer;
import java.io.Reader;

import org.zmpp.iff.FormChunk;
import org.zmpp.iff.WritableFormChunk;
import org.zmpp.io.IOSystem;
import org.zmpp.textbased.VirtualConsole;
import org.zmpp.vm.Instruction;
import org.zmpp.vm.Machine;
import org.zmpp.vm.SaveGameDataStore;
import org.zmpp.vm.ScreenModel;

import eu.sblendorio.bbs.core.PetsciiThread;

public class BBSConsole implements VirtualConsole, SaveGameDataStore,  IOSystem {

    PetsciiThread petsciiThread;
    Machine machine;
    boolean debugMode = false;
    ScreenModel screenModel;

    public BBSConsole(Machine machine, PetsciiThread petsciiThread, boolean debugMode) {
        this.petsciiThread = petsciiThread;
        this.machine = machine;
        this.debugMode = debugMode;
        screenModel = new BBSScreenModel(petsciiThread,machine);
    }

    @Override
    public void reportInvalidStory() {
        this.petsciiThread.println("Invalid story.");

    }

    @Override
    public void runTheGame() {
        screenModel.waitInitialized();  
        machine.start();
    
        while (machine.getCpu().isRunning()) {
        
        Instruction instr = machine.getCpu().nextStep();
        if (this.debugMode) {
            String message = String.format("%05x: %s", machine.getCpu().getProgramCounter(),
            instr.toString());
            this.petsciiThread.print(message);
        }
        instr.execute();
        }

    }

    /** SaveGameDataStore */
    @Override
    public boolean saveFormChunk(final WritableFormChunk formchunk) {
        throw new java.lang.UnsupportedOperationException("Save game not yet implemented");
    }

    /** SaveGameDataStore */
    @Override
    public FormChunk retrieveFormChunk() {
        throw new java.lang.UnsupportedOperationException("Load game not yet implemented");
    }

    /** IOSystem */
    @Override
    public Writer getTranscriptWriter() {
        throw new java.lang.UnsupportedOperationException("Transcript of game not yet implemented");
    }

    /** IOSystem */
    @Override
    public Reader getInputStreamReader() {
        throw new java.lang.UnsupportedOperationException("Inputting commands from file not yet implemented");
    }

    public ScreenModel getScreenModel(){
        return screenModel;
    }


}