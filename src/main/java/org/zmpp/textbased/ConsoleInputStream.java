package org.zmpp.textbased;

import org.zmpp.io.InputStream;

public class ConsoleInputStream implements InputStream {
    @Override
    public void cancelInput() {

    }

    @Override
    public short getZsciiChar(boolean flushBeforeGet) {
        return 0;
    }

    @Override
    public void close() {

    }
}
