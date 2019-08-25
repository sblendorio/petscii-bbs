package eu.sblendorio.bbs.core;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static eu.sblendorio.bbs.core.Utils.isControlChar;
import static eu.sblendorio.bbs.core.Utils.isPrintableChar;
import static java.lang.System.arraycopy;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.substring;

/* This class is a modified version of original BufferedReader from original java IO library */
public class CbmInputOutput extends Reader {

    private Reader in;
    private PrintStream out;

    private char cb[];
    private int nChars, nextChar;

    private static final int INVALIDATED = -2;
    private static final int UNMARKED = -1;
    private int markedChar = UNMARKED;
    private int readAheadLimit = 0; /* Valid only when markedChar > 0 */

    /** If the next character is a line feed, skip it */
    private boolean skipLF = false;

    /** The skipLF flag when the mark was set */
    private boolean markedSkipLF = false;

    private static int defaultCharBufferSize = 8192;
    private static int defaultExpectedLineLength = 80;

    private CbmInputOutput(Reader in, int sz) {
        super(in);
        if (sz <= 0)
            throw new IllegalArgumentException("Buffer size <= 0");
        this.in = in;
        cb = new char[sz];
        nextChar = nChars = 0;
    }

    public CbmInputOutput(Socket socket) throws IOException {
        this(new InputStreamReader(socket.getInputStream(), ISO_8859_1), defaultCharBufferSize);
        this.out = new PrintStream(socket.getOutputStream(), true, ISO_8859_1.name());
    }

    public PrintStream out() {return out;}

    public int readKey() throws IOException {
        final int result = in.read();
        if (result == -1) throw new CbmIOException("CbmInputOutput::readKey()");
        return result;
    }

    /** Checks to make sure that the stream has not been closed */
    private void ensureOpen() throws IOException {
        if (in == null)
            throw new IOException("Stream closed");
    }

    /**
     * Fills the input buffer, taking the mark into account if it is valid.
     */
    private void fill() throws IOException {
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
                    char ncb[] = new char[readAheadLimit];
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
    private int read1(char[] cbuf, int off, int len) throws IOException {
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

    public int read(char cbuf[], int off, int len) throws IOException {
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

    public String readLine(boolean ignoreLF, int maxLength, boolean mask) throws IOException {
        StringBuffer s = null;
        int startChar;
        synchronized (lock) {
            int size = 0;
            ensureOpen();
            boolean omitLF = ignoreLF || skipLF;

            bufferLoop:
            for (;;) {
                if (nextChar >= nChars)
                    fill();
                if (nextChar >= nChars) { /* EOF */
                    if (s != null && s.length() > 0) {
                        final String missingInput = decode(s.toString());
                        throw new CbmIOException("CbmInputOutput::readLine(), missingInput='"+missingInput+"'", missingInput);
                    } else
                        throw new CbmIOException("CbmInputOutput::readLine()");
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

    private String decode(String s) throws IOException {
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
        final String result = new String(output, 0, i+1, ISO_8859_1);
        return result;
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
                long d = nChars - nextChar;
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

    public boolean markSupported() {
        return true;
    }

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
        resetInput(false);
    }

    public void resetInput(boolean sendResponse) throws IOException {
        final int THRESHOLD = 2048;
        byte[] buffer = new byte[THRESHOLD];

        int count = 0;
        while (ready() && count < THRESHOLD) {
            int key = read();
            buffer[count++] = (byte) key;
            if (key == -1) throw new CbmIOException("CbmInputOutput::resetInput()");
        }
        if (count == 0) return;

        byte[] excludedInput = new byte[count];
        arraycopy(buffer, 0, excludedInput, 0, count);
        final String missingInput = new String(excludedInput, ISO_8859_1);
        System.err.println("Flushing input buffer: '" +
                substring(missingInput
                        .replaceAll("\r+", "\\\\r")
                        .replaceAll("\n+", "\\\\n"),0,120) +
                (missingInput.length() > 120 ? "..." : EMPTY) +
                "', len = " + missingInput.length());

        if (count >= THRESHOLD) {
            out.flush();
            out.close();
            this.close();
            throw new CbmIOException("SEVERE. CbmInputOutput::resetInput, potential DoS detected.");
        } else if (missingInput.matches("(?is)^(G?ET|P?OST|H?EAD|P?UT|D?ELETE|C?ONNECT) [^\n]+ HTTP/[0-9.]+.*")) {
            if (sendResponse)  {
                /*
                out.println("HTTP/1.1 200 OK\n" +
                        "Server: PETSCII BBS Server for 8-bit Commodore Computers\n" +
                        "Content-Type: text/html; charset=utf-8\n" +
                        "Connection: Closed\n" +
                        "\n" +
                        httpMessage);
                */
            }
            out.flush();
            out.close();
            this.close();
            throw new CbmIOException("HTTP Connection detected, closing socket" + (sendResponse ? " [sendResponse]." : "."));
        } else if (missingInput.matches("(?is)^.*Cookie: mstshash=[a-z].*")) {
            out.flush();
            out.close();
            this.close();
            throw new CbmIOException("MICROSOFT REMOTE DESKTOP Connection detected, closing socket");
        } else if (missingInput.matches("(?is)^.*P?uTTYPuTTYPuTTY.*")) {
            out.flush();
            out.close();
            this.close();
            throw new CbmIOException("Weird PuTTY Connection detected, closing socket");
        } else if (
                missingInput.matches("(?is)^SSH-[0-9\\.]+-.*") ||
                missingInput.matches("(?is)^.*Handshake failed.*") ||
                missingInput.matches("(?is)^.*x?term-[0-9]+color.*")
        ) {
            out.flush();
            out.close();
            this.close();
            throw new CbmIOException("SECURE SHELL (ssh) Connection detected, closing socket");
        } else if (missingInput.matches("(?is)^R?FB [0-9][0-9][0-9]\\.[0-9][0-9][0-9]\n.*")) {
            out.flush();
            out.close();
            this.close();
            throw new CbmIOException("VNC Connection detected, closing socket");
        }
    }

    public void write(byte[] buf, int off, int len) { out.write(buf, off, len); }
    public void write(byte[] b) throws IOException { out.write(b); }
    public void write(int b) { out.write(b); }
    public void write(int... b) { for (int c: b) out.write(c); }
    public void flush() { out.flush(); }
    public void cls() { out.write(147); }
    public void newline() { out.write(13); }
    public void printlnRaw(String s) { out.print(s); newline(); }
    public void printRaw(String s) { out.print(s); }
    public void println(String msg) { print(msg); newline(); }

    public void print(String msg) {
        if (msg == null) return;

        for (char c: msg.toCharArray()) {
            if (!isPrintableChar(c) && c != '\r' && c != '\n')
                continue;
            else if (c == '_')
                c = (char) 228;
            else if (c >= 'a' && c <= 'z')
                c = Character.toUpperCase(c);
            else if (c >= 'A' && c <= 'Z')
                c = Character.toLowerCase(c);

            out.write(c);
        }
    }

    public void writeRawFile(String filename) throws Exception {
        write(readBinaryFile(filename));
        flush();
    }

    public byte[] readBinaryFile(String filename) throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
             ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[2048];
            for (int len = is.read(buffer); len != -1; len = is.read(buffer)) os.write(buffer, 0, len);
            return os.toByteArray();
        }
    }

    public List<String> readTextFile(String filename) throws Exception {
        List<String> result = new ArrayList<>();
        String line;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            while ((line = br.readLine()) != null) result.add(line);
        } finally {
            return result;
        }
    }

    public static final String httpMessage = "<html><head><title>Warning</title></head>" +
            "<body style='font-family: tahoma,verdana,arial,helvetica'><blockquote><br/>" +
            "<center><h1>WARNING: This is <b><u>NOT</u></b> a website</h1></center>" +
            "<br/>" +
            "This is a <b>BBS Server</b> for 8-bit Commodore computers. Please connect through a PETSCII Terminal, " +
            "either a 'real' (a C64) or an emulated one (like <a href='https://sourceforge.net/projects/syncterm/'>SyncTERM</a>). " +
            "If you use a <b>real</b> C64, you can access the Internet though a <b>RR-NET</b> compatible cartridge or " +
            "though a <b>WiFi modem</b> connected to the <i>user port</i>." +
            "<br/><br/><center>" +
            "<img src='data:image/png;base64, iVBORw0KGgoAAAANSUhEUgAAAMgAAADICAYAAACtWK6eAAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAALEwAACxMBAJqcGAAABKxJREFUeJzt3T+IHHUcxuE3McQ/QWI0kELEQuxS2AgighIsLGJnZ+mJjYWIhdjZaCU2IoKpU4iFRRobgxAEQcgRCIjViRhThBCF6AmGWBiLGOfdXGb2dvfueWCrX+Z339vcB4aZub0EAAAAAAAAAACAsfYsegCSJEeSPD7i+LNJrk40CyydtSTXR7ye2P6Rd4e9ix4AlplAoBAIFAKBQiBQCAQK90G2zztJHhxYeyjJ+RF735vk4MDa5STvjdgbtsVGhu9jrI/ce73svTFy713NKRYUAoFCIFAIBAqBQCEQKAQChUCgEAgUAoFCIFAIBAqBQCEQKAQChUCgEAgUAoFCIFAIBAqBQCEQKAQChUCgEAgUAoFCIFAIBAqBQCEQKAQChUCgEAgUAoFCIFAIBAqBQCEQKAQChUCgEAgUAoFCIFAIBIp9ix5ghTyd5KsRx9891SBb9GiSzRHHH0vyzUSzrByB3L69WdwP+Vhj5t7VZxm7+puHWQQChUCgEAgUAoHCVaybHUuyf2DtUJIvR+z9bJJ7Rhx/pzaTfD3i+EeSvHCHx/6U5PyIr82SuZTk+sDrzMi9N8re6yP3Xi97b4zc+0zZe9brxMivvXBOsaAQCBQCgUIgUAgECoFA4T4IY1xN8lZZ35fkk7L+cZJzk040MYEwxmZ6AGtJXivrp7LkgTjFgkIgUAgECoFAIRAoBALFMl7m/TTJi3Pa+80kJ+e0N7c6mX8u5Q75MMOPxF9L8vDkE23RMgZyKMmROe1935z25f/9fuM15P4M/19fm36crXOKBYVAoBAIFAKBQiBQLONVrFm+HXHs3iRPlfV5vh9nk1wcWPth5N7nMvwJ7r+M3Jsl83n6R8mM+SE+MWPv9hr7sT+rqn3sz6WRe58qe/81cu9JOMWCQiBQCAQKgUAhECgEAsUq3gcZ47Mk35f1d+OJ3//6KMkXA2vtSV3mZJ73QWaZ558/4Fbug8AqEwgUAoFCIFAIBAqBQLGM90FeT/J2WT+f5K6BtdNJXp18InatZQxk6JeK/vVYhgNpNwFhy5xiQSEQKAQChUCgEAgUy3gVa5Z2mfdikqPl2AtJLk8+EayI4+mPyq/NON7j7tvL4+6wygQChUCgEAgUAoFCIFCs4n2QefogyYGBtR+3cxCWg0Bu9v6iB2C5OMWCQiBQCAQKgUAhECgEAsVOu8x7JsmTZf2lJN+V9eeTXJl0Ilghs/4M9OHFjbYr+X0QWGUCgUIgUAgECoFAsdMu87JcDiQ5VNavJPl5YO3a9OMwi8u822st/f0+vrjRbo9TLCgEAoVAoBAIFAKBQiBQ7Lb7IKeTbJb1l5PsGVi7kOSzySeCFeLPH0zLfRDYyQQChUCgEAgUAoFCIFAIBAqBQCEQKAQChUCgEAgUAoFitz3uPstvGX5PriZ5oBz7R5I/J59oue1JcnDGv/m1rC3FB1QzjWfSH91+Y3GjLczh9PfkxOJGm4ZTLCgEAoVAoBAIFAKBQiBQCAQKgUAhECgEAoVAoBAIFAKBQiBQ+H2Q6byS5LlFD7HN9i96gHkTyHSO3nixgzjFgkIgUAgECoFAIRAoBAIAAAAAAAAAADDe30hxG6K21esTAAAAAElFTkSuQmCC'></img>" +
            "</center></body></html>";

}
