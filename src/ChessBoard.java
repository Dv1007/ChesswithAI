import java.util.ArrayList;
import java.util.HashMap;

public class ChessBoard {
    
    private byte[] board;
    private boolean wKingMoved;
    private boolean bKingMoved;
    private boolean wLeftRookMoved;
    private boolean wRightRookMoved;
    private boolean bLeftRookMoved;
    private boolean bRightRookMoved;
    private int enPassantRow = -1;
    private int enPassantCol = -1;
    private boolean enPassant = false;
    private HashMap<String, Integer> positionCount;
    private boolean whiteTurn;
    private boolean repeatDraw = false;
    private int halfCount = 0;
    private boolean fiftyMove = false;

    public ChessBoard() {
        //board
        board = new byte[64];
        wKingMoved = false;
        bKingMoved = false;
        wLeftRookMoved = false;
        wRightRookMoved = false;
        bLeftRookMoved = false;
        bRightRookMoved = false;
        positionCount = new HashMap<>();
        whiteTurn = true;
        halfCount = 0;
        boardSetup();
        setPosition();
    }

    public void boardSetup(){
        for (int i = 0; i < 64; i++) {
            board[i] = 0;
        }
        wKingMoved = false;
        bKingMoved = false;
        wLeftRookMoved = false;
        wRightRookMoved = false;
        bLeftRookMoved = false;
        bRightRookMoved = false;
        enPassant = false;
        enPassantRow = -1;
        enPassantCol = -1;
        whiteTurn = true;
        halfCount = 0;
        fiftyMove = false;
        repeatDraw = false;

        board[0] = 4;
        board[1] = 2;
        board[2] = 3;
        board[3] = 5;
        board[4] = 6;
        board[5] = 3;
        board[6] = 2;
        board[7] = 4;
        
        for (int i = 8; i < 16; i++) {
            board[i] = 1;
        }
        board[56] = -4;
        board[57] = -2;
        board[58] = -3;
        board[59] = -5;
        board[60] = -6;
        board[61] = -3;
        board[62] = -2;
        board[63] = -4;

        for (int i = 48; i < 56; i++) {
            board[i] = -1;
        }
    }

    public void printBoard() {
        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                System.out.print(board[index(i, j)] + " ");
            }
            System.out.println();
        }
    }

    public byte getPiece(int row, int col) {
        return board[index(row, col)];
    }

    public byte[] getBoardState() {
        byte[] boardState = new byte[64];
        int index = 0;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                boardState[index] = convertPiece(board[index(row, col)]);
                index++;
            }
        }
        return boardState;
    }

    public void setPiece(int row, int col, byte piece) {
        board[index(row, col)] = piece;
    }
    //pawn logic
    public boolean pawnMove(int startRow, int startCol, int endRow, int endCol) {
        if (endRow < 0 || endRow > 7 || endCol < 0 || endCol > 7) {
            return false;
        }

        byte piece = board[index(startRow, startCol)];

        if (piece != 1 && piece != -1) {
            return false; // Not a pawn
        }

        boolean legalMove = false;
        boolean enPassantCapture = false;
        int direction;
        
        if (piece == 1) {
            direction = 1;
        }
        else if (piece == -1) {
            direction = -1;
        }
        else {
            direction = -1;
        }
        // One Square
        boolean sameColumn = endCol == startCol;
        boolean moveSquare1 = endRow == startRow + direction;
        boolean emptySquare = board[index(endRow, endCol)]== 0;

        if (sameColumn && moveSquare1 && emptySquare) {
            legalMove = true;
        }
        //Two Squares
        boolean moveWhite = piece == 1 && startRow == 1;
        boolean moveBlack = piece == -1 && startRow == 6;
        boolean moveSquare2 = sameColumn && endRow == startRow + 2 * direction;
        boolean moveSpace = false;

        if ((moveWhite || moveBlack) && moveSquare2) {
            moveSpace = board[index(startRow + direction, startCol)]== 0 && board[index(endRow, endCol)]== 0;
        }
        if ((moveWhite || moveBlack) && moveSquare2 && moveSpace) {
            legalMove = true;
        }
        //diagonal capture
        boolean moveDiagonal = Math.abs(endCol - startCol) == 1;
        boolean moveForward = endRow == startRow + direction;
        byte loc = board[index(endRow, endCol)];
        boolean opponentPiece = false;

        if (piece == 1 && loc < 0) {
            opponentPiece = true;
        }

        if (piece == -1 && loc > 0) {
            opponentPiece = true;
        }
    
        if (moveDiagonal && moveForward) {
            if (opponentPiece) {
                legalMove = true;
            }

            boolean target = (piece == 1 && endRow == 5) || (piece == -1 && endRow == 2);

            if (enPassant && target && endRow == enPassantRow && endCol == enPassantCol && loc == 0) {
                legalMove = true;
                enPassantCapture = true;
            }
        }
        
        if (legalMove) {
            if (leaveInCheck(startRow, startCol, endRow, endCol)) {
                return false;
            }
            if (piece == 1 && startRow == 1 && endRow == 3) {
                enPassantRow = 2;
                enPassantCol = startCol;
                enPassant = true;
            }
            else if (piece == -1 && startRow == 6 && endRow == 4) {
                enPassantRow = 5;
                enPassantCol = startCol;
                enPassant = true;
            }
            else {
                enPassant = false;
                enPassantRow = -1;
                enPassantCol = -1;
            }

            board[index(endRow, endCol)] = piece;
            board[index(startRow, startCol)] = 0;

            if (enPassantCapture) {
                if (piece == 1) {
                    board[index(endRow - 1, endCol)] = 0;
                }
                else {
                    board[index(endRow + 1, endCol)] = 0;
                }
            }
        }
        return legalMove;
    }
    //rook logic
    public boolean rookMove(int startRow, int startCol, int endRow, int endCol) {
        if (startRow == endRow && startCol == endCol) {
            return false;
        }

        byte piece = board[index(startRow, startCol)];

        if (piece != 4 && piece != -4) {
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
            if (startCol == endCol) {
                return false;
            }
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
            if (row < 0 || row >= 8 || col < 0 || col >= 8) {
                return false;
            }
            if (board[index(row, col)] != 0) {
                return false;
            }
            row = row + moveRow;
            col = col + moveCol;
        }
        
        byte loc = board[index(endRow, endCol)];
        boolean whiteCapture = piece == 4 && loc < 0;
        boolean blackCapture = piece == -4 && loc > 0;
        boolean legalMove = loc== 0 || whiteCapture || blackCapture;
        
        if (legalMove) {
            if (leaveInCheck(startRow, startCol, endRow, endCol)) {
                return false;
            }
            if (piece == 4) {
                if (startRow == 0 && startCol == 0) {
                    wLeftRookMoved = true;
                }
                if (startRow == 0 && startCol == 7) {
                    wRightRookMoved = true;
                }

            }
            if (piece == -4) {
                if (startRow == 7 && startCol == 0) {
                    bLeftRookMoved = true;
                }
                if (startRow == 7 && startCol == 7) {
                    bRightRookMoved = true;
                }
            }
            
            board[index(endRow, endCol)] = piece;
            board[index(startRow, startCol)] = 0;

        }
        return legalMove;
    }
    //bishop logic
    public boolean bishopMove(int startRow, int startCol, int endRow, int endCol) {
        if (startRow == endRow && startCol == endCol) {
            return false;
        }

        byte piece = board[index(startRow, startCol)];

        if (piece != 3 && piece != -3) {
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
            if (board[index(row, col)] != 0) {
                return false;
            }
            row  = row + moveRow;
            col = col + moveCol;
        }

        byte loc = board[index(endRow, endCol)];
        boolean whiteCapture = piece == 3 && loc < 0;
        boolean blackCapture = piece == -3 && loc > 0;
        boolean legalMove = loc== 0 || whiteCapture || blackCapture;
        
        if (legalMove) {
            
            if (leaveInCheck(startRow, startCol, endRow, endCol)) {
                return false;
            }
            
            board[index(endRow, endCol)] = piece;
            board[index(startRow, startCol)] = 0;
        }
        return legalMove;
    }
    //knight logic
    public boolean knightMove(int startRow, int startCol, int endRow, int endCol) {
        if (startRow == endRow && startCol == endCol) {
            return false;
        }

        byte piece = board[index(startRow, startCol)];

        if (piece != 2 && piece != -2) {
            return false; //not a knight
        } 

        int rowDiff = Math.abs(endRow -startRow);
        int colDiff = Math.abs(endCol - startCol);
        
        if (!((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2))) {
            return false;
        }
        byte loc = board[index(endRow, endCol)];
        boolean whiteCapture = piece == 2 && loc < 0;
        boolean blackCapture = piece == -2 && loc > 0;
        boolean legalMove = loc== 0 || whiteCapture || blackCapture;
        
        if (legalMove) {
            if (leaveInCheck(startRow, startCol, endRow, endCol)) {
                return false;
            }
            
            board[index(endRow, endCol)] = piece;
            board[index(startRow, startCol)] = 0;

        }
        return legalMove;
    }
    //queen logic
    public boolean queenMove(int startRow, int startCol, int endRow, int endCol) {
        if (startRow == endRow && startCol == endCol) {
            return false;
        }

        byte piece = board[index(startRow, startCol)];

        if (piece != 5 && piece != -5) {
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
            else if (startCol > endCol) {
                moveCol = -1;
            }
            else {
                return false;
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
            if (board[index(row, col)] != 0) {
                return false;
            }
        
            row  = row + moveRow;
            col = col + moveCol;
        }

        byte loc = board[index(endRow, endCol)];
        boolean whiteCapture = piece == 5 && loc < 0;
        boolean blackCapture = piece == -5 && loc > 0;
        boolean legalMove = loc== 0 || whiteCapture || blackCapture;
        
        if (legalMove) {
            
            if (leaveInCheck(startRow, startCol, endRow, endCol)) {
                return false;
            }
            
            board[index(endRow, endCol)] = piece;
            board[index(startRow, startCol)] = 0;

        }
        return legalMove;
    }
    //king logic
    public boolean kingMove(int startRow, int startCol, int endRow, int endCol) {
        if (startRow == endRow && startCol == endCol) {
            return false;
        }

        byte piece = board[index(startRow, startCol)];

        if (piece != 6 && piece != -6) {
            return false; //not a king
        }

        int rowDiff = Math.abs(endRow - startRow);
        int colDiff = Math.abs(endCol - startCol);

        if (rowDiff == 0 && colDiff == 2) {
            if (!isCastle(startRow, startCol, endRow, endCol)) {
                return false;
            }

            board[index(endRow, endCol)] = piece;
            board[index(startRow, startCol)] = 0;

            if (startCol < endCol) {
                board[index(startRow, 5)] = board[index(startRow, 7)];
                board[index(startRow, 7)] = 0;
            }
            else {
                board[index(startRow, 3)] = board[index(startRow, 0)];
                board[index(startRow, 0)] = 0;
            }
            if (piece == 6) {
                wKingMoved = true;
            }
            else {
                bKingMoved = true;
            }    
            return true;
        }
        if (rowDiff > 1 || colDiff > 1) {
            return false;
        }

        byte loc = board[index(endRow, endCol)];
        boolean whiteCapture = piece == 6 && loc < 0;
        boolean blackCapture = piece == -6 && loc > 0;
        boolean legalMove = loc== 0 || whiteCapture || blackCapture;
        
        if (legalMove) {
            if (leaveInCheck(startRow, startCol, endRow, endCol)) {
                return false;
            }

            board[index(endRow, endCol)] = piece;
            board[index(startRow, startCol)] = 0;

            if (piece == 6) {
                wKingMoved = true;
            }
            if (piece == -6) {
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
        byte king;

        if (whiteKing) {
            king = 6;
        }
        else {
            king = -6;
        }
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (board[index(r, c)] == king) {
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
                byte piece = board[index(r, c)];

                if (piece == 0) {
                    continue;
                }
                boolean isWhite = piece > 0;

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
        byte piece = board[index(startRow, startCol)];

        if (piece == 1) {
            if (kingRow == startRow + 1) {
                if (kingCol == startCol + 1 || kingCol == startCol - 1) {
                    return true;
                }
            }
        }
        else if (piece == -1) {
            if (kingRow == startRow - 1) {
                if (kingCol == startCol + 1 || kingCol == startCol - 1) {
                    return true;
                }
            }
        }
        else if (piece == 4 || piece == -4 || piece == 3 || piece == -3 || piece == 5 || piece == -5) {
            boolean isRook = piece == 4 || piece == -4 || piece == 5 || piece == -5;
            boolean isBishop = piece == 3 || piece == -3 || piece == 5 || piece == -5;

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
                            if (board[index(startRow, c)] != 0) {
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
                            if (r < 0) {
                                break;
                            }
                            if (r >= 8) {
                                break;
                            }
                            if (board[index(r, startCol)] != 0) {
                                blockMove= true;
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
                    
                    while (r != kingRow && c != kingCol) {
                        if (r < 0 || r >= 8 || c < 0 || c >= 8) {
                            return false;
                        }
                        if (board[index(r, c)] != 0) {
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
        if (piece == 2 || piece == -2) {
            int rowDiff = Math.abs(kingRow - startRow);
            int colDiff = Math.abs(kingCol - startCol);

            if ((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2)) {
                return true;
            }
        }
        if (piece == 6 || piece == -6) {
            int rowDiff = Math.abs(kingRow - startRow);
            int colDiff = Math.abs(kingCol - startCol);

            if (rowDiff <= 1 && colDiff <= 1) {
                return true;
            }
        }
        return false;
    }   
    public boolean leaveInCheck(int startRow, int startCol, int endRow, int endCol) {
        byte piece = board[index(startRow, startCol)];
        byte temp = board[index(endRow, endCol)];

        board[index(endRow, endCol)] = piece;
        board[index(startRow, startCol)] = 0;
        boolean inCheck;

        if (piece > 0) {
            inCheck = isCheck(true);
        }
        else {
            inCheck = isCheck(false);
        }

        board[index(startRow, startCol)] = piece;
        board[index(endRow, endCol)] = temp;

        return inCheck;
    }
    public ChessBoard(ChessBoard other) {
        board = new byte[64];

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[index(i, j)] = other.board[index(i, j)];
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
        halfCount = other.halfCount;
        fiftyMove = other.fiftyMove;
        whiteTurn = other.whiteTurn;
        positionCount = new HashMap<>(other.positionCount);

    }
    public boolean isCheckmate(boolean isWhite) {
        if(!isCheck(isWhite)) {
            return false;
        }
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                byte piece = board[index(r, c)];
                if (piece== 0) {
                    continue;
                }
                
                boolean isWhitePiece = piece > 0;
                
                if (isWhite != isWhitePiece) {
                    continue;
                }

                for (int a = 0; a < 8; a++) {
                    for (int b = 0; b < 8; b++) {
                        ChessBoard testBoard = new ChessBoard(this);
                        boolean moved = false;

                        if (piece == 1 || piece == -1) {
                            moved = testBoard.pawnMove(r, c, a, b);
                        }

                        else if (piece == 4 || piece == -4) {
                            moved = testBoard.rookMove(r, c, a, b);
                        }

                        else if (piece == 3 || piece == -3) {
                            moved = testBoard.bishopMove(r, c, a, b);
                        }
                        
                        else if (piece == 2 || piece == -2) {
                            moved = testBoard.knightMove(r, c, a, b);
                        }
                        
                        else if (piece == 5 || piece == -5) {
                            moved = testBoard.queenMove(r, c, a, b);
                        }
                        
                        else if (piece == 6 || piece == -6) {
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

                byte piece = board[index(r, c)];

                if (piece== 0) {
                    continue;
                }
                
                boolean isWhitePiece = piece > 0;
                
                if (isWhite != isWhitePiece) {
                    continue;
                }
                
                for (int a = 0; a < 8; a++) {
                    for (int b = 0; b < 8; b++) {

                        ChessBoard testBoard = new ChessBoard(this);
                        boolean moved = false;

                        if (piece == 1 || piece == -1) {
                            moved = testBoard.pawnMove(r, c, a, b);
                        }
                        
                        else if (piece == 4 || piece == -4) {
                            moved = testBoard.rookMove(r, c, a, b);
                        }
                        
                        else if (piece == 3 || piece == -3) {
                            moved = testBoard.bishopMove(r, c, a, b);
                        }
                        
                        else if (piece == 2 || piece == -2) {
                            moved = testBoard.knightMove(r, c, a, b);
                        }
                        
                        else if (piece == 5 || piece == -5) {
                            moved = testBoard.queenMove(r, c, a, b);
                        }
                        
                        else if (piece == 6 || piece == -6) {
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
        byte piece = board[index(startRow, startCol)];

        if (piece != 6 && piece != -6) {
            return false; 
        }
        
        int rookCol;
        if (endCol > startCol) {
            rookCol = 7;
        }
        else {
            rookCol = 0;
        }

        byte rook = board[index(startRow, rookCol)];

        if (piece == 6 && rook != 4) {
            return false;
        }
        
        if (piece == -6 && rook != -4) {
            return false; 
        }

        if ((piece == 6 && wKingMoved)) {
            return false; 
        }

        if (piece == -6 && bKingMoved) {
            return false;
        }

        if (isCheck(piece == 6)) {
            return false;
        }

        if (piece == 6) {
            if (rookCol == 0 && wLeftRookMoved) {
                return false;
            }
            if (rookCol == 7 && wRightRookMoved) {
                return false;
            }    
        }

        if (piece == -6) {
            if (rookCol == 0 && bLeftRookMoved) {
                return false;
            }
            if (rookCol == 7 && bRightRookMoved) {
                return false;
            }
        }

        ChessBoard testBoard = new ChessBoard(this);
        testBoard.board[index(endRow, endCol)] = piece;
        testBoard.board[index(startRow, startCol)] = 0;

        if (testBoard.isCheck(piece == 6)) {
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
        
        if (leaveInCheck(startRow, startCol, startRow, middleCol)) {
            return false;
        }
        
        for (int c = startCol + direction; c != rookCol; c += direction) {
            if (board[index(startRow, c)] != 0) {
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

        if (piece == 6) {
            testBoard.board[index(startRow, rookEndCol)] = 4;
        }
        else { 
        testBoard.board[index(startRow, rookEndCol)] = -4;
        }

        testBoard.board[index(startRow, startCol)] = 0;
        testBoard.board[index(startRow, rookCol)] = 0;
        
        if (testBoard.isCheck(piece == 6)) {
            return false;
        }
        return true;
    }
    public boolean pawnPromote(int row, int col, byte piecePromote) {
        byte piece = board[index(row, col)];

        if (piece== 0) {
            return false;
        }

        boolean isWhite = piece == 1;
        boolean isBlack = piece == -1;

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
            if (piecePromote != 4 && piecePromote != 3 && piecePromote != 2 && piecePromote != 5) {
                return false;
            }
        }
        if (!isWhite) {
            if (piecePromote != 4 && piecePromote != 3 && piecePromote != 2 && piecePromote != 5) {
                return false;
            }
            if (piece < 0) {
                piecePromote = (byte) - Math.abs(piecePromote);
            }
            else {
                piecePromote = (byte)Math.abs(piecePromote);
            }
        }

        board[index(row, col)] = piecePromote;
        return true;
    }
    public boolean makeMove(Move move) {
        byte piece = board[index(move.getStartRow(), move.getStartCol())];
        
        if (piece == 0) {
            return false;
        }

        if (whiteTurn && piece < 0) {
            return false;
        }

        if (!whiteTurn && piece > 0) {
            return false;
        }

        boolean capture = board[index(move.getEndRow(), move.getEndCol())] != 0 || ((piece == 1 || piece == -1) && enPassant && move.getEndRow() == enPassantRow && move.getEndCol() == enPassantCol);
        boolean pawnMove = piece == 1 || piece == -1;
        boolean moved = false;

        if (piece == 1 || piece == -1) {
            moved = pawnMove(move.getStartRow(), move.getStartCol(), move.getEndRow(), move.getEndCol());
        }
        
        else if (piece == 4 || piece == -4) {
            moved = rookMove(move.getStartRow(), move.getStartCol(), move.getEndRow(), move.getEndCol());
        }
        
        else if (piece == 3 || piece == -3) {
            moved = bishopMove(move.getStartRow(), move.getStartCol(), move.getEndRow(), move.getEndCol());
        }

        else if (piece == 2 || piece == -2) {
            moved = knightMove(move.getStartRow(), move.getStartCol(), move.getEndRow(), move.getEndCol());
        }

        else if (piece == 5 || piece == -5) {
            moved = queenMove(move.getStartRow(), move.getStartCol(), move.getEndRow(), move.getEndCol());
        }
        else if (piece == 6 || piece == -6) {
            moved = kingMove(move.getStartRow(), move.getStartCol(), move.getEndRow(), move.getEndCol());
        }

        if (moved) {
            if (capture || pawnMove) {
                halfCount = 0;
            }
            else {
                halfCount++;
            }
            if (halfCount >= 100) {
                fiftyMove = true;
            }

            flipTurn();

            if (positionCount != null) {
                threefoldRepetition();    
            }
        }
        return moved;
    }

    public ArrayList<Move> generateMoves(boolean isWhite) {
        ArrayList<Move> legalMoves = new ArrayList<>();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                byte piece = board[index(r, c)];

                if (piece != 0) {
                    boolean isWhitePiece = piece > 0;

                    if (isWhitePiece == isWhite) {
                        for (int a = 0; a < 8; a++) {
                            for (int b = 0; b < 8; b++) {
                                if (r != a || c != b) {
                                    ChessBoard test = new ChessBoard(this);
                                    Move move = new Move(r, c, a, b);
                                
                                    if (test.makeMove(move)) {
                                        legalMoves.add(move);
                                    }
                                }
                            }
                        }
                    }
                }
            }  
        }
        return legalMoves;
    }   
    public int evaluateBoard() {
        if (isCheckmate(true)) {
            return -100000;    
        }

        if (isCheckmate(false)) {
            return 100000;
        }

        int score = 0;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                byte piece = board[index(r, c)];
                if (piece == 1) {
                    score += 100;
                }

                else if (piece == 2) {
                    score += 300;
                }

                else if (piece == 3) {
                    score += 300;
                }

                else if (piece == 4) {
                    score += 500;
                }

                else if (piece == 5) {
                    score += 900;
                }

                else if (piece == -1) {
                    score -= 100;
                }

                else if (piece == -2) {
                    score -= 300;
                }

                else if (piece == -3) {
                    score -= 300; 
                }

                else if (piece == -4) {
                    score -= 500;
                }

                else if (piece == -5) {
                    score -= 900;
                }
            }
        }
        return score;
    }
    public String getPosition() {
        String pos = "";
        
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                pos += board[index(r, c)];
            }
        }

        pos += wKingMoved;
        pos += bKingMoved;
        pos += wLeftRookMoved;
        pos += wRightRookMoved;
        pos += bLeftRookMoved;
        pos += bRightRookMoved;
        pos += enPassant;
        pos += enPassantRow;
        pos += enPassantCol;
        pos += whiteTurn;

        return pos;
    }

    public void setPosition() {
        positionCount.put(getPosition(), 1);
    }

    public boolean threefoldRepetition() {
        String pos = getPosition();
        Integer count = positionCount.get(pos);

        if (count == null) {
            positionCount.put(pos, 1);
            return false;
        }

        else {
            count++;
            positionCount.put(pos, count);

            if (count >= 3) {
                repeatDraw = true;
            }
        }
        return repeatDraw;
    }

    public boolean repeatDraw() {
        return repeatDraw;
    }

    public boolean whiteTurn() {
        return whiteTurn;
    }

    public void flipTurn() {
        whiteTurn = !whiteTurn;
    }

    public boolean fiftyMove() {
        return fiftyMove;
    }

    public boolean insufficientMaterial() {
        int wBishops = 0;
        int bBishops = 0;
        int wKnights = 0;
        int bKnights = 0;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                byte piece = board[index(r, c)];

                if (piece != 0) {
                    if (piece == 1 || piece == 4 || piece == 5 || piece == -1 || piece == -5 || piece == -4) {
                        return false;
                    }
            
                    if (piece == 3) {
                        wBishops++;
                    }
                    
                    else if (piece == -3) {
                        bBishops++;
                    }
                    
                    else if (piece == 2) {
                        wKnights++;
                    }
                    
                    else if (piece == -2) {
                        bKnights++;
                    }
                }
            }
        }

        int total = wBishops + bBishops + wKnights + bKnights;
        
        if (total == 0) {
            return true;
        }

        if (total == 1) {
            return true;
        }
        return false;
    }

    public boolean isDraw() {
        if (isCheckmate(whiteTurn)) {
            return false;
        }
        return repeatDraw || fiftyMove || insufficientMaterial() || isStalemate(whiteTurn); 
    }

    private byte convertPiece(byte piece) {
        return piece;
    }

    private int index(int row, int col) {
        return row * 8 + col;
    }
}
