package eu.sblendorio.bbs.core;

public class GoBackException extends RuntimeException {
    public GoBackException() {
        super();
    }

    public GoBackException(String msg) {
        super(msg);
    }
}
