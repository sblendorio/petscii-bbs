package eu.sblendorio.bbs.core;

import java.io.IOException;

public class CbmIOException extends IOException {
    final private String missingInput;

    public CbmIOException(String msg) {
        super(msg);
        this.missingInput = null;
    }

    public CbmIOException(String msg, String missingInput) {
        super(msg);
        this.missingInput = missingInput;
    }

    public String getMissingInput() {
        return missingInput;
    }
}
