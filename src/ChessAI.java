import java.util.ArrayList;
public class ChessAI {
    private int nodes;
    public Move getBestMove(ChessBoard board, boolean isWhite, int depth) {
        nodes = 0;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        Move bestMove = null;
        
        ArrayList<Move> moves = board.generateMoves(isWhite);
        MoveOrder.sortMoves(board, moves);
        
        for (Move move : moves) {
            ChessBoard copy = new ChessBoard(board);
            copy.makeMove(move);
            
            int score = minimax(copy, depth - 1, alpha, beta, !isWhite);
            if (bestMove == null) {
                bestMove = move;
            }
            if (isWhite) {
                if (score > alpha) {
                    alpha = score;
                    bestMove = move;
                }
            }
            else {
                if (score < beta) {
                    beta = score;
                    bestMove = move;
                }
            }
        }
        return bestMove;
    }
    private int minimax(ChessBoard board, int depth, int alpha, int beta, boolean maximizingPlayer) {
        nodes++;
        if (depth <= 0 || terminalNode(board)) {
            return board.evaluateBoard();
        }
        ArrayList<Move> moves = board.generateMoves(maximizingPlayer);
        MoveOrder.sortMoves(board, moves);

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move: moves) {
                ChessBoard copy = new ChessBoard(board);
                copy.makeMove(move);
                int eval = minimax(copy, depth - 1, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (alpha >= beta) {
                    break;
                }
            }
            return maxEval;
        }
        else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : moves) {
                ChessBoard copy = new ChessBoard(board);
                copy.makeMove(move);
                
                int eval = minimax(copy, depth - 1, alpha, beta, true);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (alpha >= beta) {
                    break;
                }
            }
            return minEval;
        }
    }
    private boolean terminalNode(ChessBoard board) {
        return board.isCheckmate(true) || board.isCheckmate(false) || board.isDraw();
    }
    public int getNodes() {
        return nodes;
    }
}
