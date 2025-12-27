public class ChessGame {
    public static void main(String[] args) {
        //board
        String[][] board = new String[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = "*";
            }
        }
    for (int j = 0; j < 8; j++) {
        //white pawns
        board[1][j] = "P";
        //black pawns
        board[6][j] = "p";
    }
    //white rooks
    board[0][0] = "R";
    board[0][7] = "R";
    //black rooks
    board[7][0] = "r";
    board[7][7] = "r";
    //Queens
    board[0][3] = "Q";
    board[7][3] = "q";
    //white bishops
    board[0][2] = "B";
    board[0][5] = "B";
    //black bishops
    board[7][2] = "b";
    board[7][5] = "b";
    //white knights
    board[0][1] = "N";
    board[0][6] = "N";
    //black knights
    board[7][1] = "n";
    board[7][6] = "n";
    //Kings
    board[0][4] = "K";
    board[7][4] = "k";
    
    printBoard(board);
    }
    public static void printBoard(String[][] board) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }
}
