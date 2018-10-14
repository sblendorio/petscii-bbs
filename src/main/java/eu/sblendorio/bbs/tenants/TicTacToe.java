package eu.sblendorio.bbs.tenants;

import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.core.PetsciiThread;
import eu.sblendorio.bbs.games_ai.TicTacToeAI;

import java.io.IOException;

import static eu.sblendorio.bbs.core.Keys.*;
import static eu.sblendorio.bbs.core.Colors.*;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

@Hidden
public class TicTacToe extends PetsciiThread {
    TicTacToeAI model;

    @Override
    public void doLoop() throws Exception {
        model = new TicTacToeAI();
        model.setComputerIsOpponent(true);
        model.setComputerIsDifficult(true);
        model.startNewGame();
        model.performWinCheck();
        boolean userTurn = true;
        write(CLR, UPPERCASE, CASE_LOCK);
        logo();
        drawBoard();
        do {
            log("TicTacToe, userTurn="+userTurn);
            if (userTurn) {
                int row=-1, col=-1;
                String coords;
                boolean validCoords = false;
                do {
                    log("TicTacToe, asking user move.");
                    gotoXY(2, 16);
                    print("move or \".\":   ");
                    write(LEFT, LEFT);
                    flush();
                    resetInput();
                    String coordsRaw = readLine(2);
                    coords = lowerCase(coordsRaw);
                    if (".".equals(coords)) {
                        log("Exiting TIC-TAC-TOE with explicit command");
                        write(CLR, LOWERCASE);
                        log("Going back to main menu");
                        return;
                    }
                    if (length(coords) == 2) {
                        log("TicTacToe, usermove="+coords);
                        if (coords.charAt(0) >= 'a' && coords.charAt(0) <= 'c' && coords.charAt(1) >= '1' && coords.charAt(1) <= '3') {
                            row = toInt(coords.substring(1, 2)) - 1;
                            col = coords.charAt(0) - 'a';
                        } else if (coords.charAt(0) >= '1' && coords.charAt(0) <= '3' && coords.charAt(1) >= 'a' && coords.charAt(1) <= 'c') {
                            row = toInt(coords.substring(0, 1)) - 1;
                            col = coords.charAt(1) - 'a';
                        }
                        final char value = (row>=0 && col>=0) ? model.getGameBoard()[row][col] : '*';
                        validCoords = (value == ' ');
                        log("TicTacToe, usermoveValid="+validCoords);
                    }
                } while (!validCoords);
                char piece = model.getPlayerToMove();
                model.makeMoveInSquare(row, col);
                drawPiece(piece, row, col); flush();
            } else {
                char piece = model.getPlayerToMove();
                String coords = computerMove(model);
                int row = coords.charAt(0) - '0';
                int col = coords.charAt(1) - '0';
                drawPiece(piece, row, col); flush();
            }
            userTurn = !userTurn;
        } while (!model.gameIsComplete());
        gotoXY(2, 18);
        if (model.getGameWinner() == ' ')
            println("the match is draw");
        else
            println("the winner is '"+model.getGameWinner()+"'");
        println();
        println("  press any key to go back");
        flush(); readKey();
        log("Exiting TIC-TAC-TOE after match end");
        write(CLR, LOWERCASE);
        log("Going back to main menu");
        return;
    }


    public void drawBoard() {
        write(HOME, RETURN, WHITE,
                32, 32, 32, 32, 'A', 32, 32, 32, 'B', 32, 32, 32, 'C', RETURN, GREY3,
                32, 32,176,192,192,192,178,192,192,192,178,192,192,192,174, RETURN,
                32, 32,221, 32, 32, 32, 221,32, 32, 32, 221, 32, 32, 32,221,RETURN,
                32, WHITE, '1', GREY3, 221, 32, 32, 32, 221,32, 32, 32, 221, 32, 32, 32,221,RETURN,
                32, 32,221, 32, 32, 32, 221,32, 32, 32, 221, 32, 32, 32,221,RETURN,
                32, 32,171, 192,192,192,219,192,192,192,219,192,192,192,179,RETURN,
                32, 32,221, 32, 32, 32, 221,32, 32, 32, 221, 32, 32, 32,221,RETURN,
                32, WHITE, '2', GREY3, 221, 32, 32, 32, 221,32, 32, 32, 221, 32, 32, 32,221,RETURN,
                32, 32,221, 32, 32, 32, 221,32, 32, 32, 221, 32, 32, 32,221,RETURN,
                32, 32,171, 192,192,192,219,192,192,192,219,192,192,192,179,RETURN,
                32, 32,221, 32, 32, 32, 221,32, 32, 32, 221, 32, 32, 32,221,RETURN,
                32, WHITE, '3',GREY3, 221, 32, 32, 32, 221,32, 32, 32, 221, 32, 32, 32,221,RETURN,
                32, 32,221, 32, 32, 32, 221,32, 32, 32, 221, 32, 32, 32,221,RETURN,
                32, 32,173,192,192,192, 177,192,192,192,177,192,192,192, 189,RETURN
        );
    }

    public void drawPiece(char piece, int row, int col) {
        if (piece == 'x') drawX(row, col);
        else if (piece == 'o') drawO(row, col);
    }

    public void drawX(int row, int col) {
        write(HOME, DOWN, DOWN, DOWN);
        for (int i=0; i<4*row; ++i) write(DOWN);
        for (int i=0; i<4*col+3; ++i) write(RIGHT);
        write(205, 32, 206, DOWN, LEFT, LEFT, LEFT,
                32, 214, 32, DOWN, LEFT, LEFT, LEFT,
                206, 32, 205);
    }

    public void drawO(int row, int col) {
        write(HOME, DOWN, DOWN, DOWN);
        for (int i=0; i<4*row; ++i) write(DOWN);
        for (int i=0; i<4*col+3; ++i) write(RIGHT);
        write(213, 196,201, DOWN, LEFT, LEFT, LEFT,
                199, 32, 200, DOWN, LEFT, LEFT, LEFT,
                202, 198,203);
    }

    public String computerMove(TicTacToeAI model) {
        final char[][] before = gridCopy(model.getGameBoard());
        model.computerMove();
        final char[][] after = model.getGameBoard();
        for (int i=0; i<3; ++i) {
            for (int j=0; j<3 ; ++j) {
                if (before[i][j] != after[i][j]) return i+""+j;
            }
        }
        return "00";
    }

    private static char[][] gridCopy(char[][] grid) {
        char[][] result = new char[3][3];
        for (int i=0; i<3; ++i) for (int j=0; j<3; ++j) result[i][j] = grid[i][j];
        return result;
    }

    private void logo() throws IOException {
        write(new byte[]{
                32,  13,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,
                32,  32,  32,  32,  32,  31, -84, -94, -94, -94, -94, -94, -94, -94, -94, -94,
                -94, -94, -94, -94, -94, -94, -94, -94,  13,  32,  32,  32,  32,  32,  32,  32,
                32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  18, -95,-110,
                -95,  28, -94, -94,  13,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,
                32,  32,  32,  32,  32,  32,  32,  32,  32,  18,  31, -95,-110, -95,  18,  28,
                -95,-110, -95,  32, -98, -94, -94,  13,  32,  32,  32,  32,  32,  32,  32,  32,
                32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  18,  31, -95,-110,
                -95,  18,  28, -95,-110, -95,  18, -98, -95,-110, -95,  13,  32,  32,  32,  32,
                32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,
                32,  32,  18,  28, -66, -68, -98, -95,-110, -95,  13,  32,  32,  32,  32,  32,
                32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,
                32,  32,  32, -68,  18, -68,-110, -94, -94, -94, -94, -94, -94, -94, -94, -94,
                -94, -94,  13,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,
                32,  32,  32,  32,  32,  32,  32,  32,  32, -97, -94, -94, -94,  32,  32,  32,
                32,  32,  32,  32,  32,  32,  32,  32,  18, -98,  32,-110,  13,  32,  32,  32,
                32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,
                32,  32,  32,  32,  18, -97,  32,-110,  32,-100, -94, -94,  32,  32,  32,  32,
                32,  32,  32,  32,  32,  18, -98,  32,-110,  13,  32,  32,  32,  32,  32,  32,
                32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,
                32,  18, -97,  32,-100, -95,-110, -95,  18, -95,-110, -95,  18,  30, -66, -94,
                -110, -66,  32,  32,  32,  32,  32,  18, -98,  32,-110,  13,  32,  32,  32,  32,
                32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,
                32,  32,  32,  18, -97,  32,-100, -95, -84, -69,-110, -95,  18,  30,  32,-110,
                32,  32,  32,  32,  32,  32,  32,  18, -98,  32,-110,  13,  32,  32,  32,  32,
                32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,
                32,  32,  32,  32,  18,-100, -95,-110, -95,  18, -95,-110, -95,  18,  30,  32,
                -110,  32,  32,  32,  32,  32,  32,  32,  18, -98, -69,-110,  13,  32,  32,  32,
                32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,
                32,  32,  32,  32,  32,  18,-100, -95,-110, -95,  32,  32,  30, -68,  18, -94,
                -110, -66,  32,  32,  32,  32,  32,  18, -98, -95,-110,  13,  32,  32,  32,  32,
                32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,
                32,  32,  32,  32,  18,-100, -95,-110, -95,-102, -94, -94, -94,  32,  32,  32,
                32,  32,  32,  32,  18, -98, -95,-110,  13,  32,  32,  32,  32,  32,  32,  32,
                32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,
                32,  18,-100, -95,-110, -95,  32,  18,-102,  32,-110,  32,-106, -94, -94,  32,
                32,  32,  32,  32,  18, -98, -95,-110,  13,  32,  32,  32,  32,  32,  32,  32,
                32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,
                32,  18,-100, -95,-110, -95,  32,  18,-102,  32,-106, -95,-110, -95,  18, -95,
                -110, -95,-104, -94, -94, -69,  32,  18, -98, -95,-110,  13,  32,  32,  32,  32,
                32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,
                32,  32,  32,  32,  18,-100, -95,-110,  32,  32,  18,-102,  32,-106, -95,-110,
                -95,  18, -95,-110, -95,  18,-104,  32,-110,  32,  32,  32,  18, -98, -95,-110,
                13,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,
                32,  32,  32,  32,  32,  32,  32,  32,  32,  18,-100, -95,-110,  32,  32,  18,
                -102, -95,-110,-106, -68,  18, -68, -66,-110, -66,  18,-104,  32, -94,-110,  32,
                32,  18, -98, -95,-110,  13,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,
                32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  32,  18,-100,
                -95,-110,  32,  32,  18,-102, -95,-110,  32,  32,  32,  32,  18,-104,  32,-110,
                -94, -69,  32,  18, -98, -95,-110,  13
        });
    }
}
