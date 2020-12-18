package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.Utils;
import java.nio.charset.StandardCharsets;

public class TempA1 extends AsciiThread {

    public TempA1() {
        super();
    }

    @Override
    public void doLoop() throws Exception {
        println("Hello world! ");
        write(Utils.toBytes(27, "(0", 0x6c,0x71,0x6b,13,10,0x78,32,0x78,13,10,0x6d,0x71,0x6a,13,10,13,10,27,"(B"));
        resetInput();
        readKey();
        println("Consciously closing the connection.");
    }


    @Override
    public byte[] initializingBytes() {
        // return new byte[] {(byte)255, (byte)253, 34,  /* IAC DO LINEMODE */
        //     (byte)255,(byte) 250, 34, 1, 0, (byte)255, (byte)240, /* IAC SB LINEMODE MODE 0 IAC SE */
        //     (byte)255, (byte)253, 1    /* IAC WILL ECHO */};
        //return Utils.toBytes(255, 251,24);
        //return Utils.toBytes(255,250,42,1,59,85,84,70,45,56,59,73,83,79,45,56,56,53,57,45,49,255,240);
        return null;
    }


    /*

    https://stackoverflow.com/questions/34758488/how-to-guess-telnet-client-encoding-with-ansi-escape-codes

       IAC  = 255 = \377 https://tools.ietf.org/html/rfc854#page-14
https://tools.ietf.org/html/rfc884
https://users.cs.cf.ac.uk/Dave.Marshall/Internet/node141.html

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
      SB 250 = \372
    TERMINAL-TYPE 24 = \030
    SEND = 1
    SE = 240 \360

        IAC SB TERMINAL-TYPE SEND IAC SE


     */

}
