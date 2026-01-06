
public class ChessBoard {
    
    private String[][] board;
    
    public ChessBoard() {
        //board
        board = new String[8][8];
        boardSetup();
    }
    public void boardSetup() {
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
    } 
    public void printBoard() {
        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }
    
    public String getPiece(int row, int col) {
        return board[row][col];
    }

    public void setPiece(int row, int col, String piece) {
        board[row][col] = piece;
    }
    //pawn logic
    public boolean pawnMove(int startRow, int startCol, int endRow, int endCol) {
        String piece = board[startRow][startCol];
        if (!piece.equals("P") && !piece.equals("p")) {
            return false; // Not a pawn
        }
        boolean legalMove = false;
        int direction;
        
        if (piece.equals("P")) {
            direction = 1;
        }
        else if (piece.equals("p")) {
            direction = -1;
        }
        else {
            direction = -1;
        }
    
        // One Square
        boolean sameColumn = endCol == startCol;
        boolean moveSquare1 = endRow == startRow + direction;
        boolean emptySquare = board[endRow][endCol].equals("*");

        if (sameColumn && moveSquare1 && emptySquare) {
            legalMove = true;
        }
        //Two Squares
        boolean moveWhite = piece.equals("P") && startRow == 1;
        boolean moveBlack = piece.equals("p") && startRow == 6;
        boolean moveSquare2 = sameColumn && ((piece.equals("P") && endRow == startRow + 2) || (piece.equals("p") && endRow == startRow - 2)); 
        boolean moveSpace = board[startRow + direction][startCol].equals("*") && board[endRow][endCol].equals("*");

        if ((moveWhite || moveBlack) && moveSquare2 && moveSpace) {
            legalMove = true;
        }

        //diagonal capture
        boolean moveDiagonal = Math.abs(endCol - startCol) == 1;
        boolean moveForward = endRow == startRow + direction;
        String loc = board[endRow][endCol];

        boolean whiteCapture = piece.equals("P") && (loc.equals("p") || loc.equals("r") || loc.equals("n") || loc.equals("b") || loc.equals("q") || loc.equals("k"));
        boolean blackCapture = piece.equals("p") && (loc.equals("P") || loc.equals("R") || loc.equals("N") || loc.equals("B") || loc.equals("Q") || loc.equals("K"));

        if (moveDiagonal && moveForward && (whiteCapture || blackCapture)) {
            legalMove = true;
        }   
        
        if (legalMove) {
            board[endRow][endCol] = piece;
            board[startRow][startCol] = "*";
        }
        return legalMove; 
    }
    //rook logic
    public boolean rookMove(int startRow, int startCol, int endRow, int endCol) {
        String piece = board[startRow][startCol];
        if (!piece.equals("R") && !piece.equals("r")) {
            return false; //not a rook
        }
        boolean sameRow = startRow == endRow;
        boolean sameColumn = startCol == endCol;

        if (!sameRow && !sameColumn) {
            return false;
        }
        int moveRow = 0;
        int moveCol = 0;
        
        if (sameRow) {
            if (startCol < endCol) {
                moveCol = 1;
            }
            else {
                moveCol = -1;
            }
        }
        else {
            if (startRow < endRow) {
                moveRow = 1;
            }
            else {
                moveRow = -1;
            }
        }
        //check row and column
        int row = startRow + moveRow;
        int col = startCol + moveCol;

        while (row != endRow || col != endCol) {
            if (!board[row][col].equals("*")) {
                return false;
            }
            row = row + moveRow;
            col = col + moveCol;
        }
        
        String loc = board[endRow][endCol];
        boolean whiteCapture = piece.equals("R") && (loc.equals("p") || loc.equals("r") || loc.equals("n") || loc.equals("b") || loc.equals("q") || loc.equals("k"));
        boolean blackCapture = piece.equals("r") && (loc.equals("P") || loc.equals("R") || loc.equals("N") || loc.equals("B") || loc.equals("Q") || loc.equals("K"));
        boolean legalMove = loc.equals("*") || whiteCapture || blackCapture;
        if (legalMove) {
            board[endRow][endCol] = piece;
            board[startRow][startCol] = "*";
        }
        return legalMove;
    }


    //bishop logic
    public boolean bishopMove(int startRow, int startCol, int endRow, int endCol) {
        String piece = board[startRow][startCol];
        if (!piece.equals("B") && !piece.equals("b")) {
            return false; //not a bishop
        } 

        if (Math.abs(endRow - startRow) != Math.abs(endCol - startCol)) {
            return false;
        }
        int moveRow;
        int moveCol;

        if (startRow < endRow) {
            moveRow = 1;
        }
        else {
            moveRow = -1;
        }
        if (startCol < endCol) {
            moveCol = 1;
        }
        else {
            moveCol = -1;
        }

        int row = startRow + moveRow;
        int col = startCol + moveCol;

        while (row != endRow && col != endCol) {
            if (!board[row][col].equals("*")) {
                return false;
            }
            row  = row + moveRow;
            col = col + moveCol;
        }

        String loc = board[endRow][endCol];
        boolean whiteCapture = piece.equals("B") && (loc.equals("p") || loc.equals("r") || loc.equals("n") || loc.equals("b") || loc.equals("q") || loc.equals("k"));
        boolean blackCapture = piece.equals("b") && (loc.equals("P") || loc.equals("R") || loc.equals("N") || loc.equals("B") || loc.equals("Q") || loc.equals("K"));
        boolean legalMove = loc.equals("*") || whiteCapture || blackCapture;
        if (legalMove) {
            board[endRow][endCol] = piece;
            board[startRow][startCol] = "*";
        }
        return legalMove;
    }

    //knight logic
    public boolean knightMove(int startRow, int startCol, int endRow, int endCol) {
        String piece = board[startRow][startCol];
        if (!piece.equals("N") && !piece.equals("n")) {
            return false; //not a knight
        } 
        int rowDiff = Math.abs(endRow -startRow);
        int colDiff = Math.abs(endCol - startCol);
        
        if ((!(rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2))) {
            return false;
        }
        String loc = board[endRow][endCol];
        boolean whiteCapture = piece.equals("N") && (loc.equals("p") || loc.equals("r") || loc.equals("n") || loc.equals("b") || loc.equals("q") || loc.equals("k"));
        boolean blackCapture = piece.equals("n") && (loc.equals("P") || loc.equals("R") || loc.equals("N") || loc.equals("B") || loc.equals("Q") || loc.equals("K"));
        boolean legalMove = loc.equals("*") || whiteCapture || blackCapture;
        if (legalMove) {
            board[endRow][endCol] = piece;
            board[startRow][startCol] = "*";
        }
        return legalMove;
    }
    //queen logic
    public boolean queenMove(int startRow, int startCol, int endRow, int endCol) {
        String piece = board[startRow][startCol];
        if (!piece.equals("Q") && !piece.equals("q")) {
            return false; //not a queen
        } 
        //combine bishop and rook logic
        boolean sameRow = startRow == endRow;
        boolean sameColumn = startCol == endCol;

        if (!sameRow && !sameColumn && Math.abs(endRow - startRow) != Math.abs(endCol - startCol)) {
            return false;
        }

        int moveRow = 0;
        int moveCol = 0;

        if (sameRow) {
            if (startCol < endCol) {
                moveCol = 1;
            }
            else {
                moveCol = -1;
            }
        }
        else if (sameColumn) {
            if (startRow < endRow) {
                moveRow = 1;
            }
            else {
                moveRow = -1;
            }
        }
        else {
            if (startRow < endRow) {
                moveRow = 1;
            }
            else {
                moveRow = -1; 
            }
            if (startCol < endCol) {
                moveCol = 1;
            }
            else {
                moveCol = -1;
            }
        }

        int row = startRow + moveRow;
        int col = startCol + moveCol;

        while (row != endRow && col != endCol) {
            if (!board[row][col].equals("*")) {
                return false;
            }
            row  = row + moveRow;
            col = col + moveCol;
        }

        String loc = board[endRow][endCol];
        boolean whiteCapture = piece.equals("Q") && (loc.equals("p") || loc.equals("r") || loc.equals("n") || loc.equals("b") || loc.equals("q") || loc.equals("k"));
        boolean blackCapture = piece.equals("q") && (loc.equals("P") || loc.equals("R") || loc.equals("N") || loc.equals("B") || loc.equals("Q") || loc.equals("K"));
        boolean legalMove = loc.equals("*") || whiteCapture || blackCapture;
        
        if (legalMove) {
            board[endRow][endCol] = piece;
            board[startRow][startCol] = "*";
        }
        return legalMove;
    }
    //king logic
    public boolean kingMove(int startRow, int startCol, int endRow, int endCol) {
        String piece = board[startRow][startCol];
        if (!piece.equals("K") && !piece.equals("k")) {
            return false; //not a king
        }
        int rowDiff = Math.abs(endRow - startRow);
        int colDiff = Math.abs(endCol - startCol);
        if (rowDiff > 1 || colDiff > 1) {
            return false;
        }

        String loc = board[endRow][endCol];
        boolean whiteCapture = piece.equals("K") && (loc.equals("p") || loc.equals("r") || loc.equals("n") || loc.equals("b") || loc.equals("q") || loc.equals("k"));
        boolean blackCapture = piece.equals("k") && (loc.equals("P") || loc.equals("R") || loc.equals("N") || loc.equals("B") || loc.equals("Q") || loc.equals("K"));
        boolean legalMove = loc.equals("*") || whiteCapture || blackCapture;
        
        if (legalMove) {
            board[endRow][endCol] = piece;
            board[startRow][startCol] = "*";
        }
        return legalMove;
    }
}
