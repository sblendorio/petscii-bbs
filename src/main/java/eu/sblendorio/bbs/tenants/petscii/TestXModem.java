package eu.sblendorio.bbs.tenants.petscii;

import eu.sblendorio.bbs.core.*;

import eu.sblendorio.bbs.core.PetsciiThread;
import java.net.URL;
import java.nio.file.*;

public class TestXModem extends PetsciiThread {

    @Override
    public void doLoop() throws Exception {
        println("Press any key");
        readKey();
        log("Start download. calculating size.");
        byte[] bytes = downloadFile(new URL("https://server.com/file.prg"));
        log("Size = " + bytes.length);

        Path path = Paths.get("/tmp/x1");
        XModem t = new XModem(this.io, this.io.out());
        //t.send(Files.readAllBytes(path));
        t.send(bytes);
    }
}
