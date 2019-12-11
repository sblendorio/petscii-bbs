package org.zmpp.textbased;

import org.zmpp.iff.FormChunk;
import org.zmpp.iff.WritableFormChunk;
import org.zmpp.vm.SaveGameDataStore;

public class ConsoleSaveGameDataStore implements SaveGameDataStore {
    @Override
    public boolean saveFormChunk(WritableFormChunk formchunk) {
        return false;
    }

    @Override
    public FormChunk retrieveFormChunk() {
        return null;
    }
}
