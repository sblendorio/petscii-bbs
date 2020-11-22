package eu.sblendorio.bbs.core;

import java.io.IOException;

public class BbsIOException extends IOException {
    private final String missingInput;

    public BbsIOException(String msg) {
        super(msg);
        this.missingInput = null;
    }

    public BbsIOException(String msg, String missingInput) {
        super(msg);
        this.missingInput = missingInput;
    }

    public String getMissingInput() {
        return missingInput;
    }
}
