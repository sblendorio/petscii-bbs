package eu.sblendorio.bbs.tenants.mixed;

import eu.sblendorio.bbs.core.AsciiThread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TcpProxy extends AsciiThread {

    private String targetHost;
    private int targetPort;

    public TcpProxy(String host, int port) {
        super();
        targetHost = host;
        targetPort = port;
    }

    @Override
    public void doLoop() throws Exception {
        Socket targetSocket = new Socket(targetHost, targetPort);

        Thread thread1 = new Thread(() -> forwardData(socket, targetSocket));
        Thread thread2 = new Thread(() -> forwardData(targetSocket, socket));

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        targetSocket.close();
    }

    private static void forwardData(Socket socketInput, Socket socketOutput) {
        byte[] buffer = new byte[4096];
        int bytesRead;

        try (InputStream input = socketInput.getInputStream();
             OutputStream output = socketOutput.getOutputStream()) {

            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
                output.flush();
            }
        } catch (IOException e) {
            System.err.println("Forwarding error");
        }
    }

}
