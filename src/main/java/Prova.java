import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.Utils;

public class Prova extends AsciiThread {


    @Override
    public void doLoop() throws Exception {
        //write(Utils.bytes(12, dest));
        // write(12,   27, 0x57,      65,10,66,10,67,11,68,11,69);
        //write(31, 54, 54, 65, 66, 67);
        write(0x0c,
                0x1b, 0x22, 0x1b, 0x46,
                0x1b, 0x57,
                0x09, 0x09, 0x0a, 0x0a, 0x0a,
                0x41, 0x42, 0x43);
        for (int i=0; i<40; i++)
            println();
        print("ciao batman");
        flush();
    }
}
