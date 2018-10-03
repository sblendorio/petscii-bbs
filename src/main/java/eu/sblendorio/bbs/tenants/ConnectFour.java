package eu.sblendorio.bbs.tenants;

import com.google.common.collect.ImmutableMap;
import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.core.PetsciiThread;
import eu.sblendorio.bbs.games_ai.ConnectFourAI.Board;
import eu.sblendorio.bbs.games_ai.ConnectFourAI;

import java.util.Map;

import static eu.sblendorio.bbs.core.Keys.*;
import static eu.sblendorio.bbs.core.Colors.*;

@Hidden
public class ConnectFour extends PetsciiThread {

    protected int player1color = BLUE;
    protected int player2color = RED;

    private Map<Integer, Integer> playerColor = ImmutableMap.of(
            1, player1color,
            2, player2color);

    private Map<Integer, String> playerName = ImmutableMap.of(
            1,"Robot",
            2,"Human");

    private Map<Integer, String> playerMsg = ImmutableMap.of(
            1," THINKING ",
            2," PRESS 1-7");

    private Map<Integer, Integer> playerCtrl = ImmutableMap.of(
            1, REVON,
            2, REVOFF);

    private Map<Boolean, Integer> playerId = ImmutableMap.of(
            false, 1,
            true, 2);

    @Override
    public void doLoop() throws Exception {
        log("CONNECT-4. Start match");
        boolean userTurn = true;
        write(CLR, LOWERCASE, CASE_LOCK);
        logo();
        drawBoard();
        gotoXY(33,23); write(GREY3); print(".=EXIT");
        boolean isHumanTurn = true;

        Board b = new Board();
        ConnectFourAI ai = new ConnectFourAI(b);
        int x = 1;
        int winner = -1;
        do {
            int player = playerId.get(isHumanTurn);
            displayTurn(player);
            if (isHumanTurn) {
                do {
                    int ch;
                    do {
                        flush();
                        resetInput();
                        ch = readKey();
                        if (ch == '.') {
                            log("CONNECT-4. EXIT by user");
                            return;
                        }
                    } while (ch < '1' || ch > '7');
                    x = ch - '1';
                } while (!b.isLegalMove(x));
            } else {
                x = ai.getAIMove();
            }
            int y = b.getHeight(x);
            b.placeMove(x, isHumanTurn ? 2 : 1);
            putPiece(player, x, y);
            log("CONNECT-4. player="+player+", "+x+"["+y+"]");
            isHumanTurn = !isHumanTurn;
            winner = ai.gameResult(b);
        } while (winner < 0);
        log("CONNECT-4. Winner is: "+winner);
        if (winner == 1 || winner == 2)
            declareWinner(winner);
        else
            noWinner();
        write(GREY3);
        gotoXY(32,18); print("PRESS");
        gotoXY(32,19); print("ENTER");
        flush();
        int ch;
        do {
            resetInput();
            ch = readKey();
        } while (ch != 13 && ch != 10 && ch != '.');
        log("CONNECT-4. EXIT after match");
    }

    private void declareWinner(int player) {
        gotoXY(31, 8); write(WHITE); println("WINNER:");
        drawDisc(playerColor.get(player), 33, 10);

        gotoXY(29,15); write(REVOFF); print("          ".substring(0,playerMsg.get(player).length()));
        gotoXY(29,16); print("          ".substring(0,playerMsg.get(player).length()));
        gotoXY(29,17); print("          ".substring(0,playerMsg.get(player).length()));

        gotoXY(31, 13); write(YELLOW, REVON); print("          ".substring(0,playerName.get(player).length()+2));
        gotoXY(31, 14); print(" " + playerName.get(player) + " ");
        gotoXY(31, 15); print("          ".substring(0,playerName.get(player).length()+2));
        write(REVOFF);
    }

    private void noWinner() {
        gotoXY(30, 8); write(WHITE); println("NO WINNER");
        drawDisc(YELLOW, 33, 10);

        gotoXY(29,15); write(REVOFF); print("          ");
        gotoXY(29,16); print("          ");
        gotoXY(29,17); print("          ");

        gotoXY(31, 13); write(YELLOW, REVON); print("       ");
        gotoXY(31, 14); print(" DRAW! ");
        gotoXY(31, 15); print("       ");
        write(REVOFF);
    }

    private void displayTurn(int player) {
        gotoXY(31, 8); write(WHITE); println("Turn of");
        drawDisc(playerColor.get(player), 33, 10);
        gotoXY(32, 13); write(WHITE); print(playerName.get(player));
        gotoXY(29,15); write(GREY3, playerCtrl.get(player)); print("          ".substring(0,playerMsg.get(player).length()));
        gotoXY(29,16); write(GREY3); print(playerMsg.get(player));
        gotoXY(29,17); write(GREY3, playerCtrl.get(player)); print("          ".substring(0,playerMsg.get(player).length()));
        write(REVOFF); gotoXY(30, 13);
    }

    private void putPiece(int player, int x, int y) {
        drawDisc(playerColor.get(player), 1+(4*x), 21-(3*y));
    }

    private void drawDisc(int color, int x, int y) {
        write(color);
        gotoXY(x, y); write(REVON, 190, 32, 188);
        gotoXY(x, y+1); write( 187, 32, 172, REVOFF);

    }

    private void drawBoard() {
        write(HOME, YELLOW, REVOFF, DOWN, DOWN, DOWN, DOWN, DOWN);
        for (int i = 0; i < 6; ++i) {
            if (i == 0) {
                write(176, 192, 192, 192);
                for (int j = 0; j < 6; ++j) write( 178, 192, 192, 192);
                write(174, 13);
            } else {
                write(171, 192, 192, 192);
                for (int j = 0; j < 6; ++j) write(219, 192, 192, 192);
                write(179, 13);
            }
            for (int k = 0; k < 2; ++k) {
                for (int j = 0; j < 7; ++j) write(221, 32, 32, 32);
                write(221, 13);
            }
        }
        write(173, 192, 192, 192);
        for (int j = 0; j < 6; ++j) write(177, 192, 192, 192);
        write(189, 13, WHITE);
        for (int j = 1; j <= 7; ++j) print("  "+j+" ");
    }

    private void logo() {
        write(HOME,
            32, -103, -94, -69, -102, -84, -94, 32, -106, -69, 32, 28, -69, -98, -69, 32,
            -107, -69, 5, -94, -97, -94, 32, -101, -94, 5, -69, -100, -94, -94, -69, 13,
            18, -103, -95, -110, 32, 32, -102, -95, 32, 31, -95, 18, -106, -84, -110, -69,
            28, -95, 18, -98, -84, -110, -69, -107, -95, 18, 5, -68, -110, -69, 18, -101,
            -95, -110, 32, 32, 32, -100, -95, 32, 28, -84, -94, -94, 32, -94, -94, 32,
            -94, 32, -94, -84, -94, -94, 13, 30, -68, -94, -69, 31, -65, -94, -66, 28,
            -95, -106, -68, 28, -95, -107, -95, -98, -68, -107, -95, 18, -97, -68, -110, 5,
            -94, -104, -68, -94, -69, 32, -102, -95, 32, 18, 28, -95, -110, -95, 32, 18,
            -95, -110, -95, 18, -95, -110, -95, 18, 32, -110, 32, 18, 32, -95, -110, -95,
            18, -95, -110, -95, 13, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
            32, 32, 32, 32, 32, 32, 32, 32, 32, 18, 31, -95, -84, -110, -66, 18,
            -95, -110, -95, 18, -95, -110, -95, 18, 32, -110, 32, 18, 32, -95, -84, -69,
            -110, -69, 13, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
            32, 32, 32, 32, 32, 32, 32, -68, -66, 32, 32, 18, -94, -94, -110, 32,
            -68, 18, -94, -110, -66, -68, -66, -68, -66, 13
        );
    }
}
