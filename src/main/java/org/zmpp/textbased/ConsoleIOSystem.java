package org.zmpp.textbased;

import org.zmpp.io.IOSystem;

import java.io.*;

public class ConsoleIOSystem implements IOSystem {

    Writer writer = new PrintWriter(System.out, true);
    Reader reader = new InputStreamReader(System.in);

    @Override
    public Writer getTranscriptWriter() {
        return writer;
    }

    @Override
    public Reader getInputStreamReader() {
        return reader;
    }
}
