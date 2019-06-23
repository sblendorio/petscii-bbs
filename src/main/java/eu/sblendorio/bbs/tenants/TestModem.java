package eu.sblendorio.bbs.tenants;

import eu.sblendorio.bbs.core.*;
import java.nio.file.*;

public class TestModem extends PetsciiThread {

    @Override
    public void doLoop() throws Exception {
        println("Press any key");
        readKey();

        Path path = Paths.get("/tmp/tank-64.prg");
        byte[] bytes = Files.readAllBytes(path);
        XModem t = new XModem(this.cbm, this.cbm.out());
        t.send(bytes);
    }
}
