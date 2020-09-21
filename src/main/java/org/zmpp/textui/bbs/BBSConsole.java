package org.zmpp.textui.bbs;

import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.io.Reader;

import org.zmpp.base.DefaultMemoryAccess;
import org.zmpp.iff.DefaultFormChunk;
import org.zmpp.iff.FormChunk;
import org.zmpp.iff.WritableFormChunk;
import org.zmpp.io.IOSystem;
import org.zmpp.textui.VirtualConsole;
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
            File saveFile;
            boolean sure = true;
            do {
                petsciiThread.print("Filename: ");
                petsciiThread.flush();
                petsciiThread.resetInput();
                String filename = petsciiThread.readLine();
                if (isBlank(filename)) {
                    petsciiThread.println("Aborted.");
                    return false;
                }
                saveFile = new File(currentdir + File.separator + filename.toLowerCase() + ".ziff");
                if (saveFile.exists()) {
                    petsciiThread.println("WARNING: File already exists.");
                    petsciiThread.print("Keep going with this? (Y/N) ");
                    petsciiThread.flush();
                    petsciiThread.resetInput();
                    String line = petsciiThread.readLine();
                    if (isBlank(line)) {
                        petsciiThread.println("Aborted.");
                        return false;
                    }
                    final String response = defaultString(line).trim().toLowerCase();
                    sure = response.equals("y") || response.equals("yes");
                }
            } while (!sure);
            raf = new RandomAccessFile(saveFile, "rw");
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
            if (isBlank(filename)) {
                petsciiThread.println("Aborted.");
                return null;
            }
            File loadFile = new File(currentdir + File.separator + filename.toLowerCase() + ".ziff");
            if (!loadFile.exists()) {
                petsciiThread.println("File not found. Aborted.");
                return null;
            }
            raf = new RandomAccessFile(loadFile, "r");
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
        petsciiThread.log("Transcript of game not yet implemented");
        return null;
    }

    /** IOSystem */
    @Override
    public Reader getInputStreamReader() {
        petsciiThread.log("Inputting commands from file not yet implemented");
        return null;
    }

    public ScreenModel getScreenModel(){
        return screenModel;
    }
}
