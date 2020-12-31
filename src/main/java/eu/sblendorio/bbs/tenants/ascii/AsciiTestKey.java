package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import java.nio.charset.StandardCharsets;

public class AsciiTestKey extends AsciiThread {

    public AsciiTestKey() {
        super();
    }

    @Override
    public void doLoop() throws Exception {
        newline();
        newline();
        int key;
        do {
            print("Command> ");
            flush(); key = readKey();
            println("You wrote: "+key);
            System.out.println("#"+clientId+" wrote: "+key);
        } while (key != 46);
    }


    @Override
    public byte[] initializingBytes() {
       // return new byte[] {(byte)255, (byte)253, 34,  /* IAC DO LINEMODE */
       //     (byte)255,(byte) 250, 34, 1, 0, (byte)255, (byte)240, /* IAC SB LINEMODE MODE 0 IAC SE */
       //     (byte)255, (byte)253, 1    /* IAC WILL ECHO */};

       return
           ("\377\375\042"      // LINEMODE
           +"\377\376\020"      // LINEFEED
          // +"\377\375\055"
           )
           .getBytes(StandardCharsets.ISO_8859_1);
    }

    /*
       IAC  = 255 = \377 https://tools.ietf.org/html/rfc854#page-14

      WILL (option code)  251    Indicates the desire to begin
                         \373    performing, or confirmation that
                                 you are now performing, the
                                 indicated option.
      WON'T (option code) 252    Indicates the refusal to perform,
                         \374    or continue performing, the
                                 indicated option.
      DO (option code)    253    Indicates the request that the
                         \375    other party perform, or
                                 confirmation that you are expecting
                                 the other party to perform, the
                                 indicated option.
      DON'T (option code) 254    Indicates the demand that the
                         \376    other party stop performing,
                                 or confirmation that you are no
                                 longer expecting the other party
                                 to perform, the indicated option.
      IAC                 255    Data Byte 255.
     */

}
