package eu.sblendorio.bbs.tenants;

import eu.sblendorio.bbs.core.PetsciiThread;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static eu.sblendorio.bbs.core.Keys.*;
import static eu.sblendorio.bbs.core.Colors.*;

public class Ossa extends PetsciiThread {

    public static String[] bodyParts = {
            "ossa/cranio_draw",
            "ossa/tronco_draw",
            "ossa/braccio_draw",
            "ossa/gamba_draw",
    };

    @Override
    public void doLoop() throws Exception {
        write(WHITE);
        int choice = 0;
        do {
            displayMainMenu();
            do {
                resetInput();
                choice = readKey();
            } while ("12.".indexOf(choice) == -1);
            if (choice == '1') startLessons();
        } while (choice != '2' && choice != '.');
        write(CLR, LOWERCASE);
    }

    public void displayMainMenu() throws Exception {
        write(CLR, UPPERCASE, CASE_LOCK);
        gotoXY(4,25); print("(c) 1985 mantrasoft, diego & e.t.a.");
        write(HOME, RETURN, RETURN);
        for (int i=1; i<=39; ++i) write(191);
        write(RETURN, RETURN);
        println("impariamo a conoscere il corpo umano:");
        newline();
        println("1. le ossa.");
        newline();
        for (int i=1; i<=39; ++i) write(191);
        write(RETURN, RETURN, RETURN, RETURN);
        println("   desideri :");
        write(32, 32, 32, 197, 197, 197, 197, 197, 197, 197, 197, 197, 197, RETURN, RETURN);
        println("1. spiegazione."); newline();
        println("2. fine."); newline();
        newline();
        print("   scegli: ");
        flush();
    }

    public void startLessons() throws Exception {
        int choice = 0;
        do {
            displayMenuLessons();
            do {
                resetInput();
                choice = readKey();
            } while ("12345.".indexOf(choice) == -1);
            switch (choice) {
                case '1': displayBodyPart("cranio"); break;
                case '2': displayBodyPart("tronco"); break;
                case '3': displayBodyPart("braccio"); break;
                case '4': displayBodyPart("gamba"); break;
            }
        } while (choice != '5' && choice != '.');
    }

    public void displayMenuLessons() throws Exception {
        cls();
        println("spiegazione.");
        newline();
        for (int i=1; i<=39; ++i) write(191);
        write(RETURN, RETURN, RETURN);
        println("1.  cranio."); newline(); newline();
        println("2.  tronco."); newline(); newline();
        println("3.  braccio."); newline(); newline();
        println("4.  gamba."); newline(); newline();
        println("5.  fine spiegazione"); newline(); newline(); newline();
        print("  scegli: ");
        flush();
    }

    public void displayBodyPart(String name) throws Exception {
        int key;
        cls();
        writeRawFile("ossa/" + name + "_draw");
        sleep(2500);
        write(HOME);
        writeFileWithDelay("ossa/" +name + "_info", 90);
        gotoXY(24,23); write(REVON); print("   premi  c   "); write(REVOFF);
        gotoXY(24,24); write(REVON); print("per continuare"); write(REVOFF);
        flush();
        do {
            resetInput();
            key=readKey();
        } while ("cC.".indexOf(key)==-1);
    }

    public void writeFileWithDelay(String filename, long delayInMillis) throws Exception {
        byte[] bytes = readBinaryFile(filename);
        for (byte b: bytes) {
            write(b);
            if (delayInMillis != 0 && b != 13 && b != 29 && b != 19) {
                flush();
                sleep(delayInMillis);
            }
        }
        flush();
    }

}
