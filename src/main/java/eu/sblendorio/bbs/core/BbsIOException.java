package eu.sblendorio.bbs.core;

import java.io.IOException;

public class BbsIOException extends IOException {
    private final String missingInput;

    public BbsIOException() {
        this(null, null);
    }

    public BbsIOException(String msg) {
        this(msg, null);
    }

    public BbsIOException(String msg, String missingInput) {
        super(msg);
        this.missingInput = missingInput;
    }

    public String getMissingInput() {
        return missingInput;
    }
}
