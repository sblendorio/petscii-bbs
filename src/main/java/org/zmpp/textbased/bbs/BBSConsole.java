package org.zmpp.textbased.bbs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.io.Reader;

import org.zmpp.base.DefaultMemoryAccess;
import org.zmpp.iff.DefaultFormChunk;
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
        RandomAccessFile raf = null;
        String currentdir = new File(System.getProperty("user.dir")).getAbsolutePath();
        try {
            petsciiThread.newline();
            petsciiThread.print("Filename: ");
            petsciiThread.flush();
            petsciiThread.resetInput();
            String filename = petsciiThread.readLine();
            File savefile = new File(currentdir + File.separator + filename);
            raf = new RandomAccessFile(savefile, "rw");
            byte[] data = formchunk.getBytes();
            raf.write(data);
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (raf != null) try { raf.close(); } catch (Exception ex) { }
        }

        return false;
    }

    /** SaveGameDataStore */
    @Override
    public FormChunk retrieveFormChunk() {
        RandomAccessFile raf = null;
        String currentdir = new File(System.getProperty("user.dir")).getAbsolutePath();
        try {
            petsciiThread.newline();
            petsciiThread.print("Filename: ");
            petsciiThread.flush();
            petsciiThread.resetInput();
            String filename = petsciiThread.readLine();
            File savefile = new File(currentdir + File.separator + filename);
            raf = new RandomAccessFile(savefile, "r");
            byte[] data = new byte[(int) raf.length()];
            raf.readFully(data);
            return new DefaultFormChunk(new DefaultMemoryAccess(data));
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (raf != null) try { raf.close(); } catch (Exception ex) { }
        }

        return null;
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