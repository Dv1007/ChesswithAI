
public class ChessBoard {
    
    private String[][] board;
    private boolean wKingMoved;
    private boolean bKingMoved;
    private boolean wLeftRookMoved;
    private boolean wRightRookMoved;
    private boolean bLeftRookMoved;
    private boolean bRightRookMoved;
    private int enPassantRow = -1;
    private int enPassantCol = -1;
    private boolean enPassant = false;

    
    public ChessBoard() {
        //board
        board = new String[8][8];
        wKingMoved = false;
        bKingMoved = false;
        wLeftRookMoved = false;
        wRightRookMoved = false;
        bLeftRookMoved = false;
        bRightRookMoved = false;
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
        boolean moveSquare2 = sameColumn && endRow == startRow + 2 * direction;
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
    
        if (moveDiagonal && moveForward) {
            if (whiteCapture || blackCapture) {
                legalMove = true;
            }
            if (enPassant && moveDiagonal && moveForward && endRow == enPassantRow && endCol == enPassantCol && board[endRow][endCol].equals("*")) {
                legalMove = true;

                if (piece.equals("P")) {
                    board[endRow - 1][endCol] = "*";
                }
                else {
                    board[endRow + 1][endCol] = "*";
                }
            }
        }
        
        if (legalMove) {
            if (!((piece.equals("P") && startRow == 1 && endRow == 3) && !(piece.equals("p") && startRow == 6 && endRow == 4))) {
                enPassant = false;
                enPassantRow = -1;
                enPassantCol = -1;
            }
            if (piece.equals("P") && startRow == 1 && endRow == 3) {
                enPassantRow = 2;
                enPassantCol = startCol;
                enPassant = true;
            }
            else if (piece.equals("p") && startRow == 6 && endRow == 4) {
                enPassantRow = 5;
                enPassantCol = startCol;
                enPassant = true;
            }
            
            if (leaveInCheck(startRow, startCol, endRow, endCol)) {
                return false;
            }
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
            
            if (leaveInCheck(startRow, startCol, endRow, endCol)) {
                return false;
            }
            if (piece.equals("R")) {
                if (startRow == 0 && startCol == 0) {
                    wLeftRookMoved = true;
                }
                if (startRow == 0 && startCol == 7) {
                    wRightRookMoved = true;
                }

            }
            if (piece.equals("r")) {
                if (startRow == 7 && startCol == 0) {
                    bLeftRookMoved = true;
                }
                if (startRow == 7 && startCol == 7) {
                    bRightRookMoved = true;
                }
            }
            
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

        while (row != endRow || col != endCol) {
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
            
            if (leaveInCheck(startRow, startCol, endRow, endCol)) {
                return false;
            }
            
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
        
        if (!((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2))) {
            return false;
        }
        String loc = board[endRow][endCol];
        boolean whiteCapture = piece.equals("N") && (loc.equals("p") || loc.equals("r") || loc.equals("n") || loc.equals("b") || loc.equals("q") || loc.equals("k"));
        boolean blackCapture = piece.equals("n") && (loc.equals("P") || loc.equals("R") || loc.equals("N") || loc.equals("B") || loc.equals("Q") || loc.equals("K"));
        boolean legalMove = loc.equals("*") || whiteCapture || blackCapture;
        
        if (legalMove) {
            
            if (leaveInCheck(startRow, startCol, endRow, endCol)) {
                return false;
            }
            
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

        while (row != endRow || col != endCol) {
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
            
            if (leaveInCheck(startRow, startCol, endRow, endCol)) {
                return false;
            }
            
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
        if (rowDiff == 0 && colDiff == 2) {
            if (!isCastle(startRow, startCol, endRow, endCol)) {
                return false;
            }
            board[endRow][endCol] = piece;
            board[startRow][startCol] = "*";

            if (startCol < endCol) {
                board[startRow][5] = board[startRow][7];
                board[startRow][7] = "*";
            }
            else {
                board[startRow][3] = board[startRow][0];
                board[startRow][0] = "*";
            }
    
            return true;
        }
        if (rowDiff > 1 || colDiff > 1) {
            return false;
        }

        String loc = board[endRow][endCol];
        boolean whiteCapture = piece.equals("K") && (loc.equals("p") || loc.equals("r") || loc.equals("n") || loc.equals("b") || loc.equals("q") || loc.equals("k"));
        boolean blackCapture = piece.equals("k") && (loc.equals("P") || loc.equals("R") || loc.equals("N") || loc.equals("B") || loc.equals("Q") || loc.equals("K"));
        boolean legalMove = loc.equals("*") || whiteCapture || blackCapture;
        
        if (legalMove) {
            
            if (leaveInCheck(startRow, startCol, endRow, endCol)) {
                return false;
            }
            board[endRow][endCol] = piece;
            board[startRow][startCol] = "*";

            if (piece.equals("K")) {
                wKingMoved = true;
            }
            if (piece.equals("k")) {
                bKingMoved = true;
            }
            
            enPassant = false;
            enPassantRow = -1;
            enPassantCol = -1;
        }
        return legalMove;
    }
    public boolean isCheck(boolean whiteKing) {
        int kingRow = -1;
        int kingCol = -1;
        String king;
        if (whiteKing) {
            king = "K";
        }
        else {
            king = "k";
        }
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    if (board[r][c].equals(king)) {
                        kingRow = r;
                        kingCol = c;
                    }
                }
            }
            if (kingRow == -1 || kingCol == -1) {
                return false;
            }

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                String piece = board[r][c];
                if (piece.equals("*")) {
                    continue;
                }
                boolean isWhite = piece.equals(piece.toUpperCase());
                if (whiteKing && !isWhite) {
                    if (attackKing(r, c, kingRow, kingCol)) {
                        return true;
                    }
                }
                if (!whiteKing && isWhite) {
                    if (attackKing(r, c, kingRow, kingCol)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public boolean attackKing(int startRow, int startCol, int kingRow, int kingCol) {
        String piece = board[startRow][startCol];
        if (piece.equals("P")) {
            if (kingRow == startRow + 1) {
                if (kingCol == startCol + 1 || kingCol == startCol - 1) {
                    return true;
                }
            }
        }
        else if (piece.equals("p")) {
            if (kingRow == startRow - 1) {
                if (kingCol == startCol + 1 || kingCol == startCol - 1) {
                    return true;
                }
            }
        }
        else if (piece.equals("R") || piece.equals("r") || piece.equals("B") || piece.equals("b") || piece.equals("Q") || piece.equals("q")) {
            boolean isRook = piece.equals("R") || piece.equals("r") || piece.equals("Q") || piece.equals("q");
            boolean isBishop = piece.equals("B") || piece.equals("b") || piece.equals("Q") || piece.equals("q");

            if (isRook) {
                if (startRow == kingRow || startCol == kingCol) {
                    if (startRow == kingRow) {
                        int move;
                        if (kingCol > startCol) {
                            move = 1;
                        }
                        else {
                            move = -1;
                        }
                        
                        int c = startCol + move;
                        boolean blockMove = false;

                        while (c != kingCol) {
                            if (!board[startRow][c].equals("*")) {
                                blockMove = true;
                                break;
                            }
                            c += move;
                        }
                        if (!blockMove) {
                            return true;
                        }
                    }
                    else if (startCol == kingCol) {
                        int move;
                        if (kingRow > startRow) {
                            move = 1;
                        }
                        else {
                            move = -1;    
                        }
                        int r = startRow + move;
                        boolean blockMove = false;
                        while (r != kingRow) {
                            if (!board[r][startCol].equals("*")) {
                                blockMove = true;
                                break;
                            }
                            r += move;
                        }
                        if (!blockMove) {
                            return true;
                        }
                    }
                }
            }

            if (isBishop) {
                if (Math.abs(kingRow - startRow) == Math.abs(kingCol - startCol)) {
                    int rowMove;
                    int colMove;

                    if (kingRow > startRow) {
                        rowMove = 1;
                    }
                    else {
                        rowMove = -1;
                    }

                    if (kingCol > startCol) {
                        colMove = 1;
                    }
                    else {
                        colMove = -1;
                    }

                    int r = startRow + rowMove;
                    int c = startCol + colMove;
                    boolean blockMove = false;
                    
                    while (r != kingRow) {
                        if (!board[r][c].equals("*")) {
                            blockMove = true;
                            break;
                        }
                        r += rowMove;
                        c += colMove;
                    }
                    if (!blockMove) {
                        return true;
                    }
                }
            }
        }
            if (piece.equals("N") || piece.equals("n")) {
                int rowDiff = Math.abs(kingRow - startRow);
                int colDiff = Math.abs(kingCol - startCol);
                if ((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2)) {
                    return true;
                }
            }
            if (piece.equals("K") || piece.equals("k")) {
                int rowDiff = Math.abs(kingRow - startRow);
                int colDiff = Math.abs(kingCol - startCol);
                if (rowDiff <= 1 && colDiff <= 1) {
                    return true;
                }
            }
            return false;
        }   
    public boolean leaveInCheck(int startRow, int startCol, int endRow, int endCol) {
        String piece = board[startRow][startCol];
        String temp = board[endRow][endCol];
        board[endRow][endCol] = piece;
        board[startRow][startCol] = "*";
        boolean inCheck;
        if (piece.equals(piece.toUpperCase())) {
            inCheck = isCheck(true);
        }
        else {
            inCheck = isCheck(false);
        }
        board[startRow][startCol] = piece;
        board[endRow][endCol] = temp;
        return inCheck;
    }
    public ChessBoard(ChessBoard other) {
        board = new String[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = other.board[i][j];
            }
        }
        wKingMoved = other.wKingMoved;
        bKingMoved = other.bKingMoved;
        wLeftRookMoved = other.wLeftRookMoved;
        wRightRookMoved = other.wRightRookMoved;
        bLeftRookMoved = other.bLeftRookMoved;
        bRightRookMoved = other.bRightRookMoved;
        enPassant = other.enPassant;
        enPassantRow = other.enPassantRow;
        enPassantCol = other.enPassantCol;
    }
    
    public boolean isCheckmate(boolean isWhite) {
        if(!isCheck(isWhite)) {
            return false;
        }
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                String piece = board[r][c];
                if (piece.equals("*")) {
                    continue;
                }
                
                boolean isWhitePiece = piece.equals(piece.toUpperCase());
                
                if (isWhite != isWhitePiece) {
                    continue;
                }
                
                for (int a = 0; a < 8; a++) {
                    for (int b = 0; b < 8; b++) {
                        ChessBoard testBoard = new ChessBoard(this);
                        boolean moved = false;

                        if (piece.equals("P") || piece.equals("p")) {
                            moved = testBoard.pawnMove(r, c, a, b);
                        }
                        
                        else if (piece.equals("R") || piece.equals("r")) {
                            moved = testBoard.rookMove(r, c, a, b);
                        }
                        
                        else if (piece.equals("B") || piece.equals("b")) {
                            moved = testBoard.bishopMove(r, c, a, b);
                        }
                        
                        else if (piece.equals("N") || piece.equals("n")) {
                            moved = testBoard.knightMove(r, c, a, b);
                        }
                        
                        else if (piece.equals("Q") || piece.equals("q")) {
                            moved = testBoard.queenMove(r, c, a, b);
                        }
                        
                        else if (piece.equals("K") || piece.equals("k")) {
                            moved = testBoard.kingMove(r, c, a, b);
                        }

                        if (moved && !testBoard.isCheck(isWhite)) {
                            return false;
                        }

                    }
                }
            }
        }
        return true;
    }

    public boolean isStalemate(boolean isWhite) {
        if(isCheck(isWhite)) {
            return false;
        }
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                String piece = board[r][c];
                if (piece.equals("*")) {
                    continue;
                }
                
                boolean isWhitePiece = piece.equals(piece.toUpperCase());
                
                if (isWhite != isWhitePiece) {
                    continue;
                }
                
                for (int a = 0; a < 8; a++) {
                    for (int b = 0; b < 8; b++) {
                        ChessBoard testBoard = new ChessBoard(this);
                        boolean moved = false;

                        if (piece.equals("P") || piece.equals("p")) {
                            moved = testBoard.pawnMove(r, c, a, b);
                        }
                        
                        else if (piece.equals("R") || piece.equals("r")) {
                            moved = testBoard.rookMove(r, c, a, b);
                        }
                        
                        else if (piece.equals("B") || piece.equals("b")) {
                            moved = testBoard.bishopMove(r, c, a, b);
                        }
                        
                        else if (piece.equals("N") || piece.equals("n")) {
                            moved = testBoard.knightMove(r, c, a, b);
                        }
                        
                        else if (piece.equals("Q") || piece.equals("q")) {
                            moved = testBoard.queenMove(r, c, a, b);
                        }
                        
                        else if (piece.equals("K") || piece.equals("k")) {
                            moved = testBoard.kingMove(r, c, a, b);
                        }

                        if (moved && !testBoard.isCheck(isWhite)) {
                            return false;
                        }

                    }
                }
            }
        }
        return true;
    }
    
    public boolean isCastle(int startRow, int startCol, int endRow, int endCol) {
        String piece = board[startRow][startCol];
        if (!piece.equals("K") && !piece.equals("k")) {
            return false; 
        }
        
        int rookCol;
        if (endCol > startCol) {
            rookCol = 7;
        }
        else {
            rookCol = 0;
        }
        String rook = board[startRow][rookCol];
        if (!rook.equals("R") && !rook.equals("r")) {
            return false; 
        }

        if ((piece.equals("K") && wKingMoved)) {
            return false; 
        }

        if (piece.equals("k") && bKingMoved) {
            return false;
        }

        if (isCheck(piece.equals("K"))) {
            return false;
        }

        if (piece.equals("K")) {
            if (rookCol == 0 && wLeftRookMoved) {
                return false;
            }
            if (rookCol == 7 && wRightRookMoved) {
                return false;
            }    
        }

        if (piece.equals("k")) {
            if (rookCol == 0 && bLeftRookMoved) {
                return false;
            }
            if (rookCol == 7 && bRightRookMoved) {
                return false;
            }
        }
        ChessBoard testBoard = new ChessBoard(this);
        testBoard.board[endRow][endCol] = piece;
        testBoard.board[startRow][startCol] = "*";
        if (testBoard.isCheck(piece.equals("K"))) {
            return false; 
        }
        int direction;

        if (endCol > startCol) {
            direction = 1;
        }
        else {
            direction = -1;
        }

        int middleCol = startCol + direction;
        ChessBoard middleBoard = new ChessBoard(this);
        middleBoard.board[startRow][startCol] = "*";
        middleBoard.board[startRow][middleCol] = piece;

        if (middleBoard.isCheck(piece.equals("K"))) {
            return false;
        }
        
        for (int c = startCol + direction; c != rookCol; c+= direction) {
            if (!board[startRow][c].equals("*")) {
                return false;
            }
        }
        
        int rookEndCol;

        if (endCol > startCol) {
            rookEndCol = endCol - 1;
        }
        else {
            rookEndCol = endCol + 1;
        }

        if (piece.equals("K")) {
            testBoard.board[startRow][rookEndCol] = "R";
        }
        else {
        testBoard.board[startRow][rookEndCol] = "r";
        }
        testBoard.board[startRow][startCol] = "*";
        
        if (testBoard.isCheck(piece.equals("K"))) {
            return false;
        }
        return true;
    }
    public boolean pawnPromote(int row, int col, String piecePromote) {
        String piece = board[row][col];
        if (piece.equals("*")) {
            return false;
        }
        boolean isWhite = piece.equals("P");
        boolean isBlack = piece.equals("p");
        if (!isWhite && !isBlack) {
            return false;
        }        
        if (isWhite && row != 7) {
            return false;
        }
        if (isBlack && row != 0) {
            return false;
        }
        if (isWhite) {
            if (!piecePromote.equals("R") && !piecePromote.equals("B") && !piecePromote.equals("N") && !piecePromote.equals("Q")) {
                return false;
            }
        }
        if (!isWhite) {
            if (!piecePromote.equals("R") && !piecePromote.equals("B") && !piecePromote.equals("N") && !piecePromote.equals("Q")) {
                return false;
            }
            piecePromote = piecePromote.toLowerCase();
        }
        board[row][col] = piecePromote;
        return true;
    }
}
