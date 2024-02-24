package org.zmpp.swingui.view;

import org.zmpp.base.DefaultMemory;
import org.zmpp.base.Memory;
import org.zmpp.iff.DefaultFormChunk;
import org.zmpp.iff.FormChunk;
import org.zmpp.iff.WritableFormChunk;
import org.zmpp.swingui.app.Main;
import org.zmpp.vm.SaveGameDataStore;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileSaveGameDataStore implements SaveGameDataStore {
    private final Component parent;

    public FileSaveGameDataStore(Component parent) {
        this.parent = parent;
    }

    public boolean saveFormChunk(WritableFormChunk formchunk) {
        File currentdir = new File(System.getProperty("user.dir"));
        JFileChooser fileChooser = new JFileChooser(currentdir);
        fileChooser.setDialogTitle(Main.getMessage("dialog.savegame.title"));

        if (fileChooser.showSaveDialog(this.parent) == 0) {
            File savefile = fileChooser.getSelectedFile();
            RandomAccessFile raf = null;
            try {
                raf = new RandomAccessFile(savefile, "rw");
                byte[] data = formchunk.getBytes();
                raf.write(data);
                return true;
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                if (raf != null) try {
                    raf.close();
                } catch (Exception ex) {
                }
            }
        }
        return false;
    }


    public FormChunk retrieveFormChunk() {
        File currentdir = new File(System.getProperty("user.dir"));
        JFileChooser fileChooser = new JFileChooser(currentdir);
        fileChooser.setDialogTitle(Main.getMessage("dialog.restoregame.title"));
        if (fileChooser.showOpenDialog(this.parent) == 0) {
            File savefile = fileChooser.getSelectedFile();
            RandomAccessFile raf = null;
            try {
                raf = new RandomAccessFile(savefile, "r");
                byte[] data = new byte[(int) raf.length()];
                raf.readFully(data);
                return new DefaultFormChunk(new DefaultMemory(data));
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                if (raf != null) try {
                    raf.close();
                } catch (Exception ex) {
                }
            }
        }
        return null;
    }
}


/* Location:              /Users/sblendorio/Downloads/zmpp-1.5-preview1-bin/zmpp-1.5-preview1.jar!/org/zmpp/swingui/view/FileSaveGameDataStore.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */