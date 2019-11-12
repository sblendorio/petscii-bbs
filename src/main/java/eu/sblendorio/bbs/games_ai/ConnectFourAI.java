// CREDITS: https://github.com/jn1772/Connect4AI
package eu.sblendorio.bbs.games_ai;

import java.util.Scanner;

public class ConnectFourAI {
    private Board b;
    private Scanner scan;
    private int nextMoveLocation=-1;
    private int maxDepth = 9;

    public ConnectFourAI(Board b){
        this.b = b;
        scan = new Scanner(System.in);
    }

    //Opponent's turn
    public void letOpponentMove(){
        System.out.println("Your move (1-7): ");
        int move = scan.nextInt();
        while(move<1 || move > 7 || !b.isLegalMove(move-1)){
            System.out.println("Invalid move.\n\nYour move (1-7): ");
            move = scan.nextInt();
        }

        //Assume 2 is the opponent
        b.placeMove(move-1, (byte)2);
    }

    //Game Result
    public int gameResult(Board b){
        int aiScore = 0, humanScore = 0;
        for(int i=5;i>=0;--i){
            for(int j=0;j<=6;++j){
                if(b.board[i][j]==0) continue;

                //Checking cells to the right
                if(j<=3){
                    for(int k=0;k<4;++k){
                        if(b.board[i][j+k]==1) aiScore++;
                        else if(b.board[i][j+k]==2) humanScore++;
                        else break;
                    }
                    if(aiScore==4)return 1; else if (humanScore==4)return 2;
                    aiScore = 0; humanScore = 0;
                }

                //Checking cells up
                if(i>=3){
                    for(int k=0;k<4;++k){
                        if(b.board[i-k][j]==1) aiScore++;
                        else if(b.board[i-k][j]==2) humanScore++;
                        else break;
                    }
                    if(aiScore==4)return 1; else if (humanScore==4)return 2;
                    aiScore = 0; humanScore = 0;
                }

                //Checking diagonal up-right
                if(j<=3 && i>= 3){
                    for(int k=0;k<4;++k){
                        if(b.board[i-k][j+k]==1) aiScore++;
                        else if(b.board[i-k][j+k]==2) humanScore++;
                        else break;
                    }
                    if(aiScore==4)return 1; else if (humanScore==4)return 2;
                    aiScore = 0; humanScore = 0;
                }

                //Checking diagonal up-left
                if(j>=3 && i>=3){
                    for(int k=0;k<4;++k){
                        if(b.board[i-k][j-k]==1) aiScore++;
                        else if(b.board[i-k][j-k]==2) humanScore++;
                        else break;
                    }
                    if(aiScore==4)return 1; else if (humanScore==4)return 2;
                    aiScore = 0; humanScore = 0;
                }
            }
        }

        for(int j=0;j<7;++j){
            //Game has not ended yet
            if(b.board[0][j]==0)return -1;
        }
        //Game draw!
        return 0;
    }

    int calculateScore(int aiScore, int moreMoves){
        int moveScore = 4 - moreMoves;
        if(aiScore==0)return 0;
        else if(aiScore==1)return 1*moveScore;
        else if(aiScore==2)return 10*moveScore;
        else if(aiScore==3)return 100*moveScore;
        else return 1000;
    }

    //Evaluate board favorableness for AI
    public int evaluateBoard(Board b){

        int aiScore=1;
        int score=0;
        int blanks = 0;
        int k=0, moreMoves=0;
        for(int i=5;i>=0;--i){
            for(int j=0;j<=6;++j){

                if(b.board[i][j]==0 || b.board[i][j]==2) continue;

                if(j<=3){
                    for(k=1;k<4;++k){
                        if(b.board[i][j+k]==1)aiScore++;
                        else if(b.board[i][j+k]==2){aiScore=0;blanks = 0;break;}
                        else blanks++;
                    }

                    moreMoves = 0;
                    if(blanks>0)
                        for(int c=1;c<4;++c){
                            int column = j+c;
                            for(int m=i; m<= 5;m++){
                                if(b.board[m][column]==0)moreMoves++;
                                else break;
                            }
                        }

                    if(moreMoves!=0) score += calculateScore(aiScore, moreMoves);
                    aiScore=1;
                    blanks = 0;
                }

                if(i>=3){
                    for(k=1;k<4;++k){
                        if(b.board[i-k][j]==1)aiScore++;
                        else if(b.board[i-k][j]==2){aiScore=0;break;}
                    }
                    moreMoves = 0;

                    if(aiScore>0){
                        int column = j;
                        for(int m=i-k+1; m<=i-1;m++){
                            if(b.board[m][column]==0)moreMoves++;
                            else break;
                        }
                    }
                    if(moreMoves!=0) score += calculateScore(aiScore, moreMoves);
                    aiScore=1;
                    blanks = 0;
                }

                if(j>=3){
                    for(k=1;k<4;++k){
                        if(b.board[i][j-k]==1)aiScore++;
                        else if(b.board[i][j-k]==2){aiScore=0; blanks=0;break;}
                        else blanks++;
                    }
                    moreMoves=0;
                    if(blanks>0)
                        for(int c=1;c<4;++c){
                            int column = j- c;
                            for(int m=i; m<= 5;m++){
                                if(b.board[m][column]==0)moreMoves++;
                                else break;
                            }
                        }

                    if(moreMoves!=0) score += calculateScore(aiScore, moreMoves);
                    aiScore=1;
                    blanks = 0;
                }

                if(j<=3 && i>=3){
                    for(k=1;k<4;++k){
                        if(b.board[i-k][j+k]==1)aiScore++;
                        else if(b.board[i-k][j+k]==2){aiScore=0;blanks=0;break;}
                        else blanks++;
                    }
                    moreMoves=0;
                    if(blanks>0){
                        for(int c=1;c<4;++c){
                            int column = j+c, row = i-c;
                            for(int m=row;m<=5;++m){
                                if(b.board[m][column]==0)moreMoves++;
                                else if(b.board[m][column]==1);
                                else break;
                            }
                        }
                        if(moreMoves!=0) score += calculateScore(aiScore, moreMoves);
                        aiScore=1;
                        blanks = 0;
                    }
                }

                if(i>=3 && j>=3){
                    for(k=1;k<4;++k){
                        if(b.board[i-k][j-k]==1)aiScore++;
                        else if(b.board[i-k][j-k]==2){aiScore=0;blanks=0;break;}
                        else blanks++;
                    }
                    moreMoves=0;
                    if(blanks>0){
                        for(int c=1;c<4;++c){
                            int column = j-c, row = i-c;
                            for(int m=row;m<=5;++m){
                                if(b.board[m][column]==0)moreMoves++;
                                else if(b.board[m][column]==1);
                                else break;
                            }
                        }
                        if(moreMoves!=0) score += calculateScore(aiScore, moreMoves);
                        aiScore=1;
                        blanks = 0;
                    }
                }
            }
        }
        return score;
    }

    public int minimax(int depth, int turn, int alpha, int beta){

        if(beta<=alpha){if(turn == 1) return Integer.MAX_VALUE; else return Integer.MIN_VALUE; }
        int gameResult = gameResult(b);

        if(gameResult==1)return Integer.MAX_VALUE/2;
        else if(gameResult==2)return Integer.MIN_VALUE/2;
        else if(gameResult==0)return 0;

        if(depth==maxDepth)return evaluateBoard(b);

        int maxScore=Integer.MIN_VALUE, minScore = Integer.MAX_VALUE;

        for(int j=0;j<=6;++j){

            int currentScore = 0;

            if(!b.isLegalMove(j)) continue;

            if(turn==1){
                b.placeMove(j, 1);
                currentScore = minimax(depth+1, 2, alpha, beta);

                if(depth==0){
                    //System.out.println("Score for location "+j+" = "+currentScore);
                    if(currentScore > maxScore)nextMoveLocation = j;
                    if(currentScore == Integer.MAX_VALUE/2){b.undoMove(j);break;}
                }

                maxScore = Math.max(currentScore, maxScore);

                alpha = Math.max(currentScore, alpha);
            }
            else if(turn==2){
                b.placeMove(j, 2);
                currentScore = minimax(depth+1, 1, alpha, beta);
                minScore = Math.min(currentScore, minScore);

                beta = Math.min(currentScore, beta);
            }
            b.undoMove(j);
            if(currentScore == Integer.MAX_VALUE || currentScore == Integer.MIN_VALUE) break;
        }
        return turn==1?maxScore:minScore;
    }

    public int getAIMove(){
        nextMoveLocation = -1;
        minimax(0, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
        return nextMoveLocation;
    }

    public void playAgainstAIConsole(){
        int humanMove=-1;
        Scanner scan = new Scanner(System.in);
        System.out.println("Would you like to play first? (yes/no) ");
        String answer = scan.next().trim();

        if(answer.equalsIgnoreCase("yes")) letOpponentMove();
        b.displayBoard();
        b.placeMove(3, 1);
        b.displayBoard();

        while(true){
            letOpponentMove();
            b.displayBoard();

            int gameResult = gameResult(b);
            if(gameResult==1){System.out.println("AI Wins!");break;}
            else if(gameResult==2){System.out.println("You Win!");break;}
            else if(gameResult==0){System.out.println("Draw!");break;}

            b.placeMove(getAIMove(), 1);
            b.displayBoard();
            gameResult = gameResult(b);
            if(gameResult==1){System.out.println("AI Wins!");break;}
            else if(gameResult==2){System.out.println("You Win!");break;}
            else if(gameResult==0){System.out.println("Draw!");break;}
        }

    }

    public static void main(String[] args) {
        Board b = new Board();
        ConnectFourAI ai = new ConnectFourAI(b);
        ai.playAgainstAIConsole();
    }

    public static class Board {
        byte[][] board;

        public Board(){
            board = new byte[][]{
                    {0,0,0,0,0,0,0,},
                    {0,0,0,0,0,0,0,},
                    {0,0,0,0,0,0,0,},
                    {0,0,0,0,0,0,0,},
                    {0,0,0,0,0,0,0,},
                    {0,0,0,0,0,0,0,},
            };
        }

        public int getHeight(int column) {
            int y = 5;
            while (y > 0 && board[y][column] != 0) --y;
            return 5-y;
        }

        public boolean isLegalMove(int column){
            return board[0][column]==0;
        }

        //Placing a Move on the board
        public boolean placeMove(int column, int player){
            if(!isLegalMove(column)) {System.out.println("Illegal move!"); return false;}
            for(int i=5;i>=0;--i){
                if(board[i][column] == 0) {
                    board[i][column] = (byte)player;
                    return true;
                }
            }
            return false;
        }

        public void undoMove(int column){
            for(int i=0;i<=5;++i){
                if(board[i][column] != 0) {
                    board[i][column] = 0;
                    break;
                }
            }
        }

        //Printing the board
        public void displayBoard(){
            System.out.println();
            for(int i=0;i<=5;++i){
                for(int j=0;j<=6;++j){
                    System.out.print(board[i][j]+" ");
                }
                System.out.println();
            }
            System.out.println();
        }
    }

}
