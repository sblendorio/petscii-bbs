package eu.sblendorio.bbs.tenants;

import eu.sblendorio.bbs.core.PetsciiThread;

import static eu.sblendorio.bbs.core.Keys.*;
import static eu.sblendorio.bbs.core.Colors.*;

public class MenuRetroAcademy extends PetsciiThread {

    @Override
    public void doLoop() throws Exception {
        while (true) {
            int delta = 1;
            write(CLR, LOWERCASE, CASE_LOCK);
            log("Starting MenuRetroAcademy BBS / main menu");
            logo();

            gotoXY(5, delta + 4); write(WHITE); print("Blog / News"); write(GREY3);
            gotoXY(5, delta + 6); write(REVON); print(" 1 "); write(REVOFF); print(" Retroacademy");
            gotoXY(5, delta + 7); write(REVON); print(" 2 "); write(REVOFF); print(" Disinformatico");
            gotoXY(5, delta + 8); write(REVON); print(" 3 "); write(REVOFF); print(" MedBunker");
            gotoXY(5, delta + 9); write(REVON); print(" 4 "); write(REVOFF); print(" Dottore, e' vero che...");
            gotoXY(5, delta + 10); write(REVON); print(" 5 "); write(REVOFF); print(" David Puente");
            gotoXY(5, delta + 11); write(REVON); print(" 6 "); write(REVOFF); print(" Open Online");

            gotoXY(24, delta + 11); write(WHITE); print("Games"); write(GREY3);
            gotoXY(24, delta + 13); write(REVON); print(" 7 "); write(REVOFF); print(" TIC-TAC-TOE");
            gotoXY(24, delta + 14); write(REVON); print(" 8 "); write(REVOFF); print(" CONNECT-4");

            gotoXY(6, delta + 14); write(WHITE); print("Misc"); write(GREY3);
            gotoXY(6, delta + 16); write(REVON); print(" 9 "); write(REVOFF); print(" Sportal.IT");
            gotoXY(6, delta + 17); write(REVON); print(" 0 "); write(REVOFF); print(" Impariamo a conoscere le ossa");
            gotoXY(6, delta + 18); write(REVON); print(" P "); write(REVOFF); print(" PETSCII Art Gallery");
            gotoXY(6, delta + 19); write(REVON); print(" . "); write(REVOFF); print(" Logoff");

            gotoXY(26, delta + 4); write(WHITE); print("Servizi"); write(GREY3);
            gotoXY(26, delta + 6); write(REVON); print(" M "); write(REVOFF); print(" Messaggi");
            gotoXY(26, delta + 7); write(REVON); print(" T "); write(REVOFF); print(" Televideo");

            gotoXY(4, 23); write(GREY3); print("Copyright (C) 2018 Retroacademy ");

            flush();
            boolean validKey;
            do {
                validKey = true;
                log("Menu. Waiting for key pressed.");
                resetInput(); int key = readKey();
                key = Character.toLowerCase(key);
                log("Menu. Pressed: '" + (key == 13 || key == 10 ? "chr("+key+")" : ((char) key)) + "' (code=" + key + ")");
                if (key == '.') {
                    newline();
                    newline();
                    println("Disconnected.");
                    return;
                }
                    else if (key == '1') launch(new RetroAcademy());
                    else if (key == '2') launch(new Disinformatico());
                    else if (key == '3') launch(new Medbunker());
                    else if (key == '4') launch(new DottoreMaEVeroChe());
                    else if (key == '5') launch(new DavidPuenteBlog());
                    else if (key == '6') launch(new OpenOnline());
                    else if (key == '7') launch(new TicTacToe());
                    else if (key == '9') launch(new Sportal());
                    else if (key == '8') launch(new ConnectFour());
                    else if (key == '0') launch(new Ossa());
                    else if (key == 'p') launch(new PetsciiArtGallery());
                    else if (key == 'm') launch(new UserLogon());
                    else if (key == 't') launch(new TelevideoRai());
                    else validKey = false;
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

}