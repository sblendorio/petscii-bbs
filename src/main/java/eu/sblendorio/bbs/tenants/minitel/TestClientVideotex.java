package eu.sblendorio.bbs.tenants.minitel;

import eu.sblendorio.bbs.core.MinitelThread;
import eu.sblendorio.bbs.core.Utils;

public class TestClientVideotex extends MinitelThread {
    @Override
    public void doLoop() throws Exception {
        cls();
        println("DRCS="+drcsEnabled());
        flush(); resetInput();
        write(0x1b, 0x39, 0x7b);
        flush();
        Thread.sleep(1000L);
        byte[] r = resetInput();
        print("Result = ");
        println(Utils.hex(r));
        println("len="+r.length);
        println("-----------------");
        log(Utils.hex(r));
        log("len="+r.length);
        log("END");
        readKey();
        // FRENCH MINITEL 1B: 1,67,117,60,4 = 0x01, 0x43, 0x75, 0x3c, 0x04
    }

    public static void main(String args[]) {
        byte[] bytes = new byte[] {0x01, 0x43, 0x75, 0x3c, 0x04};
        System.out.println(Utils.hex(bytes));
    }
}
