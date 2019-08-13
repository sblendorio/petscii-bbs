package eu.sblendorio.bbs.core;

import java.io.*;
import java.util.Arrays;

/**
 * a tiny version of Ward Christensen's MODEM program for UNIX.
 * Written ~ 1980 by Andrew Scott Beals. Last revised 1982.
 * A.D. 2000 - dragged from the archives for use in Java Cookbook.
 * A.D. 2019 - adapted and fixed by Francesco Sblendorio
 *
 * @author C version by Andrew Scott Beals, sjobrg.andy%mit-oz@mit-mc.arpa.
 * @author Java version by Ian F. Darwin, ian@darwinsys.com
 * @author fixed Java version by Francesco Sblendorio, sblendorio@gmail.com
 * $Id: TModem.java,v 1.8 2000/03/02 03:40:50 ian Exp $
 *
 * Sample of usage in PETSCII BBS Builder:
 *
 * <pre>
 * import eu.sblendorio.bbs.core.*;
 * import java.nio.file.*;
 *
 * public class TestModem extends PetsciiThread {
 *
 *     &#64;Override
 *     public void doLoop() throws Exception {
 *         println("Press any key");
 *         readKey();
 *
 *         Path path = Paths.get("/tmp/tank-64.prg");
 *         byte[] bytes = Files.readAllBytes(path);
 *         XModem t = new XModem(this.cbm, this.cbm.out());
 *         t.send(bytes);
 *     }
 * }
 * </pre>
 */
public class XModem {

    protected final byte CPMEOF = 26;       /* control/z */
    protected final int MAXERRORS = 10;     /* max times to retry one block */
    protected final int SECSIZE = 128;      /* cpm sector, transmission block */
    protected final int SENTIMOUT = 30;     /* timeout time in send */

    /* Protocol characters used */
    protected final byte SOH = 1;    /* Start Of Header */
    protected final byte EOT = 4;    /* End Of Transmission */
    protected final byte ACK = 6;    /* ACKnowlege */
    protected final byte CAN = 24;   /* CANcel */
    protected final byte NAK = 0x15; /* Negative AcKnowlege */

    protected Reader inStream;
    protected PrintStream outStream;

    public XModem(Reader input, PrintStream output) {
        inStream = input;
        outStream = output;
    }

    /** A flag used to communicate with inner class IOTimer */
    protected boolean gotChar;

    class IOTimer extends Thread {
        final String message;
        final long milliseconds;

        IOTimer(long sec, String message) {
            this.milliseconds = 1000 * sec;
            this.message = message;
        }

        public void run() {
            try {
                Thread.sleep(milliseconds);
            } catch (InterruptedException e) {} // can't happen
            if (!gotChar) {
                log("Timed out waiting for " + message);
                die(1);
            }
        }
    }

    public boolean send(byte[] inputByteArray) throws IOException, InterruptedException
    {
        char checksum, index, blocknumber, errorcount;
        byte character;
        byte[] sector = new byte[SECSIZE];
        int nbytes;

        try (DataInputStream inputData = new DataInputStream(new ByteArrayInputStream(inputByteArray))) {
            errorcount = 0;
            blocknumber = 1;

            // The C version uses "alarm()", a UNIX-only system call,
            // to detect if the read times out. Here we do detect it
            // by using a Thread, the IOTimer class defined above.
            gotChar = false;
            new IOTimer(SENTIMOUT, "NAK to start send").start();

            do {
                character = getchar();
                gotChar = true;
                if (character != NAK && errorcount < MAXERRORS)
                    ++errorcount;
            } while (character != NAK && errorcount < MAXERRORS);

            log("Transmission beginning");
            if (errorcount == MAXERRORS) xerror();

            Arrays.fill(sector, CPMEOF);
            while ((nbytes = inputData.read(sector)) > 0) {
                if (nbytes < SECSIZE)
                    sector[nbytes] = CPMEOF;
                errorcount = 0;
                while (errorcount < MAXERRORS) {
                    putchar(SOH);   /* here is our header */
                    putchar(blocknumber);   /* the block number */
                    putchar(~blocknumber);  /* & its complement */
                    checksum = 0;
                    for (index = 0; index < SECSIZE; index++) {
                        putchar(sector[index]);
                        checksum += sector[index];
                    }
                    putchar(checksum);
                    character = getchar();
                    if (character == CAN)
                        throw new CancelTransferException();
                    else if (character != ACK)
                        ++errorcount;
                    else
                        break;
                }
                if (errorcount == MAXERRORS)
                    xerror();
                ++blocknumber;

                Arrays.fill(sector, CPMEOF);
            }
            log("Out of cycle");
            putchar(EOT);
            final boolean isAck = getchar() == ACK;
            if (!isAck) log("Transmission interrupted after EOT, missing ACK");
            log("Transmission complete.");
        } catch (CancelTransferException e) {
            log("BREAK: Transmission canceled");
        }
        return true;
    }

    private byte getchar() throws IOException {
        return (byte)inStream.read();
    }

    private void putchar(int c) throws IOException {
        outStream.write(c);
        outStream.flush();
    }

    private void xerror() {
        System.err.println("too many errors... aborting");
        die(1);
    }

    private void die(int how) {
        System.err.println("Error code " + how);
        throw new RuntimeException(new CbmIOException("Too many errors during XModem transfer: " + how));
    }

    private void log(String message) {
        System.err.println(message);
    }

    private static class CancelTransferException extends RuntimeException {}
}