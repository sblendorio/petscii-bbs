package eu.sblendorio.bbs.core;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import static eu.sblendorio.bbs.core.HtmlUtils.utilHtmlClean;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.io.IOUtils;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.substring;
import static org.apache.commons.lang3.StringUtils.trim;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BbsThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(BbsThread.class);

    public static class DownloadData {
        private final String filename;
        private final byte[] content;

        public DownloadData(final String filename, final byte[] content) {
            this.filename = filename;
            this.content = content;
        }

        public String getFilename() { return filename; }
        public byte[] getContent() { return content; }
    }

    protected long startTimestamp = 0;
    protected InetAddress ipAddress = null;
    protected InetAddress serverAddress = null;
    protected int serverPort = 0;
    protected long clientId;
    protected String clientName;
    protected Class clientClass;
    protected Socket socket = null;
    protected BbsInputOutput io;
    protected Map<String, Object> customObject = new HashMap<>();

    protected BbsThread child = null;
    protected BbsThread parent = null;
    protected Boolean localEcho = null;

    protected boolean keepAlive = true;
    protected long keepAliveTimeout = -1; // inherit from caller
    protected long keepAliveInterval = 1000L * 60L * 1L; // send char every 1 minute
    public int keepAliveChar = 1;
    protected KeepAliveThread keepAliveThread;

    public abstract String getTerminalType();

    public class KeepAliveThread extends Thread {
        private final static long ONE_HOUR = 1000L * 60L * 60L;
        private long startTimestamp = System.currentTimeMillis();
        private AtomicBoolean running = new AtomicBoolean(true);

        @Override
        public void interrupt() {
            running.set(false);
            super.interrupt();
        }

        public long getStartTimestamp() {
            return startTimestamp;
        }

        @Override
        public void run() {
            while (!Thread.interrupted() && running.get()) {
                try {
                    Thread.sleep(keepAliveInterval);
                    if (keepAlive
                            && System.currentTimeMillis() - startTimestamp < (keepAliveTimeout <= 0 ? ONE_HOUR : keepAliveTimeout)
                            && !BbsThread.this.quoteMode()
                            && running.get()) {
                        try {
                            io.write(keepAliveChar);
                        } catch (Exception e) {
                            io.shutdown();
                            BbsThread.this.getRoot().io.shutdown();
                        }
                    }
                } catch (InterruptedException e) {
                    // Thread interrupted
                }
            }
        }

        public void restartKeepAlive() {
            startTimestamp = System.currentTimeMillis();
        }
    }

    public void updateKeepAlive(boolean keepAlive) {
        BbsThread root = getRoot();
        root.keepAlive = keepAlive;
        root.keepAliveThread.interrupt();
        root.keepAliveThread = root.new KeepAliveThread();
        root.keepAliveThread.start();
    }

    public void initBbs() throws Exception { }

    abstract public BbsInputOutput buildIO(Socket socket) throws IOException;

    protected static Map<Long, BbsThread> clients = defaultClientsMapImplementation();

    static ConcurrentHashMap<Long, BbsThread> defaultClientsMapImplementation() {
        return new ConcurrentHashMap<>();
    }

    protected static AtomicLong clientCount = new AtomicLong(0);

    public abstract void doLoop() throws Exception;

    public void receive(long senderId, Object message) {
        log("ENTERING BbsThread.receive(senderId="+senderId+", message="+message+"). child="+child+", child.class="+(child==null?"null":child.getClass().getSimpleName())+", child.clientclass="+(child==null||child.getClientClass()==null?"null":child.getClientClass().getSimpleName()));
        if (child == null) {
            log("WARNING: default receive method from [" + getClass().getSimpleName() + "] sender #" + senderId + ", message=\"" + message + "\".");
        } else {
            log("RAISING UP receive, to child");
            child.receive(senderId, message);
        }
    }

    public int send(long receiverId, Object message) {
        // FIXME here is potential hangup for chat
        log("START. class="+this.getClass().getSimpleName()+"/"+this.getClientName()+", send(receiverId="+receiverId+", message="+message);
        BbsThread receiver = getClients().get(receiverId);
        log("INLINE. receiver="+receiver+", receiver.class="+receiver.getClass().getSimpleName());
        if (receiver == null) return 1;
        log("INLINE. BEFORE call receiver.receive(clientId="+getClientId()+", message="+message);
        receiver.receive(getClientId(), message);
        log("INLINE. AFTER call receiver.receive(clientId="+getClientId()+", message="+message);
        log("END. class="+this.getClass().getSimpleName()+"/"+this.getClientName()+", send(receiverId="+receiverId+", message="+message);
        return 0;
    }

    private void setClientName(final String clientName) {
        this.clientName = clientName;
    }

    public int changeClientName(String clientName) {
        clientName = trim(clientName);
        if (isBlank(clientName) || clientName.matches("(?i)^client[0-9]+$")) return -1;
        for (Map.Entry<Long, BbsThread> entry: clients.entrySet()) {
            if (entry.getKey() != getClientId() && isNotBlank(entry.getValue().getClientName()) && entry.getValue().getClientName().equals(clientName)) {
                return -2;
            }
        }

        changeClientName(this.clientName, clientName);
        setClientName(clientName);
        return 0;
    }

    static void changeClientName(final String sourceClientName, final String targetClientName) {
        getClientByName(sourceClientName).ifPresent(client -> client.setClientName(targetClientName));
    }

    static Optional<BbsThread> getClientByName(final String clientName) {
        for (Map.Entry<Long, BbsThread> entry: clients.entrySet()) {
            if (defaultString(clientName).equals(entry.getValue().getClientName())) {
                return Optional.of(entry.getValue());
            }
        }
        return Optional.empty();
    }

    public String getClientName() { return clientName; }

    public void setBbsInputOutput(BbsInputOutput io) {
        this.io = io;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setClientId(long id) { this.clientId = id; }

    public long getClientId() { return clientId; }

    public Long getClientIdByName(String name, Comparator<String> comparator) {
        return clients.entrySet().stream()
            .filter(x -> comparator.compare(x.getValue().getClientName(), name) == 0)
            .findAny()
            .map(thread -> thread.getKey())
            .orElse(null);
    }

    public Long getClientIdByName(String name) {
        return getClientIdByName(name, String::compareTo);
    }

    public Class getClientClass() { return clientClass; }

    @Override
    public void run() {
        this.startTimestamp = System.currentTimeMillis();
        try {
            keepAliveThread = new KeepAliveThread();
            setClientId(clientCount.incrementAndGet());
            clientClass = getClass();
            ipAddress = socket.getInetAddress();
            serverAddress = socket.getLocalAddress();
            serverPort = socket.getLocalPort();
            log("New connection at " + socket + ", server="+serverAddress.getHostAddress());
            Thread.sleep(200);
            boolean qMode = io.out.quoteMode();
            if (initializingBytes() != null) {
                io.out.write(initializingBytes());
                io.resetInput();
            }
            initBbs();
            io.out.setQuoteMode(qMode);
            setClientName("client"+getClientId());
            clients.put(getClientId(), this);
            keepAliveThread.start();
            doLoop();
        } catch (BbsIOException e) {
            log("EOF " + e);
        } catch (SocketTimeoutException e) {
            log("TIMEOUT " + e);
        } catch (SocketException e) {
            log("BROKEN PIPE " + e);
        } catch (RuntimeException e) {
            if (e.getCause() == null) {
                e.printStackTrace();
            } else if (e.getCause() instanceof BbsIOException) {
                log("EOF " + e);
            } else if (e.getCause() instanceof SocketTimeoutException) {
                log("TIMEOUT " + e);
            } else if (e.getCause() instanceof SocketException) {
                log("BROKEN PIPE " + e);
            } else {
                e.printStackTrace();
            }
        } catch (Exception e) {
            log("ERROR handling: " + e);
            logger.error("ERROR handling", e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                log("Couldn't close a socket, what's going on?");
            }
            if (keepAliveThread != null) keepAliveThread.interrupt();
            clients.remove(clientId);
            if (getRoot() != null && getRoot().keepAliveThread != null) getRoot().keepAliveThread.interrupt();
            log("STOP. Connection CLOSED.");
        }
    }

    class BbsStatus {
        boolean keepAlive;
        long keepAliveTimeout;
        long keepAliveInterval;
        int keepAliveChar;
        BbsInputOutput io;
        Class clientClass;
        BbsThread child;

        BbsStatus(boolean keepAlive, long keepAliveTimeout, long keepAliveInterval, int keepAliveChar,
                  BbsInputOutput io, Class clientClass, BbsThread child) {
            this.keepAlive = keepAlive;
            this.keepAliveTimeout = keepAliveTimeout;
            this.keepAliveInterval = keepAliveInterval;
            this.keepAliveChar = keepAliveChar;
            this.io = io;
            this.clientClass = clientClass;
            this.child = child;
        }
    }

    private Deque<BbsStatus> bbsStack = new ConcurrentLinkedDeque<>();

    public BbsThread getRoot() {
        BbsThread root = this;
        while (root.parent != null) {
            root = root.parent;
        }
        return root;
    }

    public byte[] initializingBytes() {
        return null;
    }

    public boolean launch(BbsThread bbs) throws Exception {
        BbsThread root = getRoot();
        try {
            bbs.serverAddress = root.serverAddress;
            bbs.serverPort = root.serverPort;
            bbs.ipAddress = root.ipAddress;
            bbs.socket = root.socket;
            bbs.io = bbs.buildIO(socket);
            bbs.io.localEcho = bbs.localEcho;
            bbs.parent = this;
            bbs.keepAliveTimeout = bbs.keepAliveTimeout <= 0 ? root.keepAliveTimeout : bbs.keepAliveTimeout;
            bbs.clientId = root.clientId;
            bbs.clientName = root.clientName;
            if (bbs.localEcho == null) bbs.setLocalEcho(this.getLocalEcho());

            root.bbsStack.push(new BbsStatus(
                root.keepAlive,
                root.keepAliveTimeout,
                root.keepAliveInterval,
                root.keepAliveChar,
                root.io,
                root.clientClass,
                root.child));

            root.keepAlive = bbs.keepAlive;
            root.keepAliveTimeout = bbs.keepAliveTimeout;
            root.keepAliveInterval = bbs.keepAliveInterval;
            root.keepAliveChar = bbs.keepAliveChar;
            root.io = bbs.io;
            root.clientClass = bbs.clientClass = bbs.getClass();
            child = bbs;
            try {
                root.keepAliveThread.interrupt();
                root.keepAliveThread = root.new KeepAliveThread();
                bbs.keepAliveThread = root.keepAliveThread;
                root.keepAliveThread.start();
            } catch (Exception e) {
                logger.info("Error during KeepAliveThread restart", e);
            }
            root.keepAliveThread.restartKeepAlive();
            boolean qMode = bbs.io.out.quoteMode();
            if (bbs.initializingBytes() != null) {
                bbs.io.out.write(bbs.initializingBytes());
                bbs.io.resetInput();
            }
            bbs.initBbs();
            bbs.io.out.setQuoteMode(qMode);
            bbs.doLoop();
            return true;
        } catch (SocketException | SocketTimeoutException | BbsIOException e) {
            throw e;
        } catch (Exception e) {
            child = null;
            clientClass = getClass();
            if (e instanceof GoBackException) return true;
            if (e instanceof RuntimeException && !(e instanceof UncheckedIOException) && e.getCause() != null) throw e;
            log(e.getClass().getSimpleName() + " during launching of " + bbs.getClass().getSimpleName() + " within " +
                this.getClass().getSimpleName() + ". Launch interrupted. Stack trace:");
            logger.error("Launch interrupted", e);
            return false;
        } finally {
            BbsStatus bbsStatus = root.bbsStack.pop();
            root.keepAlive = bbsStatus.keepAlive;
            root.keepAliveTimeout = bbsStatus.keepAliveTimeout;
            root.keepAliveInterval = bbsStatus.keepAliveInterval;
            root.keepAliveChar = bbsStatus.keepAliveChar;
            try {
                root.keepAliveThread.interrupt();
                root.keepAliveThread = root.new KeepAliveThread();
                root.keepAliveThread.start();
            } catch (Exception e) {
                logger.info("Error during KeepAliveThread restart", e);
            }
            root.keepAliveThread.restartKeepAlive();

            root.io = bbsStatus.io;
            root.clientClass = bbsStatus.clientClass;
            this.child = bbsStatus.child;
        }

    }

    public void log(String message) {
        final String logRow =
            substring(new Timestamp(System.currentTimeMillis()).toString() + "000",0,23) +
                " Client #" + getClientId() + ". " +
                message
            ;
        logger.info(logRow);
    }

    public static char chr(int code) { return (char) code; }
    public int keyPressed() throws IOException {
        int ch = io.keyPressed();
        if (ch >= 0) restartKeepAlive();
        return ch;
    }
    public boolean isKeyPressed() throws IOException { return io.keyPressed() != -1; }
    public void write(byte[] buf, int off, int len) {
        boolean savedKeepAlive = getRoot().keepAlive;
        updateKeepAlive(false);
        io.write(buf, off, len);
        updateKeepAlive(savedKeepAlive);
    }
    public void write(byte[] b) {
        boolean savedKeepAlive = getRoot().keepAlive;
        updateKeepAlive(false);
        io.write(b);
        updateKeepAlive(savedKeepAlive);
    }
    public void write(byte b) { io.write(b); }
    public void write(int... b) {
        boolean savedKeepAlive = getRoot().keepAlive;
        updateKeepAlive(false);
        io.write(b);
        updateKeepAlive(savedKeepAlive);
    }
    public void flush() { io.flush(); }
    public abstract void cls();
    public void newline() { io.newline(); }
    public byte[] backspace() { return io.backspace(); }
    public int backspaceKey() { return io.backspaceKey(); }
    public boolean isNewline(int ch) { return io.isNewline(ch); }
    public boolean isBackspace(int ch) { return io.isBackspace(ch); }
    public byte[] newlineBytes() { return io.newlineBytes(); }
    public String newlineString() { return io.newlineString(); }
    public void printlnRaw(String msg) { io.printlnRaw(msg); }
    public void printRaw(String msg) { io.printRaw(msg); }
    public void print(String msg) { io.print(msg); }
    public void println(String msg) { io.println(msg); }
    public void println() { println(EMPTY); }
    public String readLineBuffer() { return io.readLineBuffer(); }
    public int convertToAscii(int ch) { return io.convertToAscii(ch); }
    public void afterReadLineChar() { io.afterReadLineChar(); }
    public void checkBelowLine() { io.checkBelowLine(); }

    public void restartKeepAlive() {
        keepAliveThread.restartKeepAlive();
        getRoot().keepAliveThread.restartKeepAlive();
    }

    public String readLine() throws IOException {
        restartKeepAlive();
        final String result = io.readLine();
        restartKeepAlive();
        return result;
    }

    public String readLine(int maxLength) throws IOException {
        restartKeepAlive();
        final String result = io.readLine(maxLength);
        restartKeepAlive();
        return result;
    }

    public String readLine(Set<Integer> allowedChars) throws IOException {
        restartKeepAlive();
        final String result = io.readLine(allowedChars);
        restartKeepAlive();
        return result;
    }

    public String readPassword() throws IOException {
        restartKeepAlive();
        final String result = io.readPassword();
        restartKeepAlive();
        return result;
    }

    public int readKey() throws IOException {
        restartKeepAlive();
        final int result = io.readKey();
        restartKeepAlive();
        return result;
    }

    public boolean quoteMode() { return io.quoteMode(); }
    public void setQuoteMode(boolean q) { this.io.setQuoteMode(q); }
    public void resetInput() throws IOException { io.resetInput(); }
    public void writeRawFile(String filename) throws IOException { io.writeRawFile(filename); }
    public void optionalCls() { io.optionalCls(); }
    public static byte[] readBinaryFile(String filename) { return BbsInputOutput.readBinaryFile(filename); }

    public List<String> readTextFile(String filename) throws IOException {
        return BbsInputOutput.readTextFile(filename);
    }

    public static Object httpGetJson(String url) throws IOException, ParseException {
        return httpGetJson(url, null);
    }

    public static Object httpGetJson(String url, String userAgent) throws IOException, ParseException {
        final String result = httpGet(url, userAgent);
        return isBlank(result) ? null : new JSONParser().parse(result);
    }

    public static String httpGet(String url) throws IOException {
        return httpGet(url, null);
    }

    public static String httpGet(String url, String userAgent) throws IOException {
        final URL object=new URL(url);
        HttpURLConnection conn = (HttpURLConnection) object.openConnection();
        conn.setRequestProperty("User-Agent", defaultString(userAgent));
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("GET");

        final StringBuilder sb;
        int responseCode = -1;
        try {
            responseCode = conn.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        if (responseCode >= 301 && responseCode <= 399) {
            final String newLocation = conn.getHeaderField("Location");
            return httpGet(newLocation, userAgent);
        } else if (responseCode >= 200 && responseCode <= 299) {
            sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), UTF_8));
            String line;
            while ((line = br.readLine()) != null) sb.append(line + "\n");
            br.close();
            conn.disconnect();
            return sb.toString();
        } else {
            logger.info(conn.getResponseMessage());
        }
        conn.disconnect();
        return null;
    }

    public static byte[] downloadFile(URL url) throws IOException {
        return download(url, null).getContent();
    }

    public static byte[] downloadFile(URL url, String userAgent) throws IOException {
        return download(url, userAgent).getContent();
    }

    public static DownloadData download(URL url) throws IOException {
        return download(url, null);
    }

    public static DownloadData download(URL url, String userAgent) throws IOException {
        if ("ftp".equalsIgnoreCase(url.getProtocol()))
            return ftpDownload(url);
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", defaultString(userAgent));
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);

            int responseCode = conn.getResponseCode();
            final String contentDisposition = defaultString(conn.getHeaderField("Content-Disposition"));
            final String contentPart = contentDisposition.replaceAll("(?is)^.*?;\\s*?filename=['\"](.*?)['\"].*$", "$1");
            final String filename = isEmpty(contentPart) ? url.toString().replaceAll("(?is)^.*/([^\\?&#]+).*$","$1") : contentPart;
            if (responseCode >= 301 && responseCode <= 399) {
                final String newLocation = conn.getHeaderField("Location");
                return download(new URL(newLocation), userAgent);
            } else if (responseCode >= 200 && responseCode <= 299) {
                conn.connect();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                IOUtils.copy(conn.getInputStream(), baos);
                return new DownloadData(filename, baos.toByteArray());
            } else {
                throw new BbsIOException("Error during download from "+url);
            }
        }
        catch (IOException e) {
            throw new BbsIOException("Timeout during download from "+url);
        }
    }

    public static DownloadData ftpDownload(URL url) throws IOException {
        URLConnection conn = url.openConnection();
        InputStream inputStream = conn.getInputStream();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[16384];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        outputStream.close();
        inputStream.close();

        return new DownloadData(
            url.toString().replaceAll("(?is)^.*/([^\\?&#]+).*$","$1"),
            outputStream.toByteArray()
        );
    }

    public static Map<Long, BbsThread> getClients() {
        return clients;
    }

    public Object getCustomObject(String key) { return customObject.get(key); }

    public void setCustomObject(String key, Object obj) { customObject.put(key, obj); }

    public boolean isPrintableChar(int c) {
        return io.isPrintableChar(c);
    }

    public boolean isPrintableChar(char c) {
        return io.isPrintableChar(c);
    }

    public String filterPrintable(String s) {
        return io.filterPrintable(s);
    }

    public String filterPrintableWithNewline(String s) {
        return io.filterPrintableWithNewline(s);
    }

    public int lengthPrintable(String s) {
        return filterPrintable(defaultString(s)).length();
    }

    public void setLocalEcho(boolean value) {
        localEcho = value;
        if (io != null) io.setLocalEcho(value);
    }

    public boolean getLocalEcho() {
        return io == null
            ? (localEcho == null ? true : localEcho)
            : io.getLocalEcho();
    }

    public abstract int getScreenColumns();

    public abstract int getScreenRows();

    public int keyPressed(long timeout) throws IOException {
        long INTERVAL = 150L;
        resetInput();
        if (timeout < 0)
            return readKey();

        int ch;
        long a = System.currentTimeMillis();
        while (!pressed(ch = keyPressed()) && System.currentTimeMillis() - a < timeout) {
            try {
                Thread.sleep(INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (ch >= 0) restartKeepAlive();
        return ch;
    }

    private boolean pressed(int i) {
        return
            i == 8  ||
            i == 9  ||
            i == 10 ||
            i == 13 ||
            i == 20 ||
            i >= 32;
    }

    public String htmlClean(String s) {
        return utilHtmlClean(s);
    }

}
