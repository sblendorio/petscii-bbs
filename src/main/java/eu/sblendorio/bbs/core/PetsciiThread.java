package eu.sblendorio.bbs.core;

import static eu.sblendorio.bbs.core.Keys.DOWN;
import static eu.sblendorio.bbs.core.Keys.HOME;
import static eu.sblendorio.bbs.core.Keys.RIGHT;
import java.io.UncheckedIOException;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.substring;
import static org.apache.commons.lang3.StringUtils.trim;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.io.IOUtils;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PetsciiThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(PetsciiThread.class);

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
    protected long clientId;
    protected String clientName;
    protected Class clientClass;
    protected Socket socket = null;
    protected CbmInputOutput cbm;
    protected Object customObject = null;

    protected PetsciiThread child = null;
    protected PetsciiThread parent = null;

    protected boolean keepAlive = true;
    protected long keepAliveTimeout = -1; // inherit from caller
    protected long keepAliveInterval = 1000L * 60L * 2L; // send char every 2 minutes
    protected int keepAliveChar = 1;
    protected KeepAliveThread keepAliveThread;

    public class KeepAliveThread extends Thread {
        private final static long ONE_HOUR = 1000L * 60L * 60L;
        private long startTimestamp = System.currentTimeMillis();
        private AtomicBoolean running = new AtomicBoolean(true);

        @Override
        public void interrupt() {
            running.set(false);
            super.interrupt();
        }

        @Override
        public void run() {
            while (running.get()) {
                try {
                    Thread.sleep(keepAliveInterval);
                    if (keepAlive
                        && System.currentTimeMillis() - startTimestamp < (keepAliveTimeout <= 0 ? ONE_HOUR : keepAliveTimeout)
                        && !PetsciiThread.this.quoteMode()
                        && running.get())
                        cbm.write(keepAliveChar);
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
        this.keepAlive = keepAlive;
        keepAliveThread.interrupt();
        keepAliveThread = new KeepAliveThread();
        if (parent != null) {
            parent.keepAliveThread = keepAliveThread;
        }
        keepAliveThread.start();
    }

    protected static Map<Long, PetsciiThread> clients = defaultClientsMapImplementation();

    static ConcurrentHashMap<Long, PetsciiThread> defaultClientsMapImplementation() {
        return new ConcurrentHashMap<>();
    }

    protected static AtomicLong clientCount = new AtomicLong(0);

    public abstract void doLoop() throws Exception;

    public void receive(long senderId, Object message) {
        if (child == null)
            log("WARNING: default receive method from [" + getClass().getSimpleName() + "] sender #" + senderId + ", message=\"" + message + "\".");
        else
            child.receive(senderId, message);
    }

    public int send(long receiverId, Object message) {
        PetsciiThread receiver = getClients().get(receiverId);
        if (receiver == null) return 1;
        receiver.receive(getClientId(), message);
        return 0;
    }

    private void setClientName(final String clientName) {
        this.clientName = clientName;
    }

    public int changeClientName(String clientName) {
        clientName = trim(clientName);
        if (isBlank(clientName) || clientName.matches("(?i)^client[0-9]+$")) return -1;
        for (Map.Entry<Long, PetsciiThread> entry: clients.entrySet()) {
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

    static Optional<PetsciiThread> getClientByName(final String clientName) {
        for (Map.Entry<Long, PetsciiThread> entry: clients.entrySet()) {
            if (defaultString(clientName).equals(entry.getValue().getClientName())) {
                return Optional.of(entry.getValue());
            }
        }
        return Optional.empty();
    }

    public String getClientName() { return clientName; }

    public void contextFrom(PetsciiThread source) {
        setSocket(source.socket);
        setCbmInputOutput(source.cbm);
        setClientId(source.getClientId());
        setClientName(source.getClientName());
        parent = source;
    }

    public void setCbmInputOutput(CbmInputOutput cbm) {
        this.cbm = cbm;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setClientId(long id) { this.clientId = id; }
    
    public long getClientId() { return clientId; }
    
    public Long getClientIdByName(String name) {
        return clients.entrySet().stream()
                .filter(x -> x.getValue().getClientName().equals(name))
                .findAny()
                .map(thread -> thread.getKey())
                .orElse(null);
    }

    public Class getClientClass() { return clientClass; }

    @Override
    public void run() {
        this.startTimestamp = System.currentTimeMillis();
        try {
            keepAliveThread = new KeepAliveThread();
            setClientId(clientCount.incrementAndGet());
            clientClass = getClass();
            log("New connection at " + socket);
            Thread.sleep(200);
            cbm.resetInput();
            setClientName("client"+getClientId());
            clients.put(getClientId(), this);
            keepAliveThread.start();
            doLoop();
        } catch (CbmIOException e) {
            log("EOF " + e);
        } catch (SocketTimeoutException e) {
            log("TIMEOUT " + e);
        } catch (SocketException e) {
            log("BROKEN PIPE " + e);
        } catch (RuntimeException e) {
            if (e.getCause() == null) {
                e.printStackTrace();
            } else if (e.getCause() instanceof CbmIOException) {
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
            keepAliveThread.interrupt();
            clients.remove(getClientId());
            log("STOP. Connection CLOSED.");
        }
    }

    public boolean launch(PetsciiThread bbs) throws Exception {
        final boolean oldKeepAlive = keepAlive;
        final long oldKeepAliveTimeout = keepAliveTimeout;
        final long oldKeepAliveInterval = keepAliveInterval;
        final int oldKeepAliveChar = keepAliveChar;

        try {
            keepAlive = bbs.keepAlive;
            keepAliveTimeout = bbs.keepAliveTimeout <= 0 ? oldKeepAliveTimeout : bbs.keepAliveTimeout;
            keepAliveInterval = bbs.keepAliveInterval;
            keepAliveChar = bbs.keepAliveChar;

            bbs.contextFrom(this);
            child = bbs;
            clientClass = bbs.clientClass = bbs.getClass();
            try {
                keepAliveThread.interrupt();
                keepAliveThread = new KeepAliveThread();
                bbs.keepAliveThread = keepAliveThread;
                keepAliveThread.start();
            } catch (Exception e) {
                logger.info("Error during KeepAliveThread restart", e);
            }
            keepAliveThread.restartKeepAlive();
            bbs.doLoop();
            return true;
        } catch (SocketException | SocketTimeoutException | CbmIOException e) {
            throw e;
        } catch (Exception e) {
            child = null;
            clientClass = getClass();
            if (e instanceof RuntimeException && !(e instanceof UncheckedIOException) && e.getCause() != null) throw e;
            log(e.getClass().getSimpleName() + " during launching of " + bbs.getClass().getSimpleName() + " within " +
                this.getClass().getSimpleName() + ". Launch interrupted. Stack trace:");
            logger.error("Launch interrupted", e);
            return false;
        } finally {
            keepAlive = oldKeepAlive;
            keepAliveTimeout = oldKeepAliveTimeout;
            keepAliveInterval = oldKeepAliveInterval;
            keepAliveChar = oldKeepAliveChar;
            try {
                keepAliveThread.interrupt();
                keepAliveThread = new KeepAliveThread();
                keepAliveThread.start();
            } catch (Exception e) {
                logger.info("Error during KeepAliveThread restart", e);
            }
            keepAliveThread.restartKeepAlive();

            child = null;
            clientClass = getClass();
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
    public void write(byte[] buf, int off, int len) { cbm.write(buf, off, len); }
    public void write(byte[] b) { cbm.write(b); }
    public void write(int b) { cbm.write(b); }
    public void write(int... b) { cbm.write(b); }
    public void flush() { cbm.flush(); }
    public void cls() { cbm.cls(); }
    public void newline() { cbm.newline(); }
    public void printlnRaw(String msg) { cbm.printlnRaw(msg); }
    public void printRaw(String msg) { cbm.printRaw(msg); }
    public void print(String msg) { cbm.print(msg); }
    public void println(String msg) { cbm.println(msg); }
    public void println() { println(EMPTY); }

    public String readLine() throws IOException {
        keepAliveThread.restartKeepAlive();
        final String result = cbm.readLine();
        keepAliveThread.restartKeepAlive();
        return result;
    }

    public String readLine(int maxLength) throws IOException {
        keepAliveThread.restartKeepAlive();
        final String result = cbm.readLine(maxLength);
        keepAliveThread.restartKeepAlive();
        return result;
    }

    public String readPassword() throws IOException {
        keepAliveThread.restartKeepAlive();
        final String result = cbm.readPassword();
        keepAliveThread.restartKeepAlive();
        return result;
    }

    public int readKey() throws IOException {
        keepAliveThread.restartKeepAlive();
        final int result = cbm.readKey();
        keepAliveThread.restartKeepAlive();
        return result;
    }

    public boolean quoteMode() { return cbm.quoteMode(); }
    public void setQuoteMode(boolean q) { this.cbm.setQuoteMode(q); }
    public void resetInput() throws IOException { cbm.resetInput(); }
    public void writeRawFile(String filename) throws IOException { cbm.writeRawFile(filename); }
    public byte[] readBinaryFile(String filename) throws IOException { return cbm.readBinaryFile(filename); }

    public List<String> readTextFile(String filename) throws IOException {
        return CbmInputOutput.readTextFile(filename);
    }

    public void gotoXY(int x, int y) {
        write(HOME);
        for (int i=0; i<y; ++i) write(DOWN);
        for (int i=0; i<x; ++i) write(RIGHT);
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
        int responseCode = conn.getResponseCode();
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
                throw new CbmIOException("Error during download from "+url);
            }
        }
        catch (IOException e) {
            throw new CbmIOException("Timeout during download from "+url);
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

    public static Map<Long, PetsciiThread> getClients() {
        return clients;
    }

    public Object getCustomObject() { return customObject; }
    public void setCustomObject(Object obj) { this.customObject = obj; }
}
