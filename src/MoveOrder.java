import java.util.ArrayList;

public class MoveOrder {
    private static int getPieceVal(byte piece) {
        int pieceType = piece;
        
        if (pieceType < 0) {
            pieceType = -(pieceType);
        }

        if (pieceType == 1) {
            return 100;
        }

        if (pieceType == 2) {
            return 320;
        }

        if (pieceType == 3) {
            return 330;
        }

        if (pieceType == 4) {
            return 500;
        }

        if (pieceType == 5) {
            return 900;
        }

        if (pieceType == 6) {
            return 20000;
        }
        return 0;
    }

    public static int scoreMove(ChessBoard board, Move move) {
        byte attack = board.getPiece(move.getStartRow(), move.getStartCol());
        byte sacrifice = board.getPiece(move.getEndRow(), move.getEndCol());

        int score = 0;
        if (sacrifice != 0) {
            score += 10 * getPieceVal(sacrifice);
            score -= getPieceVal(attack);
        }
        return score;
    }
    
    public static void sortMoves(ChessBoard board, ArrayList<Move> moves) {
        for (int i = 0; i < moves.size() - 1; i++) {
            int bestIndex = i;
            int bestScore = scoreMove(board, moves.get(i));

            for (int j = i + 1; j < moves.size(); j++) {
                int current = scoreMove(board, moves.get(j));

                if (current > bestScore) {
                    bestScore = current;
                    bestIndex = j;
                }
            }
            Move test = moves.get(i);
            moves.set(i, moves.get(bestIndex));
            moves.set(bestIndex, test);
        }
    }
}
