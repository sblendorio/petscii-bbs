package eu.sblendorio.bbs.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.Collections;
import java.util.HexFormat;
import java.util.List;
import java.util.Set;

import static eu.sblendorio.bbs.core.HtmlUtils.utilHtmlClean;
import static eu.sblendorio.bbs.core.HtmlUtils.utilHtmlDiacriticsToAscii;
import static java.lang.System.arraycopy;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static org.apache.commons.lang3.StringUtils.*;

/* This class is a modified version of original BufferedReader from original java IO library */
public abstract class BbsInputOutput extends Reader {

    private final static byte[] EMPTY_ARRAY = new byte[] {};
    private final static int CHAR_INTERRUPT = 3;

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

    private static final Logger logger = LogManager.getLogger(BbsInputOutput.class);
    private String readBuffer = EMPTY;

    protected Reader in;
    protected QuotedPrintStream out;
    protected Boolean localEcho = null;

    protected Socket socket = null;

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

    private int prevCharacter = 0;
    private long prevMilliseconds = System.currentTimeMillis();

    public void optionalCls() {
    }

    public int returnAlias() {
        return 10;
    }

    public int backspaceAlias() {
        return 8;
    }
    
    public int readKey() throws IOException {
        long DELTA = 1200;

        int result = in.read();
        if (result == returnAlias()) result = 10;
        if (result == backspaceAlias()) result = backspaceKey();
        long deltaMilliseconds = System.currentTimeMillis() - prevMilliseconds;
        prevMilliseconds = System.currentTimeMillis();

        // System.out.println("result="+result+", prevCharacter="+prevCharacter+", deltaMilliseconds="+deltaMilliseconds);
        if (deltaMilliseconds < DELTA && prevCharacter == 19) { // Key substitution for Minitel keyboard
                if (result == 65) result = 10;  // INVIO = NEWLINE
           else if (result == 71) result = 8;   // CORREZIONE = BACKSPACE
           else if (result == 66) result = '-'; // PRECEDENTE = '-'
           else if (result == 72) result = ' '; // SEGUENTE = ' ' (space)
           else if (result == 70) result = '.'; // INDICE = '.'
           else result = 0;
           // INDICE = 70
           // ANNULLA = 69
           // PRECEDENTE = 66
           // RIPETIZ = 67
           // GUIDA = 68
           // CORREZIONE = 71
           // SEGUENTE = 72
           // INVIO = 65
           //System.out.println("Double key for Minitel " + result);
        }
        // for Minitel with PSTN
        if (deltaMilliseconds < DELTA && prevCharacter == 27) {
            if (result == 58) {
                in.read();
                in.read();
            }
            result = 0;
        }

        if (result == -1) throw new BbsIOException("BbsIOException::readKey()");
        prevCharacter = result;

        return result;
    }

    public String readLine(int maxLength, boolean mask) throws IOException {
        return readLine(maxLength, mask, null);
    }

    public void afterReadLineChar() {
    }

    public void checkBelowLine() {
    }


    // 0, false, null, true
    public String readLineUppercaseMandatory() throws IOException {
        return readLineParametric(0, false, null, true, true, true, false);
    }

    public String readLineUppercase() throws IOException {
        return readLineParametric(0, false, null, true, true, false, false);
    }

    public String readLineUppercase(Set<Integer> allowedChars) throws IOException {
        return readLineParametric(0, false, allowedChars, true, true, false, false);
    }

    public String readLine(int maxLength, boolean mask, Set<Integer> allowedChars) throws IOException {
        return readLineParametric(maxLength, mask, allowedChars, true);
    }

    public String readLineParametric(int maxLength, boolean mask, Set<Integer> allowedChars, boolean sendCr) throws IOException {
        return readLineParametric(maxLength, mask, allowedChars, sendCr, false, false, false);
    }
    public String readLineParametric(int maxLength, boolean mask, Set<Integer> allowedChars, boolean sendCr, boolean noEmpty) throws IOException {
        return readLineParametric(maxLength, mask, allowedChars, sendCr, false, false, noEmpty);
    }
    public String readLineParametric(int maxLength, boolean mask, Set<Integer> allowedChars, boolean sendCr, boolean upcase, boolean noEmpty, boolean interruptable) throws IOException {
        int ch;
        readBuffer = EMPTY;
        do {
            ch = readKey();
            if (noEmpty && isNewline(ch) && readBuffer.trim().isEmpty()) {
                ch = 0;
                continue;
            } else if (isBackspace(ch)) {
                if (readBuffer.length() > 0) {
                    if (getLocalEcho()) {
                        writeBackspace();
                        afterReadLineChar();
                        flush();
                    }
                    readBuffer = readBuffer.substring(0, readBuffer.length() - 1);
                }
            } else if (ch == CHAR_INTERRUPT && interruptable) {
                return null;
            } else if (allowedChars != null && !allowedChars.contains(ch)) {
                continue;
            } else if (ch == 34 && (maxLength == 0 || readBuffer.length() < maxLength)) {
                if (getLocalEcho()) {
                    if (mask) write('*');
                    else writeDoublequotes();
                    afterReadLineChar();
                    flush();
                }
                readBuffer += "\"";
            } else if (isPrintableChar(ch) && (maxLength == 0 || readBuffer.length() < maxLength)) {
                if (getLocalEcho()) {
                    if (mask) {
                        write('*');
                    } else if (!upcase || !Character.isLetter(ch)) {
                        write(ch);
                    } else {
                        print(""+(char)Character.toUpperCase(ch));
                    }
                    afterReadLineChar();
                    flush();
                }
                readBuffer += (char) convertToAscii(ch);
            }
        } while (!isNewline(ch));
        if (getLocalEcho() && sendCr) {
            newline();
        }
        final String result = readBuffer;
        readBuffer = EMPTY;
        return upcase ? result.toUpperCase() : result;
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

    public String readLineNoCr() throws IOException {
        return readLineParametric(0, false, null, false);
    }

    public String readLine() throws IOException {
        return readLine(0, false);
    }

    public String readLineNoCr(Set<Integer> allowedChars) throws IOException {
        return readLineParametric(0, false, allowedChars, false);
    }

    public String readLineNoCrInterruptable(Set<Integer> allowedChars) throws IOException {
        return readLineParametric(0, false, allowedChars, false, false, false, true);
    }

    public String readLine(Set<Integer> allowedChars) throws IOException {
        return readLine(0, false, allowedChars);
    }

    public String readLine(int maxLength, Set<Integer> allowedChars) throws IOException {
        return readLine(maxLength, false, allowedChars);
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

    public byte[] resetInput() throws IOException {
        final int THRESHOLD = 2048;
        byte[] buffer = new byte[THRESHOLD];

        int count = 0;
        while (in.ready() && count < THRESHOLD) {
            int key = in.read();
            buffer[count++] = (byte) key;
            if (key == -1) throw new BbsIOException("BbsIOException::resetInput()");
        }
        if (count == 0) return EMPTY_ARRAY;

        String ip = "UNKNOWN";
        try {
            ip = socket.getInetAddress().getHostAddress();
        } catch (Exception e) {
            logger.warn("Something happened during IP retrieval", e);
        }

        final String stringIp = "(suspicious IP: " + ip +")";

        byte[] excludedInput = new byte[count];
        arraycopy(buffer, 0, excludedInput, 0, count);
        final String missingInput = new String(excludedInput, ISO_8859_1);
        // TLS: c0 2b c0 2f c0 (À+À/À)
        // TLS: c0 2b c0 2c c0 (À+À,À)
        // TLS: c0 2f c0 2b c0 (À/À+À)
        // TLS: 16 03 01
        // TLS: 16 03 03
        logger.info("Flushing input buffer (" + ip + "): '{}'{}, len = {}",
            substring(missingInput
                .replaceAll("\r+", "\\\\r")
                .replaceAll("\n+", "\\\\n")
                .replaceAll("\\p{C}", "?"), 0, 120) +
                (missingInput.length() > 120 ? "..." : EMPTY),
            "", //" HEX: "+ HexFormat.ofDelimiter(" ").formatHex(excludedInput),
            missingInput.length());

        if (missingInput.contains("User-Agent: Expanse, a Palo Alto Networks")) {
            out.flush();
            out.close();
            this.close();
            if (!ip.equals("127.0.0.1") && !ip.equals("0:0:0:0:0:0:0:1")) logger.info("CATCH EXPANSEPALOALTO " + ip);
            throw new BbsIOException("EXPANSE-PALOALTO port scanner Connection detected " + stringIp + ", closing socket");
        } else if (missingInput.contains("DWP-Handshake")) {
            out.flush();
            out.close();
            this.close();
            if (!ip.equals("127.0.0.1") && !ip.equals("0:0:0:0:0:0:0:1")) logger.info("CATCH JDWP " + ip);
            throw new BbsIOException("JDWP-Handshake port scanner Connection detected " + stringIp + ", closing socket");
        } else if (missingInput.contains("User-Agent: curl/")) {
            out.flush();
            out.close();
            this.close();
            if (!ip.equals("127.0.0.1") && !ip.equals("0:0:0:0:0:0:0:1")) logger.info("CATCH CURL " + ip);
            throw new BbsIOException("CURL port scanner Connection detected " + stringIp + ", closing socket");
        } else if (missingInput.contains("User-Agent: facebookexternalhit/")) {
            out.flush();
            out.close();
            this.close();
            if (!ip.equals("127.0.0.1") && !ip.equals("0:0:0:0:0:0:0:1")) logger.info("CATCH FACEBOOKEXTERNALHIT " + ip);
            throw new BbsIOException("FACEBOOKEXTERNALHIT Connection detected " + stringIp + ", closing socket");
        } else if (missingInput.contains("User-Agent: libwww-perl/")) {
            out.flush();
            out.close();
            this.close();
            if (!ip.equals("127.0.0.1") && !ip.equals("0:0:0:0:0:0:0:1")) logger.info("CATCH LIBWWWPERL " + ip);
            throw new BbsIOException("LIB-WWW-PERL port scanner Connection detected " + stringIp + ", closing socket");
        } else if (missingInput.matches("(?is)^.* /.* HTTP/1\\.0[^0-9.].*")) {
            out.flush();
            out.close();
            this.close();
            if (!ip.equals("127.0.0.1") && !ip.equals("0:0:0:0:0:0:0:1")) logger.info("CATCH HTTPONEZERO " + ip);
            throw new BbsIOException("HTTP 1.0 connection detected " + stringIp + ", closing socket");
        } else if (missingInput.matches("(?is)^(G?ET|P?OST|H?EAD|P?UT|D?ELETE|C?ONNECT|O?PTIONS) [^\n]+ HTTP.*")) {
            // out.write(10);
            out.flush();
            out.close();
            this.close();
            if (!ip.equals("127.0.0.1") && !ip.equals("0:0:0:0:0:0:0:1")) {
                if (missingInput.matches("(?is)^(P?OST|H?EAD|P?UT|D?ELETE|C?ONNECT|O?PTIONS) .*")) {
                    logger.info("CATCH HTTP " + ip);
                } else if (
                        missingInput.matches("(?is)^G?ET .*") &&
                                !missingInput.matches("(?is)^G?ET / .*") &&
                                !missingInput.matches("(?is)^G?ET /favicon.*") &&
                                !missingInput.matches("(?is)^G?ET /\\?fbclid=.*")
                ) {
                    logger.info("CATCH HTTPGET " + ip);
                }
                throw new BbsIOException("HTTP Connection detected " + stringIp + ", closing socket.");
            }
        } else if (missingInput.matches("(?is)^.*Cookie: mstshash=[a-z].*")) {
            out.flush();
            out.close();
            this.close();
            if (!ip.equals("127.0.0.1") && !ip.equals("0:0:0:0:0:0:0:1")) logger.info("CATCH RDP " + ip);
            throw new BbsIOException("MICROSOFT REMOTE DESKTOP Connection detected " + stringIp + ", closing socket");
        } else if (missingInput.matches("(?is)^.*M?GLNDD_[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+.*")) {
            out.flush();
            out.close();
            this.close();
            if (!ip.equals("127.0.0.1") && !ip.equals("0:0:0:0:0:0:0:1")) logger.info("CATCH MGLNDD " + ip);
            throw new BbsIOException("MGLNDD port scanner Connection detected " + stringIp + ", closing socket");
        // } else if (missingInput.contains("uTTYPuTTYPuTTY")) {
        //     out.flush();
        //     out.close();
        //     this.close();
        //     if (!ip.equals("127.0.0.1") && !ip.equals("0:0:0:0:0:0:0:1")) logger.info("CATCH PUTTY " + ip);
        //     throw new BbsIOException("Weird PuTTY Connection detected " + stringIp + ", closing socket");
        } else if (
            missingInput.matches("(?is)^SSH-[0-9\\.]+-.*") ||
                missingInput.matches("(?is)^.*Handshake failed.*") ||
                missingInput.matches("(?is)^.*x?term-[0-9]+color.*")
        ) {
            out.flush();
            out.close();
            this.close();
            if (!ip.equals("127.0.0.1") && !ip.equals("0:0:0:0:0:0:0:1")) logger.info("CATCH SSH " + ip);
            throw new BbsIOException("SECURE SHELL (ssh) Connection detected " + stringIp + ", closing socket");
        } else if (missingInput.matches("(?is)^R?FB [0-9]+\\.[0-9]+\n.*")) {
            out.flush();
            out.close();
            this.close();
            if (!ip.equals("127.0.0.1") && !ip.equals("0:0:0:0:0:0:0:1")) logger.info("CATCH VNC " + ip);
            throw new BbsIOException("VNC Connection detected " + stringIp + ", closing socket");
        } else if (missingInput.matches("(?is)^.*/bin/busybox.*cat.*/proc.*$")) {
            out.flush();
            out.close();
            this.close();
            if (!ip.equals("127.0.0.1") && !ip.equals("0:0:0:0:0:0:0:1")) logger.info("CATCH SHELLATTACK " + ip);
            throw new BbsIOException("Shell Attack detected " + stringIp + ", closing socket");
        } else if (missingInput.contains("Ì©À/À0À+À,À")) {
            out.flush();
            out.close();
            this.close();
            if (!ip.equals("127.0.0.1") && !ip.equals("0:0:0:0:0:0:0:1")) logger.info("CATCH CLIENTHELLOSSL " + ip);
            throw new BbsIOException("ClientHello SSL connection detected " + stringIp + ", closing socket");
        } else if (count >= THRESHOLD) {
            out.flush();
            out.close();
            this.close();
            if (!ip.equals("127.0.0.1") && !ip.equals("0:0:0:0:0:0:0:1")
                    && !missingInput.contains("À+À/À,À0Ì©Ì¨À") // Facebook click on link (alt: "À+À/Ì©Ì¨À")
                    && !containsIgnoreCase(missingInput,"PRINT") // copy&paste BASIC code
                    && !containsIgnoreCase(missingInput,"GOTO") // copy&paste BASIC code
            )
                logger.info("CATCH DDOS " + ip);
            throw new BbsIOException("SEVERE. BbsIOException::resetInput " + stringIp + ", potential DoS detected.");
        }
        return excludedInput;
    }

    public int keyPressed() throws IOException {
        Integer key = null;
        while (in.ready()) {
            int ch = readKey();
            if (key == null)
                key = ch;
        }
        return key == null ? -1 : key;
    }

    public void write(byte[] buf, int off, int len) {
        if (buf == null) return;
        int size = buf.length;
        for (int i = off; i < off+len; ++i) {
            if (i < size) {
                write(buf[i]);
            }
        }
    }

    public void write(byte[] b) {
        if (b == null) return;
        for (byte ch: b) {
            write(ch);
        }
    }

    public void write(int b) {
        out.write(b);
        if (out.checkError()) {
            throw new RuntimeException("Broken TCP connection");
        }
    }
    public void write(int... b) { for (int c: b) out.write(c); }
    public void flush() { out.flush(); }

    public void printlnRaw(String s) { out.print(s); newline(); }
    public void printRaw(String s) { out.print(s); }
    public void println(String msg) { print(msg); newline(); }

    public void print(String msg) {
        if (msg == null) return;
        for (char c: msg.toCharArray()) {
            out.write(c);
        }
    }

    public void writeRawFile(String filename) throws IOException {
        write(readBinaryFile(filename));
        flush();
    }

    public static byte[] readBinaryFile(String filename) {
        try (InputStream is = BbsInputOutput.class.getClassLoader().getResourceAsStream(filename);
             ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[2048];
            for (int len = is.read(buffer); len != -1; len = is.read(buffer)) os.write(buffer, 0, len);
            return os.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static List<String> readTextFile(String filename) throws IOException {
        try (InputStream is = BbsInputOutput.class.getClassLoader().getResourceAsStream(filename)) {
            return readFromInputStream(is);
        }
    }

    protected static List<String> readFromInputStream(InputStream is) throws IOException {
        if (is == null) {
            return Collections.emptyList();
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            return br.lines().toList();
        }
    }

    public void newline() {
        write(newlineBytes());
    }
    public String newlineString() {
        return new String(newlineBytes(), ISO_8859_1);
    }
    public abstract byte[] newlineBytes();
    public abstract int backspaceKey();
    public abstract byte[] backspace();
    public void writeBackspace() { write(backspace()); }
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

    public String htmlClean(String s) {
        return utilHtmlDiacriticsToAscii(utilHtmlClean(s));
    }

    @Override
    public int read(char[] chars, int i, int i1) throws IOException {
        return in.read(chars, i, i1);
    }

    public void setLocalEcho(boolean value) { this.localEcho = value; }

    public boolean getLocalEcho() { return localEcho == null ? true : localEcho; }

    public void shutdown() {
        try {
            if (in != null) in.close();
        } catch (Exception e) {
            in = null;
            out = null;
            throw new RuntimeException(e);
        } finally {
            try {
                if (out != null) out.close();
            } catch (Exception e) {
                in = null;
                out = null;
            }
        }
    }
}