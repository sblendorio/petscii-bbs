package eu.sblendorio.bbs.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Arrays;
import java.util.Optional;

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
 *         XModem t = new XModem(this);
 *         t.send(bytes);
 *     }
 * }
 * </pre>
 */
public class XModem {

    protected static final byte CPMEOF = 26;       /* control/z */
    protected static final int MAXERRORS = 10;     /* max times to retry one block */
    protected static final int SECSIZE = 128;      /* cpm sector, transmission block */
    protected static final int SENTIMOUT = 30;     /* timeout time in send */

    /* Protocol characters used */
    protected static final byte SOH = 1;    /* Start Of Header */
    protected static final byte EOT = 4;    /* End Of Transmission */
    protected static final byte ACK = 6;    /* ACKnowlege */
    protected static final byte CAN = 24;   /* CANcel */
    protected static final byte NAK = 0x15; /* Negative AcKnowlege */

    protected Reader inStream;
    protected PrintStream outStream;
    protected BbsThread bbsThread;

    public XModem(Reader input, PrintStream output) {
        inStream = input;
        outStream = output;
        bbsThread = null;
    }

    public XModem(BbsThread thread) {
        bbsThread = thread;
        inStream = thread.io;
        outStream = thread.io.out();
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

        @Override
        public void run() {
            try {
                Thread.sleep(milliseconds);
            } catch (InterruptedException e) {
                // RESTORE THE INTERRUPTED STATUS
                Thread.currentThread().interrupt();
            } // can't happen
            if (!gotChar) {
                log("Timed out waiting for " + message);
                die(1);
            }
        }
    }

    public boolean send(byte[] inputByteArray) throws IOException
    {
        char checksum;
        char index;
        char blocknumber;
        char errorcount;
        byte character;
        byte[] sector = new byte[SECSIZE];
        int nbytes;

        Optional<BbsThread> thread = Optional.ofNullable(bbsThread);

        boolean quoteMode = thread.map(BbsThread::quoteMode).orElse(false);
        boolean keepAlive = thread.map(p -> p.keepAlive).orElse(false);

        try (DataInputStream inputData = new DataInputStream(new ByteArrayInputStream(inputByteArray))) {
            thread.ifPresent(t -> t.updateKeepAlive(false));

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
                if (character != NAK) {
                    ++errorcount;
                }
            } while (character != NAK && errorcount < MAXERRORS);

            log("Transmission beginning");
            if (errorcount == MAXERRORS) {
                xerror();
            }

            Arrays.fill(sector, CPMEOF);
            while ((nbytes = inputData.read(sector)) > 0) {
                if (nbytes < SECSIZE) {
                    sector[nbytes] = CPMEOF;
                }
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
        } finally {
            thread.ifPresent(t -> t.setQuoteMode(quoteMode));
            thread.ifPresent(t -> t.updateKeepAlive(keepAlive));
        }
        return true;
    }

    private byte getchar() throws IOException {
        return (byte)inStream.read();
    }

    private void putchar(int c) {
        outStream.write(c);
        outStream.flush();
    }

    private void xerror() {
        log("XModem too many errors... aborting");
        die(1);
    }

    private void die(int how) {
        log("XModem Error Code " + how);
        throw new UncheckedIOException(new BbsIOException("Too many errors during XModem transfer: " + how));
    }

    private void log(String message) {
        log(message);
    }

    private static class CancelTransferException extends RuntimeException {}
}
