package eu.sblendorio.bbs.tenants.mixed;

import eu.sblendorio.bbs.core.AsciiThread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TcpProxy extends AsciiThread {

    private String targetHost;
    private int targetPort;
    private String stopString = null;

    private boolean stop = false;

    public TcpProxy(String host, int port) {
        super();
        targetHost = host;
        targetPort = port;
    }

    public TcpProxy(String host, int port, String stopStr) {
        this(host, port);
        stopString = stopStr;
    }

    @Override
    public void doLoop() throws Exception {
        Socket targetSocket = new Socket(targetHost, targetPort);

        Thread thread1 = new Thread(() -> forwardData(socket, targetSocket, null, false, true));
        Thread thread2 = new Thread(() -> forwardData(targetSocket, socket, stopString, true, false));

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (Exception e) {
            e.printStackTrace();
            targetSocket.close();
        }
    }

    private void forwardData(Socket socketInput, Socket socketOutput, String exitString, boolean closeIn, boolean closeOut) {
        byte[] buffer = new byte[4096];
        int bytesRead;

        InputStream input = null;
        OutputStream output = null;

        try {
            input = socketInput.getInputStream();
            output = socketOutput.getOutputStream();

            while (!stop && (bytesRead = input.read(buffer)) != -1) {
                String segment = new String(buffer, "ISO-8859-1");
                if (exitString != null && segment.contains(exitString)) {
                    stop = true;
                }

                output.write(buffer, 0, bytesRead);
                output.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Forwarding error");
        } finally {
            try {
                if (closeIn) input.close();
                if (closeOut) output.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
