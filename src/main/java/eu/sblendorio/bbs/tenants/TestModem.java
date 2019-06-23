package eu.sblendorio.bbs.tenants;

import eu.sblendorio.bbs.core.*;

import java.net.URL;
import java.nio.file.*;

public class TestModem extends PetsciiThread {

    @Override
    public void doLoop() throws Exception {
        println("Press any key");
        readKey();
        log("Start download. calculating size.");
        byte[] bytes = downloadFile(new URL("https://server.com/file.prg"));
        log("Size = " + bytes.length);

        Path path = Paths.get("/tmp/x1");
        XModem t = new XModem(this.cbm, this.cbm.out());
        //t.send(Files.readAllBytes(path));
        t.send(bytes);
    }
}
