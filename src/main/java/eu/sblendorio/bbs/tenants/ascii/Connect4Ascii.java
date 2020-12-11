package eu.sblendorio.bbs.tenants.ascii;

import com.google.common.collect.ImmutableMap;
import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.games_ai.ConnectFourAI;
import eu.sblendorio.bbs.games_ai.ConnectFourAI.Board;
import java.util.Map;

@Hidden
public class Connect4Ascii extends AsciiThread {

    public Connect4Ascii() {
    }

    private Map<Integer, String> playerName = ImmutableMap.of(
        1,"Robot",
        2,"Human");

    private Map<Boolean, Integer> playerId = ImmutableMap.of(
        false, 1,
        true, 2);

    @Override
    public void doLoop() throws Exception {
        cls();
        log("CONNECT-4. Start match");
        drawLogo();
        boolean isHumanTurn = true;

        Board b = new Board();
        ConnectFourAI ai = new ConnectFourAI(b);
        int x = 1;
        int winner = -1;
        do {
            drawBoard(b);
            int player = playerId.get(isHumanTurn);
            displayTurn(player);
            if (isHumanTurn) {
                do {
                    int ch;
                    do {
                        print("Your move (1-7) or \".\": ");
                        flush();
                        resetInput();
                        ch = readKey();
                        println();
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
            log("CONNECT-4. player="+player+", "+x+"["+y+"]");
            isHumanTurn = !isHumanTurn;
            winner = ai.gameResult(b);
        } while (winner < 0);
        log("CONNECT-4. Winner is: "+winner);
        drawBoard(b);
        println();
        if (winner == 1 || winner == 2)
            declareWinner(winner);
        else
            noWinner();
        println();
        println("PRESS ENTER");
        flush();
        int ch;
        do {
            resetInput();
            ch = readKey();
        } while (ch != 13 && ch != 10 && ch != '.');
        log("CONNECT-4. EXIT after match");
    }

    private void declareWinner(int player) {
        println("WINNER: " + playerName.get(player));
    }

    private void noWinner() {
        println("NO WINNER: DRAW!");
    }

    private void displayTurn(int player) {
        println();
        print("Turn of ");
        println(playerName.get(player));
    }

    private void drawBoard(Board b) {
        println("+-------+");
        for (int i=0; i<6; ++i) {
            print("!");
            for (int j=0; j<7; ++j) {
                if (b.getElement(i,j) == 0) {
                    print(" ");
                } else if (b.getElement(i,j) == 1) {
                    print("O");
                } else {
                    print("X");
                }
            }
            println("!");
        }
        println("+-------+");
        println("!1234567!");
        flush();
    }

    private void drawLogo() {
        println("CONNECT 4");
        println();
    }
}
