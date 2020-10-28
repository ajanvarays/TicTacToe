import java.util.Scanner;
import java.lang.Math; 

/**
 * @author      Camilla Yli-Sissala
 * @version     2019.1212                  
 * @since       1.8                        
 */


public class TicTacToe {
    /**
     * Controls functions and the order they're run.
     *
     * @param  args  Command line parameters. Not used.
     */

    public static void main(String [] args) {
        boolean gameOn = true;
        boolean turn = true;

        System.out.println("Welcome to play tic-tac-toe!");

        String [][] gameBoard = Board.create();
        int winAmount = Board.getWinAmount(gameBoard);

        while (gameOn) {
            if (turn == true) {
                Board.print(gameBoard);
                gameBoard = Play.playerMove(gameBoard);
                turn = false;
            } else {
                gameBoard = Play.computerMove(gameBoard);
                turn = true;  
            }
                gameOn = Play.gameOver(gameBoard, winAmount);
        }
    }
}

class Board {

    /**
     * Creates the gameboard that contains numbers.
     *
     * User defines the size of the 2D-array that operates as a gameboard.
     * Only numeric values bigger or equal to 3 are accepted as an input.
     * Every slot has unique number, like "[ 1 ]".
     *
     * @return modified version of 2D-array gameBoard
     */
    public static String [][] create() {
        
        Scanner input = new Scanner(System.in);
        String what = "width";
        int x = 0;
        int y = 0;
        boolean gotX = false;
        boolean gotY = false;

        // while loop continues until user gives valid values to the x and y
        while (!gotX || !gotY) {
            try {
                System.out.println("Give the " + what + " of the game board: ");
                int temp = Integer.parseInt(input.nextLine());

                if (temp >= 3 && !gotX) {
                    x = temp;
                    gotX = true;
                    what = "height";
                } else if (temp >= 3 && !gotY) {
                    y = temp;
                    gotY = true;
                } else {
                    System.out.println("The " + what + " must be at least 3.");
                }
            } catch (Exception e) {System.out.println("Something went wrong, lets try again!");}
        }

        String [][] gameBoard = new String [y][x];
        String spot = " 1 ";
        int spotNum = 1;

        // creates 2D-array that contains numbers
        for (int i=0; i<y; i++) {
            for (int j=0; j<x; j++) {
                gameBoard[i][j] = "[" + spot + "]";
                spotNum++;

                if (spotNum<10) {
                    spot = " " + String.valueOf(spotNum) + " ";
                } else if (spotNum<100) {
                    spot = " " + String.valueOf(spotNum);
                } else {
                    spot = String.valueOf(spotNum);
                }
            }
        }

        return gameBoard;
    }

    /**
     * Gets a winning condition from the user.
     *
     * Integer winAmount is needed when checking winning. It defines how many characters need to be in a row in order for a player to win.
     * Method sets the minimum and maximum values for the value and then user has to give valid input (integer between min and max).
     *
     * @param  gameBoard  2D-array that contains the moves and empty slots
     * @return winning condition as an integer
     */
    public static int getWinAmount(String [][] gameBoard) {
        Scanner input = new Scanner(System.in);
        boolean validInput = false;
        int chosenWinAmount = 0;
        int min = 3;
        int max = 3;

        if (gameBoard.length>=10 && gameBoard[0].length>=10) {
            min = 5;
        }
        if (gameBoard.length<=gameBoard[0].length) {
            max = gameBoard.length;
        } else {
            max = gameBoard[0].length;
        }

        while (!validInput) {
            try {
                System.out.println("How many characters for a win (min " + min + ", max " + max + "): ");
                String temp = input.nextLine();
                chosenWinAmount = Integer.parseInt(temp);

                if (chosenWinAmount >= min && chosenWinAmount <= max) {
                    validInput = true;
                } else {
                    System.out.println("Invalid input, try again.");
                }
            } catch (Exception e) {System.out.println("Something went wrong, try again!");}
        }

        return chosenWinAmount;

    }

    /**
     * Prints the 2D-array that works as a gameboard.
     *
     * @param  gameBoard  2D-array that contains the moves and empty slots
     */
    public static void print(String [][] gameBoard) {
        // just basic 2D-array printing
        for (String [] row : gameBoard) {
            for (String slot : row) {
                System.out.print(slot);
            }
            System.out.println();
        }
    }
}

class Play {

    /**
     * Handles the user input and places "0" to a desired slot.
     *
     * Only numeric values that are not already replaced are accepted. 2D-array is temporarily trimmed and compared
     * to the input. If match is not found, program asks for valid input. When the match is found, the number is
     * replaced with "[ 0 ]".
     *
     * @param  gameBoard  2D-array that contains the moves and empty slots
     * @return modified version of 2D-array gameBoard
     */
    public static String [][] playerMove(String [][] gameBoard) {
        Scanner input = new Scanner(System.in);
        boolean validInput = false;

        System.out.println("Your turn to move, choose your spot by giving the number displayed there.");
        while (!validInput) {
            try {
                String chosenSpot = input.nextLine();
                String trimmed = "";
                boolean found = false;

                for (int i=0; i<gameBoard.length; i++) {
                    for (int j=0; j<gameBoard[i].length; j++) {
                        trimmed = gameBoard[i][j].replaceAll("[^a-zA-Z0-9]", "");

                        if (trimmed.equals(chosenSpot)) {
                            gameBoard[i][j] = "[ 0 ]";
                            found = true;
                            validInput = true;
                        }
                    }
                }
                if (found == false) {
                    System.out.println("Invalid input, try again.");
                }
            } catch (Exception e) {}
        }

        return gameBoard;
    }

    /**
     * Tries to stop user from winning the game. Works as a second player.
     *
     * By scanning the 2D-array in every possible way computer can determine if there's any way user could be winning. It also
     * notices its own characters and can actually try to win. If there's no way to win or get closer to winning, random place is
     * selected.
     *
     * @param  gameBoard  2D-array that contains the moves and empty slots
     * @return modified version of 2D-array gameBoard
     */
    public static String [][] computerMove(String [][] gameBoard) {

        // if player is winning, computer places "X" to prevent it
        // if winning is not a possibility, random place will be selected
        
        boolean moved = false;
        
        // values for horizontal right-to-left check
        int row = 0;
        int col = -1;
        int moveRow = 0;
        int moveCol = -2;

        for (int rounds=0; rounds<8; rounds++) {
            if (rounds == 1) {
                // changes for left-to-right horizontal check
                col = 1;
                moveCol = 2;
            } else if (rounds == 2) {
                // changes for left-to-right up-to-down diagonal check
                row = 1;
                moveRow = 2;
            } else if (rounds == 3) {
                // changes for right-to-left up-to-down diagonal check
                col = -1;
                moveCol = -2;
            } else if (rounds == 4) {
                // changes for up-to-down vertical check
                col = 0;
                moveCol = 0;
            } else if (rounds == 5) {
                // changes for down-to-up vertical check
                row = -1;
                moveRow = -2;
            } else if (rounds == 6) {
                // changes for left-to-right down-to-up diagonal check
                col = -1;
                moveCol = -2;
            } else if (rounds == 7) {
                // changes for right-to-left down-to-up diagonal check
                col = 1;
                moveCol = 2;
            }

            for (int i=0; i<gameBoard.length; i++) {
                for (int j=0; j<gameBoard[0].length; j++) {
                    try {
                        if (!moved && gameBoard[i][j].equals(gameBoard[i+row][j+col]) && !gameBoard[i+moveRow][j+moveCol].equals("[ 0 ]") && !gameBoard[i+moveRow][j+moveCol].equals("[ X ]")) {
                            gameBoard[i+moveRow][j+moveCol] = "[ X ]";
                            moved = true;
                        }
                    } catch (Exception e) {}
                    
                    try {
                        if (!moved && gameBoard[i][j].equals(gameBoard[i+moveRow][j+moveCol]) && !gameBoard[i+row][j+col].equals("[ 0 ]") && !gameBoard[i+row][j+col].equals("[ X ]")) {
                            gameBoard[i+row][j+col] = "[ X ]";
                            moved = true;
                        }
                    } catch (Exception e) {}
                }
            }
        }
        // random place
        while (!moved) {
            int x = (int) (Math.random() * gameBoard.length);
            int y = (int) (Math.random() * gameBoard[0].length);

            if (!gameBoard[x][y].equals("[ X ]") && !gameBoard[x][y].equals("[ 0 ]")) {
                gameBoard[x][y] = "[ X ]";
                moved = true;
            }
        }

        return gameBoard;
    }

    /**
     * Checks if user or computer has won the game.
     *
     * If there is enough characters in a row, winner will be printed and game ends.
     * Game ends into a draw, if the array has no more empty slots.
     *
     * @param  gb  2D-array that contains the moves and empty slots
     * @param winAmount integer which defines how many characters need to be in a row in order to win
     * @return boolean that defines if the game continues or not
     */
    public static boolean gameOver (String [][] gb, int winAmount) {
        // checks if player wins the game
        // horizontal & vertical & diagonal check
        boolean gameOn = true;
        int filled = 0;
        int match = 0;
        char winner = ' ';

        int row = 0;
        int col = 1;
        int addRow = 0;
        int addCol = 1;
        for (int rounds=0; rounds<4; rounds++) {
            if (rounds == 0) {
                // changes for up-to-down horizontal check
                row = 0;
                col = 1;
                addRow = 0;
                addCol = 1;
            } else if (rounds == 1) {
                // changes for left-to-right diagonal check
                row = 1;
                col = 1;
                addRow = 1;
                addCol = 1;
            } else if (rounds == 2) {
                // changes for right-to-left diagonal check
                row = 1;
                col = -1;
                addRow = 1;
                addCol = -1;
            } else if (rounds == 3) {
                // changes for left-to-right vertical check
                row = 1;
                col = 0;
                addRow = 1;
                addCol = 0;
            }

            for (int i=0; i<gb.length; i++) {
                for (int j=0; j<gb[0].length; j++) {
                    for (int k=0; k<=winAmount; k++) {
                        try {
                            if (gb[i][j].equals(gb[i+row][j+col])) {
                                row = row+addRow;
                                col = col+addCol;
                                match++;
                            }
                        } catch (Exception e) {}
                    }
                    if (match>=(winAmount-1)) {
                        winner = gb[i][j].charAt(2);
                    }
                    if (rounds == 0) {
                        // changes for up-to-down horizontal check
                        row = 0;
                        col = 1;
                        addRow = 0;
                        addCol = 1;
                    } else if (rounds == 1) {
                        // changes for left-to-right diagonal check
                        row = 1;
                        col = 1;
                        addRow = 1;
                        addCol = 1;
                    } else if (rounds == 2) {
                        // changes for right-to-left diagonal check
                        row = 1;
                        col = -1;
                        addRow = 1;
                        addCol = -1;
                    } else if (rounds == 3) {
                        // changes for left-to-right vertical check
                        row = 1;
                        col = 0;
                        addRow = 1;
                        addCol = 0;
                    }
                    match = 0; 
                }
            }
        }
        for (int i=0; i<gb.length; i++) {
            for (int j=0; j<gb[0].length; j++) {
                if (gb[i][j].equals("[ X ]") || gb[i][j].equals("[ 0 ]")) {
                    filled++;
                }
            }
        }

        if (winner == 'X') {
            Board.print(gb);
            System.out.println("Computer won this time :(");
            gameOn = false;
        } else if (winner == '0') {
            Board.print(gb);
            System.out.println("You won, gongratulations!");
            gameOn = false;
        } else if (filled == (gb.length * gb[0].length)) {
            Board.print(gb);
            System.out.println("It's a draw!");
            gameOn = false;
        }

        return gameOn;
    }
}