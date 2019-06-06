package eu.sblendorio.bbs.tenants;

import eu.sblendorio.bbs.core.*;

import java.util.Random;

import static eu.sblendorio.bbs.core.Keys.*;
import static eu.sblendorio.bbs.core.Colors.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.SPACE;

public class Magic15 extends PetsciiThread {
    private Random random = new Random(System.currentTimeMillis());
    private int[] board = new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0};
    private final int MAX_SWAPS = 80;
    private final int[] ORDERED_BOARD = new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0};
    private final int[] color = new int[] {WHITE,
            GREY3, RED,   GREY3, RED,
            RED,   GREY3, RED,   GREY3,
            GREY3, RED,   GREY3, RED,
            RED,   GREY3, RED
    };
    private final static String BLANK = "     ";
    private final int K_UP = 'w';
    private final int K_LEFT = 'a';
    private final int K_DOWN = 's';
    private final int K_RIGHT = 'd';

    @Override
    public void doLoop() throws Exception {
        int moves = 0;
        cls(); write(LOWERCASE, CASE_LOCK);
        initScreen();
        int cmd;
        shuffle();
        do {
            drawBoard();
            boolean isValid;
            do {
                int ch = readKey();
                cmd = Character.toLowerCase(ch);
                isValid = move(cmd);
            } while (cmd != '.' && !isValid);
            if (isValid) {
                ++moves;
                gotoXY(35, 13); write(GREY3); print(EMPTY+moves);
            }
        } while (cmd != '.' && !isFinished());
        if (isFinished()) {
            drawBoard();
            gotoXY(28, 16); write(WHITE, REVON); print("         ");
            gotoXY(28, 17); write(WHITE, REVON); print(" YOU WON ");
            gotoXY(28, 18); write(WHITE, REVON); print("         ");
            write(REVOFF);
            gotoXY(26, 20); write(GREY3); print("Press any key");
            gotoXY(26, 21); write(GREY3); print("   to EXIT   ");
            gotoXY(33,23); write(GREY3); print("      ");
            readKey();
        }
    }

    private boolean isFinished() {
        for (int i=0; i<16; ++i) if (board[i] != ORDERED_BOARD[i]) return false;
        return true;
    }

    private void initScreen() {
        logo();
        write(HOME, YELLOW);
        for (int i = 0; i < 4; ++i) {
            if (i == 0) {
                write(176, 192, 192, 192, 192, 192);
                for (int j = 0; j < 3; ++j) write( 178, 192, 192, 192, 192, 192);
                write(174, 13);
            } else {
                write(171, 192, 192, 192, 192, 192);
                for (int j = 0; j < 3; ++j) write(219, 192, 192, 192, 192, 192);
                write(179, 13);
            }
            for (int k = 0; k < 5; ++k) {
                for (int j = 0; j < 4; ++j) write(221, 32, 32, 32, 32, 32);
                write(221, 13);
            }
        }
        write(173, 192, 192, 192, 192, 192);
        for (int j = 0; j < 3; ++j) write(177, 192, 192, 192, 192, 192);
        write(189);
        gotoXY(27,10); write(WHITE); print("Use W,A,S,D");
        gotoXY(26,11); write(WHITE); print("to move tiles");
        gotoXY(28, 13); write(CYAN); print("Moves: "); write(GREY3); print("0");
        gotoXY(33,23); write(GREY3); print(".=EXIT");
    }

    private int findZero() {
        for (int i=0; i<16; ++i) if (board[i] == 0) return i;
        return -1;
    }

    private boolean move(int keyMove) {
        final int pos = findZero();
        int x = pos % 4;
        int y = pos / 4;
        if ((keyMove == K_UP && y == 3) ||
            (keyMove == K_DOWN && y == 0) ||
            (keyMove == K_LEFT && x == 3) ||
            (keyMove == K_RIGHT && x == 0)) return false;
        switch (keyMove) {
            case K_UP: ++y; break;
            case K_DOWN: --y; break;
            case K_LEFT: ++x; break;
            case K_RIGHT: --x; break;
            default: return false;
        }
        int newPos = x + (y * 4);
        int t = board[pos];
        board[pos] = board[newPos];
        board[newPos] = t;
        return true;
    }

    public void shuffle() {
        int swaps = 0;
        int tries = 0;
        while (swaps < MAX_SWAPS && tries < 1000) {
            int charMove = "wasd".charAt(random.nextInt(4));
            if (move(charMove)) ++swaps;
            ++tries;
        }
    }

    private void drawBoard() {
        write(HOME, DOWN);
        write(RIGHT);
        for (int row=0; row<4; ++row) {
            for (int j=0; j<5; ++j) {
                for (int col=0; col<4; ++col) {
                    final int index = col + (row * 4);
                    final int element = board[index];
                    final String num = (j != 2 || element == 0) ? BLANK : SPACE + (element < 10 ? SPACE : EMPTY) + element + "  ";
                    final int rev = (element == 0 ? REVOFF : REVON);
                    write(color[element]);
                    write(rev); print(num); write(REVOFF, RIGHT);
                }
                println(); write(RIGHT);
            }
            if (row < 3) { println(); write(RIGHT); }
        }
    }

    private void logo() {
        write(
                19, 32, 13, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
                32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, -97, -69, 32,
                -69, -106, -84, -69, 32, -103, -94, 32, -102, -69, -98, -84, -69, 13, 32, 32,
                32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
                32, 32, 32, 32, 32, 32, 32, 32, 32, 18, -97, -84, -65, -110, -95, 18,
                -106, -68, -66, -103, -95, -110, -84, -69, -102, -95, -98, -95, -68, 13, 32, 32,
                32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
                32, 32, 32, 32, 32, 32, 32, 32, 32, -97, -95, 32, -95, -106, -95, 18,
                -95, -110, -103, -68, -94, -66, -102, -95, -98, -65, 18, -65, -110, 13, 32, 13,
                32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
                32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 18, -127,
                -66, -110, -95, 18, 30, 32, -94, -94, -110, 13, 32, 32, 32, 32, 32, 32,
                32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
                32, 32, 32, 32, 32, 32, 32, 32, 18, -127, -95, -110, -95, 18, 30, -94,
                -94, -68, -110, 13, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
                32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
                32, 32, 18, -127, -95, -110, -95, 30, -94, 32, 18, 32, -110, 13, 32, 32,
                32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
                32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, -127, -68, -66, 30,
                -68, 18, -94, -110, -66
        );
    }

}
