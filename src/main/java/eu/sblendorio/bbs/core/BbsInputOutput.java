package eu.sblendorio.bbs.core;

import static eu.sblendorio.bbs.core.Utils.isControlChar;
import static eu.sblendorio.bbs.core.Utils.isPrintableChar;
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
import java.net.Socket;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import static org.apache.commons.lang3.StringUtils.EMPTY;
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

    protected Reader in;
    protected QuotedPrintStream out;

    protected char[] cb;
    protected int nChars;
    protected int nextChar;

    protected static final int INVALIDATED = -2;
    protected static final int UNMARKED = -1;
    protected int markedChar = UNMARKED;
    protected int readAheadLimit = 0; /* Valid only when markedChar > 0 */

    /** If the next character is a line feed, skip it */
    protected boolean skipLF = false;

    /** The skipLF flag when the mark was set */
    protected boolean markedSkipLF = false;

    protected static int defaultCharBufferSize = 8192;
    protected static int defaultExpectedLineLength = 80;

    protected BbsInputOutput(Reader in, int sz) {
        super(in);
        if (sz <= 0)
            throw new IllegalArgumentException("Buffer size <= 0");
        this.in = in;
        cb = new char[sz];
        nextChar = nChars = 0;
    }

    public BbsInputOutput(Socket socket) throws IOException {
        this(new InputStreamReader(socket.getInputStream(), ISO_8859_1), defaultCharBufferSize);
        this.out = new QuotedPrintStream(socket.getOutputStream(), true, ISO_8859_1.name());
    }

    public PrintStream out() {return out;}

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

    /** Checks to make sure that the stream has not been closed */
    protected void ensureOpen() throws IOException {
        if (in == null)
            throw new IOException("Stream closed");
    }

    /**
     * Fills the input buffer, taking the mark into account if it is valid.
     */
    protected void fill() throws IOException {
        int dst;
        if (markedChar <= UNMARKED) {
            /* No mark */
            dst = 0;
        } else {
            /* Marked */
            int delta = nextChar - markedChar;
            if (delta >= readAheadLimit) {
                /* Gone past read-ahead limit: Invalidate mark */
                markedChar = INVALIDATED;
                readAheadLimit = 0;
                dst = 0;
            } else {
                if (readAheadLimit <= cb.length) {
                    /* Shuffle in the current buffer */
                    arraycopy(cb, markedChar, cb, 0, delta);
                    markedChar = 0;
                    dst = delta;
                } else {
                    /* Reallocate buffer to accommodate read-ahead limit */
                    char[] ncb = new char[readAheadLimit];
                    arraycopy(cb, markedChar, ncb, 0, delta);
                    cb = ncb;
                    markedChar = 0;
                    dst = delta;
                }
                nextChar = nChars = delta;
            }
        }

        int n;
        do {
            n = in.read(cb, dst, cb.length - dst);
        } while (n == 0);
        if (n > 0) {
            nChars = dst + n;
            nextChar = dst;
        }
    }

    /**
     * Reads a single character.
     *
     * @return The character read, as an integer in the range
     *         0 to 65535 (<tt>0x00-0xffff</tt>), or -1 if the
     *         end of the stream has been reached
     * @exception  IOException  If an I/O error occurs
     */
    @Override
    public int read() throws IOException {
        synchronized (lock) {
            ensureOpen();
            for (;;) {
                if (nextChar >= nChars) {
                    fill();
                    if (nextChar >= nChars)
                        return -1;
                }
                if (skipLF) {
                    skipLF = false;
                    if (cb[nextChar] == '\n') {
                        nextChar++;
                        continue;
                    }
                }
                char ch = cb[nextChar++];
                return ch;
            }
        }
    }

    /**
     * Reads characters into a portion of an array, reading from the underlying
     * stream if necessary.
     */
    protected int read1(char[] cbuf, int off, int len) throws IOException {
        if (nextChar >= nChars) {
            /* If the requested length is at least as large as the buffer, and
               if there is no mark/reset activity, and if line feeds are not
               being skipped, do not bother to copy the characters into the
               local buffer.  In this way buffered streams will cascade
               harmlessly. */
            if (len >= cb.length && markedChar <= UNMARKED && !skipLF) {
                return in.read(cbuf, off, len);
            }
            fill();
        }
        if (nextChar >= nChars) return -1;
        if (skipLF) {
            skipLF = false;
            if (cb[nextChar] == '\n') {
                nextChar++;
                if (nextChar >= nChars)
                    fill();
                if (nextChar >= nChars)
                    return -1;
            }
        }
        int n = Math.min(len, nChars - nextChar);
        arraycopy(cb, nextChar, cbuf, off, n);
        nextChar += n;
        return n;
    }

    public int read(char[] cbuf, int off, int len) throws IOException {
        synchronized (lock) {
            ensureOpen();
            if ((off < 0) || (off > cbuf.length) || (len < 0) ||
                ((off + len) > cbuf.length) || ((off + len) < 0)) {
                throw new IndexOutOfBoundsException();
            } else if (len == 0) {
                return 0;
            }

            int n = read1(cbuf, off, len);
            if (n <= 0) return n;
            while ((n < len) && in.ready()) {
                int n1 = read1(cbuf, off + n, len - n);
                if (n1 <= 0) break;
                n += n1;
            }
            return n;
        }
    }

    public String readLine(boolean ignoreLF) throws IOException {
        return readLine(ignoreLF, 0);
    }

    public String readLine(boolean ignoreLF, int maxLength) throws IOException {
        return readLine(ignoreLF, maxLength, false);
    }

    // CICCIO
    public String readLine(boolean ignoreLF, int maxLength, boolean mask) throws IOException {
        StringBuffer s = null;
        int startChar;
        synchronized (lock) {
            int size = 0;
            ensureOpen();
            boolean omitLF = ignoreLF || skipLF;

            for (;;) {
                if (nextChar >= nChars)
                    fill();
                if (nextChar >= nChars) { /* EOF */
                    if (s != null && s.length() > 0) {
                        final String missingInput = decode(s.toString())
                            .replaceAll("\r+", "\\\\r")
                            .replaceAll("\n+", "\\\\n")
                            .replaceAll("\\p{C}", "?");
                        throw new BbsIOException("BbsIOException::readLine(), missingInput='"+missingInput+"'", missingInput);
                    } else {
                        throw new BbsIOException("BbsIOException::readLine()");
                    }
                }
                boolean eol = false;
                char c = 0;
                int i;

                if (nChars>0 && isControlChar(cb[0])) {
                    nChars = 0;
                    nextChar = 0;
                    continue;
                }

                if (((int)cb[nextChar]) == 148) cb[nextChar]='\024';
                if (
                    (((int)cb[nextChar]) == 20 && size == 0) ||
                        (maxLength > 0 && size >= maxLength && ((int)cb[nextChar]) != 20 && ((int)cb[nextChar]) != 13)
                ) {
                    nChars = 0;
                    nextChar = 0;
                    continue;
                }
                /* Skip a leftover '\n', if necessary */
                if (omitLF && (cb[nextChar] == '\n'))
                    nextChar++;
                skipLF = false;
                omitLF = false;

                charLoop:
                for (i = nextChar; i < nChars; i++) {
                    c = cb[i];
                    if (!isControlChar(c)) {
                        if (c == 148) { cb[i]='\024'; c=cb[i]; }
                        if (c == 141) { cb[i]='\r'; c=cb[i]; }
                        if (c == 160) { cb[i]=' '; c=cb[i]; }
                        if (c == 20 && size > 0) {
                            out.write(157);
                            out.write(32);
                            out.write(157);
                            --size;
                        } else if (c == 20 && size <= 0) {
                            // do nothing
                        } else if (c == 34) {
                            out.write(34);
                            out.write(34);
                            out.write(157);
                            out.write(32);
                            out.write(157);
                            ++size;
                        } else {
                            out.write(mask && c != 13 && c != 10 ? '*' : c);
                            ++size;
                        }
                        out.flush();
                    }
                    if ((c == '\n') || (c == '\r')) {
                        eol = true;
                        break charLoop;
                    }
                }

                startChar = nextChar;
                nextChar = i;

                if (eol) {
                    String str;
                    if (s == null) {
                        str = new String(cb, startChar, i - startChar);
                    } else {
                        s.append(cb, startChar, i - startChar);
                        str = s.toString();
                    }
                    nextChar++;
                    if (c == '\r') {
                        skipLF = true;
                    }
                    out.flush();

                    return decode(str);
                }

                if (s == null)
                    s = new StringBuffer(defaultExpectedLineLength);
                s.append(cb, startChar, i - startChar);
            }
        }
    }

    // CICCIO
    protected String decode(String s) {
        byte[] bytes = s.getBytes(ISO_8859_1);
        byte[] output = new byte[s.length()];
        int i=-1;
        for (byte b: bytes) {
            if (b == 20 && i < 0) {
                continue;
            } else if (b == 20 && i >= 0) {
                i--;
                continue;
            } else if (b >= -63 && b <= -38) {
                b += 128;
            } else if (b >= 65 && b <= 90) {
                b += 32;
            } else if (b >= 97 && b <= 122) {
                b -= 32;
            }
            output[++i] = b;
        }
        return new String(output, 0, i+1, ISO_8859_1);
    }

    public String readPassword() throws IOException {
        return readLine(false, 0, true);
    }

    public String readLine() throws IOException {
        return readLine(false, 0);
    }

    public String readLine(int maxLength) throws IOException {
        return readLine(false, maxLength);
    }

    @Override
    public long skip(long n) throws IOException {
        if (n < 0L) {
            throw new IllegalArgumentException("skip value is negative");
        }
        synchronized (lock) {
            ensureOpen();
            long r = n;
            while (r > 0) {
                if (nextChar >= nChars)
                    fill();
                if (nextChar >= nChars) /* EOF */
                    break;
                if (skipLF) {
                    skipLF = false;
                    if (cb[nextChar] == '\n') {
                        nextChar++;
                    }
                }
                long d = ((long) nChars) - ((long) nextChar);
                if (r <= d) {
                    nextChar += r;
                    r = 0;
                    break;
                }
                else {
                    r -= d;
                    nextChar = nChars;
                }
            }
            return n - r;
        }
    }

    @Override
    public boolean ready() throws IOException {
        synchronized (lock) {
            ensureOpen();

            /*
             * If newline needs to be skipped and the next char to be read
             * is a newline character, then just skip it right away.
             */
            if (skipLF) {
                /* Note that in.ready() will return true if and only if the next
                 * read on the stream will not block.
                 */
                if (nextChar >= nChars && in.ready()) {
                    fill();
                }
                if (nextChar < nChars) {
                    if (cb[nextChar] == '\n')
                        nextChar++;
                    skipLF = false;
                }
            }
            return (nextChar < nChars) || in.ready();
        }
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public void mark(int readAheadLimit) throws IOException {
        if (readAheadLimit < 0) {
            throw new IllegalArgumentException("Read-ahead limit < 0");
        }
        synchronized (lock) {
            ensureOpen();
            this.readAheadLimit = readAheadLimit;
            markedChar = nextChar;
            markedSkipLF = skipLF;
        }
    }

    @Override
    public void reset() throws IOException {
        synchronized (lock) {
            ensureOpen();
            if (markedChar < 0)
                throw new IOException((markedChar == INVALIDATED)
                    ? "Mark invalid"
                    : "Stream not marked");
            nextChar = markedChar;
            skipLF = markedSkipLF;
        }
    }

    public void close() throws IOException {
        synchronized (lock) {
            if (in == null)
                return;
            try {
                in.close();
            } finally {
                in = null;
                cb = null;
            }
        }
    }

    public void resetInput() throws IOException {
        final int THRESHOLD = 192;
        byte[] buffer = new byte[THRESHOLD];

        int count = 0;
        while (ready() && count < THRESHOLD) {
            int key = read();
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

    public boolean keyPressed() throws IOException {
        boolean result = false;
        while (in.ready()) {
            result = true;
            readKey();
        }
        return result;
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

    abstract public void cls();
    abstract public void newline();

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
        if(is == null) {
            return Collections.emptyList();
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            return br.lines().collect(Collectors.toList());
        }
    }

    public abstract int backspace();
    public abstract boolean isBackspace(int ch);
    public abstract void writeDoublequotes();

}