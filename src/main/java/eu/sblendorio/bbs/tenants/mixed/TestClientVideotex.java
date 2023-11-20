package eu.sblendorio.bbs.tenants.mixed;

import eu.sblendorio.bbs.core.MinitelThread;
import eu.sblendorio.bbs.core.Utils;

public class TestClientVideotex extends MinitelThread {
    @Override
    public void doLoop() throws Exception {
        cls();
        System.out.println("START");
        write(0x1b, 0x39, 0x7b);
        Thread.sleep(1000L);
        int ch = 255;
        byte[] r = resetInput();
        cls();
        print("Result = ");
        println(Utils.hex(r));
        println("-----------------");
        System.out.println(Utils.hex(r));
        System.out.println("END");
        readKey();
        // FRENCH MINITEL 1B: 1,67,117,60,4 = 0x01, 0x43, 0x75, 0x3c, 0x04
    }

    public static void main(String args[]) {
        byte[] bytes = new byte[] {0x01, 0x43, 0x75, 0x3c, 0x04};
        System.out.println(Utils.hex(bytes));
    }
}
