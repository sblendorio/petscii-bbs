package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.games.BasicIde;

import java.util.Map;
import java.util.TreeMap;

public class BasicIdeAscii extends AsciiThread {
    private Map<Long, String> program = new TreeMap<>();

    @Override
    public void doLoop() throws Exception {
        cls();
        println("*** RETROCAMPUS BBS BASIC 1.0 ***");
        BasicIde.execute(this, program);
    }

}
