// CREDITS: https://github.com/bigbhowell/tic-tac-toe
package eu.sblendorio.bbs.games_ai;

//
//  File: TicTacToeAI.java
//  Auth: Brian W. Howell
//  Date: 20 January 2014
//  Desc: Model is the Tic-Tac-Toe game.
//        The model tracks the state of the game, supplies that game play rules, and provides
//        logic for AI game play.
//

import java.util.Random; // ai gameplay

public class TicTacToeAI
{
    private char      playerToMove;                // who's move is it?
    private boolean   computerIsOpponent;          // is opponent the computer?
    private boolean   computerIsDifficult;         // easy[0], hard[1]
    private char[][]  gameBoard = new char[3][3];  // game board
    private int       moveCount;                   // counts the total moves played in game
    private boolean   gameIsComplete;              // monitor game completion state
    private char      gameWinner;                  // who won the game?
    private WinPath   winPath = new WinPath();     // how was the 3 in a row made?

    // Class WinPath
    // A win path is the path on the game board followed to make 3 in a row.
    public class WinPath
    {
        private int startRow;  // path starts in row
        private int startCol;  // path starts in column
        private int endRow;    // path ends in row
        private int endCol;    // path ends in column

        public int getStartRow() { return startRow; }
        public int getStartCol() { return startCol; }
        public int getEndRow()   { return endRow; }
        public int getEndCol()   { return endCol; }
        public void setPath( int startRow, int startCol, int endRow, int endCol )
        {
            this.startRow = startRow;
            this.startCol = startCol;
            this.endRow = endRow;
            this.endCol = endCol;
        }
    } // end class WinPath

    //--------------------------//
    //    GETTERS & SETTERS     //
    //--------------------------//

    public char[][] getGameBoard()        { return gameBoard; }
    public boolean  computerIsOpponent()  { return computerIsOpponent; }
    public boolean  computerIsDifficult() { return computerIsDifficult; }
    public char     getPlayerToMove()     { return playerToMove; }
    public boolean  gameIsComplete()      { return gameIsComplete; }
    public char     getGameWinner()       { return gameWinner; }
    public WinPath  getWinPath()          { return winPath; }

    public void setComputerIsOpponent ( boolean trueForComputerMode )
    {
        computerIsOpponent = trueForComputerMode;
    }

    public void setComputerIsDifficult ( boolean trueForDifficult )
    {
        computerIsDifficult = trueForDifficult;
    }

    //-------------------//
    //    GAME SETUP     //
    //-------------------//

    // Tic Tac Toe model constructor
    // Sets up the default game state to 2 player
    public TicTacToeAI()
    {
        startNewGame( false );
    }

    // Starts a new game with the current opponent mode.
    public void startNewGame()
    {
        playerToMove = 'x';  // x always first
        moveCount = 0;
        gameWinner = ' ';
        gameIsComplete = false;
        resetGameBoard();
    }

    // Sets the opponent mode and starts a new game.
    public void startNewGame( boolean trueForComputerMode )
    {
        computerIsOpponent = trueForComputerMode;
        startNewGame();
    }

    // Sets the game board to its default empty state.
    private void resetGameBoard()
    {
        for ( int i = 0; i < 3; i++ ) {
            for ( int j = 0; j < 3; j++ ) {
                gameBoard[i][j] = ' ';
            }
        }
    }

    //------------------------//
    //    BASIC GAME PLAY     //
    //------------------------//

    // Returns true if a square is already played.
    public boolean squareHasBeenPlayed( int row, int col )
    {
        return gameBoard[row][col] != 'x' && gameBoard[row][col] != 'o' ? false : true;
    }

    // Makes a move in a game board square.
    public void makeMoveInSquare( int row, int col )
    {
        gameBoard[row][col] = playerToMove;           // make the move in the game board model

        if ( playerToMove == 'x' )                    // update the player to move
            playerToMove = 'o';
        else if ( playerToMove == 'o' )
            playerToMove = 'x';

        performWinCheck();                            // check for a win
        ++moveCount;                                  // count the move
        if ( moveCount == 9 ) gameIsComplete = true;  // set the game to complete if # of moves is 9
    }

    // Sets gameIsComplete to true if winning sequence is found.
    public void performWinCheck()
    {
        if ( rowWins() || colWins() || diagWins() ) gameIsComplete = true;
    }

    // Returns true if a winning row is found.
    // Sets the winner and the win path.
    private boolean rowWins()
    {
        for ( int i = 0; i < 3; i++ ) {
            int xCount = 0, oCount = 0;
            for ( int j = 0; j < 3; j++ ) {
                if ( gameBoard[i][j] == 'x' ) ++xCount;
                if ( gameBoard[i][j] == 'o' ) ++oCount;
            }
            if ( xCount == 3 || oCount == 3 ) {
                if ( xCount == 3 ) gameWinner = 'x';
                if ( oCount == 3 ) gameWinner = 'o';
                winPath.setPath( i, 0, i, 2);
                return true;
            }
        }
        return false;
    } // end rowWins()

    // Returns true if a winning column is found.
    // Sets the winner and the win path.
    private boolean colWins()
    {
        for ( int i = 0; i < 3; i++ ) {
            int xCount = 0, oCount = 0;
            for ( int j = 0; j < 3; j++ ) {
                if ( gameBoard[j][i] == 'x' ) ++xCount;
                if ( gameBoard[j][i] == 'o' ) ++oCount;
            }
            if ( xCount == 3 || oCount == 3 ) {
                if ( xCount == 3 ) gameWinner = 'x';
                if ( oCount == 3 ) gameWinner = 'o';
                winPath.setPath( 0, i, 2, i);
                return true;
            }
        }
        return false;
    } // end colWins()

    // Returns true if a winning diagonal is found.
    // Sets the winner and the win path.
    private boolean diagWins()
    {
        if ( gameBoard[0][0] == 'x' && gameBoard[1][1] == 'x' && gameBoard[2][2] == 'x' ) {
            gameWinner = 'x';
            winPath.setPath( 0, 0, 2, 2 );
            return true;
        } else if  ( gameBoard[2][0] == 'x' && gameBoard[1][1] == 'x' && gameBoard[0][2] == 'x' ) {
            gameWinner = 'x';
            winPath.setPath( 0, 2, 2, 0 );
            return true;
        } else if  ( gameBoard[0][0] == 'o' && gameBoard[1][1] == 'o' && gameBoard[2][2] == 'o' ) {
            gameWinner = 'o';
            winPath.setPath( 0, 0, 2, 2 );
            return true;
        } else if  ( gameBoard[2][0] == 'o' && gameBoard[1][1] == 'o' && gameBoard[0][2] == 'o' ) {
            gameWinner = 'o';
            winPath.setPath( 0, 2, 2, 0 );
            return true;
        } else {
            return false;
        }
    } // end diagWins()

    //-------------------//
    //    COMPUTER AI    //
    //-------------------//

    // Makes the computer's move.
    public void computerMove()
    {
        Random rgen = new Random();                            // Computer
        if ( playWin() ) return;                               // always plays win
        if ( computerIsDifficult() ) {                         // always blocks when hard
            if ( blockWin() ) return;
        }
        if ( !computerIsDifficult() && rgen.nextBoolean() ) {  // sometimes blocks when easy
            if ( blockWin() ) return;
        }
        if ( computerIsDifficult() ) {                         // always prevents forks when hard
            if ( preventForkScenarios() ) return;
        }
        if ( !computerIsDifficult() && rgen.nextBoolean() ) {  // sometimes prevents forks when easy
            if ( preventForkScenarios() ) return;
        }
        if ( playCenter() ) return;
        if ( playOppositeCorner() ) return;
        if ( playEmptyCorner() ) return;
        playEmptySide();
    }

    private boolean playWin()
    {
        return playThirdInSequenceOfTwo('o') ? true : false;
    }

    private boolean blockWin()
    {
        return playThirdInSequenceOfTwo('x') ? true : false;
    }

    // PlayThirdInSequenceOfTwo() is used to find rows, columns and diagonals that have two of the
    // same character, 'x' or 'o', plus a blank square.
    // When found the empty square is played and true is returned.
    // The <playedBy> parameter is used to define the player for which we are searching for
    // two in a row. This allows this method to serve as a win blocker or a win maker.
    private boolean playThirdInSequenceOfTwo( char playedBy )
    {
        if ( moveCount < 3 ) return false; // only check after 4 moves have been made

        // Search rows for 2 plus empty square.
        for ( int i = 0; i < 3; i++ ) {
            int count = 0, emptyCount = 0;
            for ( int j = 0; j < 3; j++ ) {
                if ( gameBoard[i][j] == playedBy ) ++count;
                if ( gameBoard[i][j] == ' ' ) ++emptyCount;
            }
            if ( count == 2 && emptyCount == 1 ) {
                for ( int j = 0; j < 3; j++ ) {
                    if ( gameBoard[i][j] == ' ' ) makeMoveInSquare( i, j );
                }
                return true;
            }
        } // end row search

        // Search columns for 2 plus empty square.
        for ( int i = 0; i < 3; i++ ) {
            int count = 0, emptyCount = 0;
            for ( int j = 0; j < 3; j++ ) {
                if ( gameBoard[j][i] == playedBy ) ++count;
                if ( gameBoard[j][i] == ' ' ) ++emptyCount;
            }
            if ( count == 2 && emptyCount == 1 ) {
                for ( int j = 0; j < 3; j++ ) {
                    if ( gameBoard[j][i] == ' ' ) makeMoveInSquare( j, i );
                }
                return true;
            }
        } // end column search

        // Search downward diagonal for 2 plus empty square.
        int count = 0, emptyCount = 0;
        for ( int i = 0, j = 0; i < 3; ++i, ++j )
        {
            if ( gameBoard[i][j] == playedBy ) ++count;
            if ( gameBoard[i][j] == ' ' ) ++emptyCount;
            if ( count == 2 && emptyCount == 1 ) {
                if ( gameBoard[0][0] == ' ' ) makeMoveInSquare( 0, 0 );
                if ( gameBoard[1][1] == ' ' ) makeMoveInSquare( 1, 1 );
                if ( gameBoard[2][2] == ' ' ) makeMoveInSquare( 2, 2 );
                return true;
            }
        } // end downward diagonal search

        // Search upward diagonal for 2 plus empty square.
        count = 0; emptyCount = 0;
        for ( int i = 0, j = 2; i < 3; i++, j-- )
        {
            if ( gameBoard[i][j] == playedBy ) ++count;
            if ( gameBoard[i][j] == ' ' ) ++emptyCount;
            if ( count == 2 && emptyCount == 1 ) {
                if ( gameBoard[2][0] == ' ' ) makeMoveInSquare( 2, 0 );
                if ( gameBoard[1][1] == ' ' ) makeMoveInSquare( 1, 1 );
                if ( gameBoard[0][2] == ' ' ) makeMoveInSquare( 0, 2 );
                return true;
            }
        } // end upward diagonal search

        return false;
    } // end playThirdInSequenceOfTwo( char playedBy )

    // Prevents fork scenarios that allow x to win
    private boolean preventForkScenarios()
    {
        if ( moveCount == 3 ) {
            if ( gameBoard[0][0] == 'x' && gameBoard[1][1] == 'o' && gameBoard[2][2] == 'x' ) {
                playEmptySide();
                return true;
            }
            if ( gameBoard[2][0] == 'x' && gameBoard[1][1] == 'o' && gameBoard[0][2] == 'x' ) {
                playEmptySide();
                return true;
            }
            if ( gameBoard[2][1] == 'x' && gameBoard[1][2] == 'x' ) {
                makeMoveInSquare( 2, 2 );
                return true;
            }
        }
        return false;
    }

    // Plays the center and returns true if move is made.
    private boolean playCenter()
    {
        if ( gameBoard[1][1] == ' ' ) {
            makeMoveInSquare( 1, 1 );
            return true;
        }
        return false;
    }

    // Plays a corner opposite a corner already played by 'x'
    // and returns true if move is made.
    private boolean playOppositeCorner()
    {
        if ( gameBoard[0][0] == 'x' && gameBoard[2][2] == ' ' ) {
            makeMoveInSquare( 2, 2 );
            return true;
        } else if ( gameBoard[2][2] == 'x' && gameBoard[0][0] == ' ' ) {
            makeMoveInSquare( 0, 0 );
            return true;
        } else if ( gameBoard[0][2] == 'x' && gameBoard[2][0] == ' ' ) {
            makeMoveInSquare( 2, 0 );
            return true;
        } else if ( gameBoard[2][0] == 'x' && gameBoard[0][2] == ' ' ) {
            makeMoveInSquare( 0, 2 );
            return true;
        }
        return false;
    }

    // Plays an empty corner and returns true if move is made.
    private boolean playEmptyCorner()
    {
        if ( gameBoard[0][0] == ' ' ) {
            makeMoveInSquare( 0, 0 );
            return true;
        } else if ( gameBoard[0][2] == ' ' ) {
            makeMoveInSquare( 0, 2 );
            return true;
        } else if ( gameBoard[2][0] == ' ' ) {
            makeMoveInSquare( 2, 0 );
            return true;
        } else if ( gameBoard[2][2] == ' ' ) {
            makeMoveInSquare( 2, 2 );
            return true;
        }
        return false;
    }

    // Plays an empty side square.
    private void playEmptySide()
    {
        if ( gameBoard[0][1] == ' ' ) {
            makeMoveInSquare( 0, 1 );
        } else if ( gameBoard[1][0] == ' ' ) {
            makeMoveInSquare( 1, 0 );
        } else if ( gameBoard[1][2] == ' ' ) {
            makeMoveInSquare( 1, 2 );
        } else if ( gameBoard[2][1] == ' ' ) {
            makeMoveInSquare( 2, 1 );
        }
    }

}  // end class TicTacToeAI