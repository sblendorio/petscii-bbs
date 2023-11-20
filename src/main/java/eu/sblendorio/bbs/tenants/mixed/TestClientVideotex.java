package eu.sblendorio.bbs.tenants.mixed;

import eu.sblendorio.bbs.core.MinitelThread;

public class TestClientVideotex extends MinitelThread {
    @Override
    public void doLoop() throws Exception {
        System.out.println("START");
        write(0x1b, 0x39, 0x7b);
        Thread.sleep(1000L);
        int ch = -200;
        byte[] r = io.resetInput();
        if (r != null) {
            for (byte b: r) {
                System.out.print(Integer.toHexString(b)+" ");
                System.out.println(b);
            }
        }
        System.out.println("FINE");
        System.exit(0);

        if (io.ready()) ch = readKey();
        System.out.print(Integer.toHexString(ch)+" ");
        System.out.println(ch);
        if (io.ready()) ch = readKey();
        System.out.print(Integer.toHexString(ch)+" ");
        System.out.println(ch);
        if (io.ready()) ch = readKey();
        System.out.print(Integer.toHexString(ch)+" ");
        System.out.println(ch);
        if (io.ready()) ch = readKey();
        System.out.print(Integer.toHexString(ch)+" ");
        System.out.println(ch);
        if (io.ready()) ch = readKey();
        System.out.print(Integer.toHexString(ch)+" ");
        System.out.println(ch);

        System.out.println("END");
        // FRENCH MINITEL 1B: 1,67,117,60,4 = 0x01, 0x43, 0x75, 0x3c, 0x04
    }
}
