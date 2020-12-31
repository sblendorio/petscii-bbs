package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TestSendAscii extends AsciiThread {

    @Override
    public void doLoop() throws Exception {
        Thread.sleep(3000);
        resetInput();
        println("Press any key");
        readKey(); resetInput();
        Thread.sleep(20000);
        log("Start download. calculating size.");
        byte[] bytes = downloadFile(new URL("https://raw.githubusercontent.com/sblendorio/hanoi-m10/master/hanoi.ba"));
        log("Size = " + bytes.length);

        //Path path = Paths.get("/tmp/x1");
        //XModem t = new XModem(this.io, this.io.out());
        //t.send(Files.readAllBytes(path));
        log("Started");
        //t.send(bytes);
        //write("10 PRINT 3*2\r\n20 PRINT \"BRAVO\"\r\n".getBytes(StandardCharsets.ISO_8859_1));
        write(bytes);
        write(26);
        println();
        println("Download finished");
        println("Press a key to exit");
        resetInput();
        readKey();
        log("Ended");
    }
}
