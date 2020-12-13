package eu.sblendorio.bbs.tenants.ascii;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

public class MenuApple1Telnet extends MenuApple1 {

    public MenuApple1Telnet() {
        super();
        setLocalEcho(true);
        clsBytes = new byte[] {
            13, 10,
            13, 10,
            13, 10,
            13, 10
        };
        screenColumns = 80;
    }

    @Override
    public void initBbs() throws Exception {
        Thread.sleep(500L);
        resetInput();
    }

    @Override
    protected String banner() { return "Retrocampus BBS for UNIX Telnet - by F. Sblendorio 2020"; }

    @Override
    public void logo() throws Exception {}

    @Override
    public byte[] initializingBytes() {
       return "\377\375\042\377\373\001".getBytes(ISO_8859_1);
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
