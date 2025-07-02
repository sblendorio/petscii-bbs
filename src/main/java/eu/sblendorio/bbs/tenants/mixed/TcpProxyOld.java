package eu.sblendorio.bbs.tenants.mixed;

import eu.sblendorio.bbs.core.AsciiThread;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class TcpProxyOld extends AsciiThread {

    private String targetHost;
    private int targetPort;

    public TcpProxyOld(String host, int port) {
        super();
        targetHost = host;
        targetPort = port;
    }

    @Override
    public void doLoop() throws Exception {
        Socket targetSocket = new Socket(targetHost, targetPort);

        InputStreamReader inSource = new InputStreamReader(socket.getInputStream(), "ISO-8859-1");
        OutputStreamWriter outTarget = new OutputStreamWriter(targetSocket.getOutputStream(), "ISO-8859-1");

        InputStreamReader inTarget = new InputStreamReader(targetSocket.getInputStream(), "ISO-8859-1");
        OutputStreamWriter outSource = new OutputStreamWriter(socket.getOutputStream(), "ISO-8859-1");

        while (true) {
            while (inSource.ready()) outTarget.write(inSource.read());
            outTarget.flush();

            while (inTarget.ready()) outSource.write(inTarget.read());
            outSource.flush();
        }
    }
}
