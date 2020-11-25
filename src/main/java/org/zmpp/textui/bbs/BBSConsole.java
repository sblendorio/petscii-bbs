package org.zmpp.textui.bbs;

import eu.sblendorio.bbs.core.BbsThread;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Writer;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isBlank;
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

public class BBSConsole implements VirtualConsole, SaveGameDataStore,  IOSystem {

    BbsThread bbsThread;
    Machine machine;
    boolean debugMode = false;
    ScreenModel screenModel;

    public BBSConsole(Machine machine, BbsThread bbsThread, boolean debugMode) {
        this.bbsThread = bbsThread;
        this.machine = machine;
        this.debugMode = debugMode;
        screenModel = new BBSScreenModel(bbsThread,machine);
    }

    @Override
    public void reportInvalidStory() {
        this.bbsThread.println("Invalid story.");

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
            this.bbsThread.print(message);
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
            bbsThread.newline();
            File saveFile;
            boolean sure = true;
            do {
                bbsThread.print("Filename: ");
                bbsThread.flush();
                bbsThread.resetInput();
                String filename = bbsThread.readLine();
                if (isBlank(filename)) {
                    bbsThread.println("Aborted.");
                    return false;
                }
                saveFile = new File(currentdir + File.separator + filename.toLowerCase() + ".ziff");
                if (saveFile.exists()) {
                    bbsThread.println("WARNING: File already exists.");
                    bbsThread.print("Keep going with this? (Y/N) ");
                    bbsThread.flush();
                    bbsThread.resetInput();
                    String line = bbsThread.readLine();
                    if (isBlank(line)) {
                        bbsThread.println("Aborted.");
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
            bbsThread.newline();
            bbsThread.print("Filename: ");
            bbsThread.flush();
            bbsThread.resetInput();
            String filename = bbsThread.readLine();
            if (isBlank(filename)) {
                bbsThread.println("Aborted.");
                return null;
            }
            File loadFile = new File(currentdir + File.separator + filename.toLowerCase() + ".ziff");
            if (!loadFile.exists()) {
                bbsThread.println("File not found. Aborted.");
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
        bbsThread.log("Transcript of game not yet implemented");
        return null;
    }

    /** IOSystem */
    @Override
    public Reader getInputStreamReader() {
        bbsThread.log("Inputting commands from file not yet implemented");
        return null;
    }

    public ScreenModel getScreenModel(){
        return screenModel;
    }
}
