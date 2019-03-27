package eu.sblendorio.bbs.core;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static eu.sblendorio.bbs.core.Keys.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.*;

public abstract class PetsciiThread extends Thread {
    protected long clientId;
    protected String clientName;
    protected Class clientClass;
    protected Socket socket = null;
    protected CbmInputOutput cbm;
    protected Object customObject = null;

    protected PetsciiThread child = null;
    protected PetsciiThread parent = null;

    protected static Map<Long, PetsciiThread> clients = new ConcurrentHashMap<>();
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
        for (Map.Entry<Long, PetsciiThread> entry: clients.entrySet())
            if (entry.getKey() != getClientId() && isEmpty(entry.getValue().getClientName()) && entry.getValue().getClientName().equals(clientName))
                return -2;

        PetsciiThread client = getClientByName(this.clientName);
        setClientName(clientName);
        client.setClientName(clientName);
        return 0;
    }

    public static PetsciiThread getClientByName(final String clientName) {
        for (Map.Entry<Long, PetsciiThread> entry: clients.entrySet())
            if (defaultString(clientName).equals(entry.getValue().getClientName()))
                return entry.getValue();

        return null;
    }

    public String getClientName() { return clientName; }

    public void contextFrom(PetsciiThread source) throws IOException {
        setSocket(source.socket);
        setCbmInputOutput(source.cbm);
        setClientId(source.getClientId());
        setClientName(source.getClientName());
        parent = source;
    }

    public void setCbmInputOutput(CbmInputOutput cbm) {
        this.cbm = cbm;
    }

    public void setSocket(Socket socket) throws IOException {
        this.socket = socket;
    }

    public void setClientId(long id) { this.clientId = id; }
    public long getClientId() { return clientId; }
    public Class getClientClass() { return clientClass; }

    public void run() {
        try {
            setClientId(clientCount.incrementAndGet());
            clientClass = getClass();
            log("New connection at " + socket);
            Thread.sleep(200);
            cbm.resetInput(true);
            setClientName("client"+getClientId());
            clients.put(getClientId(), this);
            doLoop();
        } catch (CbmIOException e) {
            log("EOF " + e);
        } catch (SocketTimeoutException e) {
            log("TIMEOUT " + e);
        } catch (SocketException e) {
            log("BROKEN PIPE " + e);
        } catch (Exception e) {
            log("ERROR handling: " + e);
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                log("Couldn't close a socket, what's going on?");
            }
            clients.remove(getClientId());
            log("STOP. Connection CLOSED.");
        }
    }

    public boolean launch(PetsciiThread bbs) throws Exception {
        try {
            bbs.contextFrom(this);
            child = bbs;
            clientClass = bbs.clientClass = bbs.getClass();
            bbs.doLoop();
            child = null;
            clientClass = getClass();
            return true;
        } catch (SocketException | SocketTimeoutException | CbmIOException e) {
            throw e;
        } catch (Exception e) {
            child = null;
            clientClass = getClass();
            log(e.getClass().getSimpleName() + " during launching of " + bbs.getClass().getSimpleName()+" within " + this.getClass().getSimpleName()+". Launch interrupted. Stack trace:");
            e.printStackTrace();
            return false;
        }
    }

    public void log(String message) {
        final String logRow =
            substring(new Timestamp(System.currentTimeMillis()).toString() + "000",0,23) +
            " Client #" + getClientId() + ". " +
            message
        ;
        System.err.println(logRow);
    }

    public static char chr(int code) { return (char) code; }
    public void write(byte[] buf, int off, int len) { cbm.write(buf, off, len); }
    public void write(byte[] b) throws IOException { cbm.write(b); }
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
    public String readLine() throws IOException { return cbm.readLine(); }
    public String readLine(int maxLength) throws IOException { return cbm.readLine(maxLength); }
    public String readPassword() throws Exception { return cbm.readPassword(); }
    public int readKey() throws IOException { return cbm.readKey(); }
    public void resetInput() throws IOException { cbm.resetInput(); }
    public void writeRawFile(String filename) throws Exception { cbm.writeRawFile(filename); }
    public byte[] readBinaryFile(String filename) throws Exception { return cbm.readBinaryFile(filename); }
    public List<String> readTextFile(String filename) throws Exception { return cbm.readTextFile(filename); }
    public void gotoXY(int x, int y) {
        write(HOME);
        for (int i=0; i<x; ++i) write(RIGHT);
        for (int i=0; i<y; ++i) write(DOWN);
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
        StringBuilder sb = new StringBuilder();
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), UTF_8));
            String line;
            while ((line = br.readLine()) != null) sb.append(line + "\n");
            br.close();
            conn.disconnect();
            return sb.toString();
        } else {
            System.err.println(conn.getResponseMessage());
        }
        conn.disconnect();
        return null;
    }

    public static Map<Long, PetsciiThread> getClients() {
        return clients;
    }

    public Object getCustomObject() { return customObject; }
    public void setCustomObject(Object obj) { this.customObject = obj; }
}
