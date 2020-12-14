package eu.sblendorio.bbs.tenants.ascii;

import eu.sblendorio.bbs.core.AsciiThread;
import eu.sblendorio.bbs.core.Hidden;
import eu.sblendorio.bbs.games_ai.TicTacToeAI;

import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

@Hidden
public class TicTacToeAscii extends AsciiThread {
    TicTacToeAI model;

    public TicTacToeAscii() {
        super();
    }

    @Override
    public void doLoop() throws Exception {
        model = new TicTacToeAI();
        model.setComputerIsOpponent(true);
        model.setComputerIsDifficult(true);
        model.startNewGame();
        model.performWinCheck();
        boolean userTurn = true;
        boolean firstMove = true;
        do {
            cls();
            if (getScreenRows() != 15) {
                if (userTurn && !firstMove) println("Computers' move:"); else drawLogo();
                newline();
            }
            firstMove = false;
            drawBoard();

            log("TicTacToe, userTurn="+userTurn);
            if (userTurn) {
                int row=-1, col=-1;
                String coords;
                boolean validCoords = false;
                do {
                    log("TicTacToe, asking user move.");
                    print("your move or \".\": ");
                    flush();
                    resetInput();
                    String coordsRaw = readLine(2);
                    coords = lowerCase(coordsRaw);
                    if (".".equals(coords)) {
                        log("Exiting TIC-TAC-TOE with explicit command");
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
            } else {
                char piece = model.getPlayerToMove();
                String coords = computerMove(model);
                int row = coords.charAt(0) - '0';
                int col = coords.charAt(1) - '0';
            }
            userTurn = !userTurn;
        } while (!model.gameIsComplete());
        if (model.getGameWinner() == ' ')
            println("The match is draw");
        else
            println("The winner is '"+model.getGameWinner()+"'");
        println();
        println("Press any key to go back");
        flush(); resetInput(); readKey();
        log("Exiting TIC-TAC-TOE after match end");
        log("Going back to main menu");
    }


    public void drawBoard() {
        println(" !ABC");
        println("-+---");
        for (int i = 0; i < 3; ++i) {
            print((i+1)+"!");
            for (int j = 0; j < 3; ++j) {
                write(model.getGameBoard()[i][j]);
            }
            println();
        }
        if (getScreenRows() != 15) println();
        flush();
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

    private void drawLogo() {
        println("TIC-TAC-TOE");
        flush();
    }
}
