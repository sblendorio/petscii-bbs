package org.zmpp.textui.cli;

import java.io.Console;
import java.io.Reader;
import java.io.Writer;

import org.zmpp.iff.FormChunk;
import org.zmpp.iff.WritableFormChunk;
import org.zmpp.io.IOSystem;
import org.zmpp.io.InputStream;
import org.zmpp.vm.Instruction;
import org.zmpp.vm.Machine;
import org.zmpp.vm.SaveGameDataStore;
import org.zmpp.vm.ScreenModel;
import org.zmpp.textui.VirtualConsole;

/**
 * Implements a CLI based interface and the load/save logic based on local
 * filesystem
 */
public class CLIConsole implements VirtualConsole, SaveGameDataStore, IOSystem {

    boolean debugMode = false;
    Console console;
    
    Machine machine;
    CLIInputStream cliInputStream;

    CLIScreenModel screenModel ;

    public CLIConsole(final Machine machine, boolean debugMode) {
        this.machine = machine;
        this.console = System.console();
        this.debugMode = debugMode;
        cliInputStream  = new CLIInputStream(machine,console);
       
        if (machine.getGameData().getStoryFileHeader().getVersion() ==  6) {
            // TBD Unsupported?
            screenModel = new CLIScreenModel(console, machine);
          } else {
            screenModel = new CLIScreenModel(console, machine);
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

    /** Console */
    @Override
    public void reportInvalidStory() {
        System.err.println("Invalid story.");
        System.exit(0);
    }

    /** Console */
    @Override
    public void runTheGame() {
        screenModel.waitInitialized();  
        machine.start();
    
        while (machine.getCpu().isRunning()) {
        
        Instruction instr = machine.getCpu().nextStep();
        if (this.debugMode) {
            console.printf("%05x: %s", machine.getCpu().getProgramCounter(),
                            instr.toString());
        }
        instr.execute();
        }
    }

    public ScreenModel getScreenModel(){
        return screenModel;
    }

    public InputStream getInputStream(){
        return this.cliInputStream;
    }
}
