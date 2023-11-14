package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.BbsInputOutput;
import eu.sblendorio.bbs.core.MinitelInputOutput;

import static org.apache.commons.lang3.math.NumberUtils.toInt;

public class WikipediaAscii extends AsciiThread {
    private BbsInputOutput interfaceType = null;
    private byte[] mainLogo = null;
    private byte[] headLogo = null;

    @Override
    public void doLoop() throws Exception {
        mainLogo=readBinaryFile("minitel/wikipedia-title.vdt");
        cls();
        if (mainLogo == null) {
            println("Wikipedia - BBS version");
            println("-----------------------");
            println();
            println("1. Search");
            println("2. I feel lucky");
            println("Type '.' to go back");
            println();
            print(">");
        } else {
            write(mainLogo);
        }
        flush(); resetInput();
        int ch;
        do {
            ch = readKey();
        } while (ch != '1' && ch != '2' && ch != '.');
        if (ch == '.') return;

        String keywords;

       // if (interfaceType instanceof MinitelInputOutput) {
            if (ch == '1') {
                write(0x1f, 0x40+8, 0x40+8);
                write(0x1b, 0x51, 0x1b, 0x47);
                print(" 1. Search               ");
            } else if (ch == '2') {
                write(0x1f, 0x40+10, 0x40+8);
                write(0x1b, 0x51, 0x1b, 0x47);
                print(" 2. I feel lucky         ");
            }

       // } else {
       //     println(String.valueOf(ch-48));
       //     println();
       //     print("Keywords> ");
       //     flush(); resetInput();
       //     keywords = readLine();
       // }

    }
}
