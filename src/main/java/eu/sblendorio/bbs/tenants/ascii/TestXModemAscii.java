package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.PetsciiThread;
import eu.sblendorio.bbs.core.XModem;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestXModemAscii extends PetsciiThread {

    @Override
    public void doLoop() throws Exception {
        Thread.sleep(2000);
        println("Press any key");
        resetInput();
        readKey();
        log("Start download. calculating size.");
        byte[] bytes = downloadFile(new URL("https://raw.githubusercontent.com/sblendorio/hanoi-m10/master/hanoi.ba"));
        log("Size = " + bytes.length);

        //Path path = Paths.get("/tmp/x1");
        XModem t = new XModem(this.io, this.io.out());
        //t.send(Files.readAllBytes(path));
        log("Started");
        t.send(bytes);
        log("Ended");
    }
}
