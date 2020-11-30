package eu.sblendorio.bbs.core;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import static java.lang.System.arraycopy;
import static java.lang.System.setOut;
import java.net.Socket;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.substring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* This class is a modified version of original BufferedReader from original java IO library */
public abstract class BbsInputOutput extends Reader {

    public static class QuotedPrintStream extends PrintStream {
        private boolean isQuoteMode = false;

        public QuotedPrintStream(OutputStream out, boolean autoFlush, String encoding)
            throws UnsupportedEncodingException {
            super(out, autoFlush, encoding);
        }

        public boolean quoteMode() {
            return isQuoteMode;
        }

        public void setQuoteMode(boolean q) {
            this.isQuoteMode = q;
        }

        @Override
        public void write(int b) {
            if (b == 34)
                isQuoteMode = !isQuoteMode;
            else if (b == 13 || b == 141)
                isQuoteMode = false;
            super.write(b);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(BbsInputOutput.class);
    private String readBuffer = EMPTY;

    protected Reader in;
    protected QuotedPrintStream out;
    protected boolean localEcho = true;

    protected static int defaultCharBufferSize = 8192;

    protected BbsInputOutput(Reader in, int sz) {
        super(in);
        if (sz <= 0)
            throw new IllegalArgumentException("Buffer size <= 0");
        this.in = in;
    }

    public BbsInputOutput(Socket socket) throws IOException {
        this(new InputStreamReader(socket.getInputStream(), ISO_8859_1), defaultCharBufferSize);
        this.out = new QuotedPrintStream(socket.getOutputStream(), true, ISO_8859_1.name());
    }

    public PrintStream out() {
        return out;
    }

    public boolean quoteMode() {
        return false;
    }

    public void setQuoteMode(boolean q) {
        out.setQuoteMode(q);
    }

    public int readKey() throws IOException {
        final int result = in.read();
        if (result == -1) throw new BbsIOException("BbsIOException::readKey()");
        return result;
    }

    public String readLine(int maxLength, boolean mask) throws IOException {
        int ch;
        readBuffer = EMPTY;
        do {
            ch = readKey();
            if (isBackspace(ch)) {
                if (readBuffer.length() > 0) {
                    if (localEcho) {
                        write(backspace());
                        flush();
                    }
                    readBuffer = readBuffer.substring(0, readBuffer.length()-1);
                }
            } else if (ch == 34 && (maxLength == 0 || readBuffer.length() < maxLength)) {
                if (localEcho) {
                    if (mask) write('*');
                    else writeDoublequotes();
                    flush();
                }
                readBuffer += "\"";
            } else if (isPrintableChar(ch) && (maxLength == 0 || readBuffer.length() < maxLength)) {
                if (localEcho) {
                    write(mask ? '*' : ch);
                    flush();
                }
                readBuffer += (char) convertToAscii(ch);
            }
        } while (!isNewline(ch));
        newline();
        final String result = readBuffer;
        readBuffer = EMPTY;
        return result;
    }

    public String readLineBuffer() {
        return readBuffer;
    }

    public String readPassword() throws IOException {
        return readLine(0, true);
    }

    public String readLine(int maxLength) throws IOException {
        return readLine(maxLength, false);
    }

    public String readLine() throws IOException {
        return readLine(0, false);
    }

    @Override
    public boolean ready() throws IOException {
        return in.ready();
    }

    public void close() throws IOException {
        synchronized (lock) {
            if (out != null) {
                try {
                    out.close();
                } finally {
                    out = null;
                }
            }
            if (in != null) {
                try {
                    in.close();
                } finally {
                    in = null;
                }
            }
        }
    }

    public void resetInput() throws IOException {
        final int THRESHOLD = 192;
        byte[] buffer = new byte[THRESHOLD];

        int count = 0;
        while (in.ready() && count < THRESHOLD) {
            int key = in.read();
            buffer[count++] = (byte) key;
            if (key == -1) throw new BbsIOException("BbsIOException::resetInput()");
        }
        if (count == 0) return;

        byte[] excludedInput = new byte[count];
        arraycopy(buffer, 0, excludedInput, 0, count);
        final String missingInput = new String(excludedInput, ISO_8859_1);
        logger.info("Flushing input buffer: '{}', len = {}",
            substring(missingInput
                .replaceAll("\r+", "\\\\r")
                .replaceAll("\n+", "\\\\n")
                .replaceAll("\\p{C}", "?"), 0, 120) +
                (missingInput.length() > 120 ? "..." : EMPTY),
            missingInput.length());

        if (missingInput.matches("(?is)^(G?ET|P?OST|H?EAD|P?UT|D?ELETE|C?ONNECT|O?PTIONS) [^\n]+ HTTP.*")) {
            // out.write(10);
            out.flush();
            out.close();
            this.close();
            throw new BbsIOException("HTTP Connection detected, closing socket.");
        } else if (missingInput.matches("(?is)^.*Cookie: mstshash=[a-z].*")) {
            out.flush();
            out.close();
            this.close();
            throw new BbsIOException("MICROSOFT REMOTE DESKTOP Connection detected, closing socket");
        } else if (missingInput.matches("(?is)^.*P?uTTYPuTTYPuTTY.*")) {
            out.flush();
            out.close();
            this.close();
            throw new BbsIOException("Weird PuTTY Connection detected, closing socket");
        } else if (
            missingInput.matches("(?is)^SSH-[0-9\\.]+-.*") ||
                missingInput.matches("(?is)^.*Handshake failed.*") ||
                missingInput.matches("(?is)^.*x?term-[0-9]+color.*")
        ) {
            out.flush();
            out.close();
            this.close();
            throw new BbsIOException("SECURE SHELL (ssh) Connection detected, closing socket");
        } else if (missingInput.matches("(?is)^R?FB [0-9][0-9][0-9]\\.[0-9][0-9][0-9]\n.*")) {
            out.flush();
            out.close();
            this.close();
            throw new BbsIOException("VNC Connection detected, closing socket");
        } else if (count >= THRESHOLD) {
            out.flush();
            out.close();
            this.close();
            throw new BbsIOException("SEVERE. BbsIOException::resetInput, potential DoS detected.");
        }
    }

    public Integer keyPressed() throws IOException {
        boolean pressed = false;
        Integer key = null;
        while (in.ready()) {
            pressed = true;
            key = readKey();
        }
        return pressed ? key : null;
    }

    public void write(byte[] buf, int off, int len) { out.write(buf, off, len); }
    public void write(byte[] b) {
        try {
            out.write(b);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    public void write(int b) { out.write(b); }
    public void write(int... b) { for (int c: b) out.write(c); }
    public void flush() { out.flush(); }

    public void printlnRaw(String s) { out.print(s); newline(); }
    public void printRaw(String s) { out.print(s); }
    public void println(String msg) { print(msg); newline(); }

    public void print(String msg) {
        if (msg == null) return;
        for (char c: msg.toCharArray())
            out.write(c);
    }

    public void writeRawFile(String filename) throws IOException {
        write(readBinaryFile(filename));
        flush();
    }

    public byte[] readBinaryFile(String filename) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
             ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[2048];
            for (int len = is.read(buffer); len != -1; len = is.read(buffer)) os.write(buffer, 0, len);
            return os.toByteArray();
        }
    }

    static List<String> readTextFile(String filename) throws IOException {
        try (InputStream is = BbsInputOutput.class.getClassLoader().getResourceAsStream(filename)) {
            return readFromInputStream(is);
        }
    }

    protected static List<String> readFromInputStream(InputStream is) throws IOException {
        if (is == null) {
            return Collections.emptyList();
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            return br.lines().collect(Collectors.toList());
        }
    }

    public void newline() {
        write(newlineBytes());
    }
    public String newlineString() {
        return new String(newlineBytes(), ISO_8859_1);
    };
    public abstract byte[] newlineBytes();
    public abstract void cls();
    public abstract int backspace();
    public abstract boolean isNewline(int ch);
    public abstract boolean isBackspace(int ch);
    public abstract void writeDoublequotes();
    public abstract int convertToAscii(int ch);

    public boolean isPrintableChar(int c) {
        return c >= 32;
    }

    public boolean isPrintableChar(char c) {
        return isPrintableChar((int) c);
    }

    public String filterPrintable(String s) {
        StringBuilder result = new StringBuilder();
        for (char c: defaultString(s).toCharArray())
            if (isPrintableChar(c)) result.append(c);
        return result.toString();
    }

    public String filterPrintableWithNewline(String s) {
        StringBuilder result = new StringBuilder();
        for (char c: defaultString(s).toCharArray())
            if (isPrintableChar(c) || c == '\n' || c == '\r') result.append(c);
        return result.toString();
    }

    @Override
    public int read(char[] chars, int i, int i1) throws IOException {
        return in.read(chars, i, i1);
    }

    public void setLocalEcho(boolean value) { this.localEcho = value; }
    public boolean getLocalEcho() { return localEcho; }

    public abstract int getScreenColumns();

    public abstract int getScreenRows();
}