import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.Utils;

public class Prova extends AsciiThread {


    @Override
    public void doLoop() throws Exception {
        byte[] bytes = readBinaryFile("prestel/topolino.seq");
        int n = 121;
        byte[] dest = new byte[bytes.length - n];
        System.arraycopy(bytes, n, dest, 0, bytes.length - n);
        System.out.println("N="+n);
        write(Utils.bytes(12, dest));
        // write(12, 97, 98, 99);
        flush();
    }
}
