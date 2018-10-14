package eu.sblendorio.bbs.tenants;

import eu.sblendorio.bbs.core.PetsciiThread;

import static eu.sblendorio.bbs.core.Keys.*;
import static eu.sblendorio.bbs.core.Colors.*;

public class MenuRetroAcademy extends PetsciiThread {

    @Override
    public void doLoop() throws Exception {
        while (true) {
            write(CLR, LOWERCASE, CASE_LOCK);
            log("Starting MenuRetroAcademy BBS / main menu");
            logo();
            write(GREY3);
            gotoXY(4,23); print("Copyright (C) 2018 Retroacademy");
            gotoXY(9,5); write(WHITE); print("Make your choice:"); write(GREY3);
            gotoXY(9, 9); write(REVON); print(" 1 "); write(REVOFF); print("  Explore retroacademy.it");
            gotoXY(9, 11); write(REVON); print(" 2 "); write(REVOFF); print("  Explore vcfed.org");
            gotoXY(9,13); write(REVON); print(" 3 "); write(REVOFF); print("  Game: TIC-TAC-TOE");
            gotoXY(9,15); write(REVON); print(" 4 "); write(REVOFF); print("  Game: CONNECT-4");
            gotoXY(9,17); write(REVON); print(" 5 "); write(REVOFF); print("  Logoff ");
            flush();
            boolean validKey;
            do {
                validKey = true;
                log("Menu. Waiting for key pressed.");
                resetInput(); int key = readKey();
                log("Menu. Pressed: "+key);
                if (key == '5' || key == '.') {
                    return;
                } else if (key == '1') {
                    launch(new RetroAcademy());
                } else if (key == '2') {
                    launch(new Vcfed());
                } else if (key == '3') {
                    launch(new TicTacToe());
                } else if (key == '4') {
                    launch(new ConnectFour());
                } else {
                    validKey = false;
                }
            } while (!validKey);
        }
    }

    public void logo() throws Exception {
        write(new byte[]{
            32,  32,  32,  32,  32,  28, -84,  32,  32,  32,  32,  32,  32,  32,  32,  32,
            32,  32,  32,  32,  32,-104, -69,  32,  32,  32,  32,  32,  32,  32,  32,  32,
            32,  32,  32,-101, -69, -84,  32, -84, -84,  13,  18,  28, -95, -65,-110, -84,
            18, -69,-110, -69,  18, -69,-110, -66,  18, -68,-110, -66,  18, -65,-110, -65,
            -104, -84,  18, -94,-110, -95,  18, -65,-110, -66,  18, -65, -69,-110, -84,  18,
            -94,-110, -95,  18, -65, -68, -95, -69,-110, -65,  18, -95,-110,  32, -95,  32,
            32,  32,-101, -68,  18, -65, -95,-110, -69, -84,  18, -95,-110, -66,  13,  18,
            28, -95,-110,  32, -68,  18, -68,-110,  32, -68, -69, -95,  32, -65,  18, -65,
            -110,-104, -68, -94, -95, -65, -69, -65,  18, -66,-110, -68, -94, -95,  18, -69,
            -110, -69,  18, -95, -95, -95,-110, -68, -94, -95,  30, -94, -94,  32,-101, -68,
            18, -65, -95, -65, -95,-110, -68, -69,  13,  32,  32,  32,  32,  32,  32,  32,
            32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,
            32,  32,  32,  32,-104, -94, -66,  13,  18,-102, -95, -84, -69,-110, -69,  13,
            18, -95, -68, -66,-110, -66,  13,  18, -95,-110, -95,  18, -95,-110, -95,  13,
            -68,  18, -94, -94,-110,  13,  18,-103, -95, -84, -69,-110, -69,  13,  18, -95,
            -68, -66,-110, -66,  13,  18, -95,-110, -95,  18, -95,-110, -95,  13, -68,  18,
            -94, -94,-110,  13,-106, -84,  18, -84, -69,-110, -69,  13, -68,  18, -68,-110,
            -94,  13, -84, -69,  18, -95,-110, -95,  13,  32,  18, -94, -94,-110,  13
        });
    }

    @Override
    public void receive(long sender, Object message) {
        log("--------------------------------");
        log("From "+getClients().get(sender).getClientName()+": " +message);
        log("--------------------------------");
        println();
        println("--------------------------------");
        println("From "+getClients().get(sender).getClientName()+": " +message);
        println("--------------------------------");
    }

}