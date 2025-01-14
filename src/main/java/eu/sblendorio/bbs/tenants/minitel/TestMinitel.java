package eu.sblendorio.bbs.tenants.minitel;

import eu.sblendorio.bbs.core.MinitelThread;

public class TestMinitel extends MinitelThread {
    @Override
    public void doLoop() throws Exception {
        cls();
        println("Prova però, perché, com'è, ü, garçon");

        println();
        println();

        write(0x19, 0x41, 'e');
        write(0x19, 0x41, 'E'); // È
        write(0x19, 0x42, 'E'); // É
        write(0x19, 0x41, 'A'); // À
        write(0x19, 0x42, 'A'); //  ___
        write(0x19, 0x41, 'O'); //  ___
        write(0x19, 0x42, 'O'); //  ___
        write(0x19, 0x41, 0x6f); //  ___
        write(0x19, 0x42, 0x6f); //  ___
        write(0x19, 0x43, 0x6f); //  ___
        write(0x19, 0x48, 0x6f); //  ___

        println();
        println();

        println("àáâäèéêëìíîïòóôöùúûüç");
        print("ÀÁÂÄÈÉÊËÌÍÎÏÒÓÔÖÙÚÛÜÇ");
        write(27,'[', 0x32, 0x4b, 0x0d);
        print("ciao");
        System.out.println("VARX="+System.getenv("VARX"));
        // write(readBinaryFile("minitel/undefined.vdt"));
    }
}
