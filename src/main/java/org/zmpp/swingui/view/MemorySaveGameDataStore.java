package org.zmpp.swingui.view;

import org.zmpp.iff.FormChunk;
import org.zmpp.iff.WritableFormChunk;
import org.zmpp.vm.SaveGameDataStore;

public class MemorySaveGameDataStore implements SaveGameDataStore {
    private WritableFormChunk savegame;

    public boolean saveFormChunk(WritableFormChunk formchunk) {
        this.savegame = formchunk;
        return true;
    }

    public FormChunk retrieveFormChunk() {
        return this.savegame;
    }
}
