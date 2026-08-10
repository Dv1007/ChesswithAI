import java.util.ArrayList;

public class TestAI {

    public static void main(String[] args) {
        ChessBoard board = new ChessBoard();

        ChessAI whiteAI = new ChessAI();
        ChessAI blackAI = new ChessAI();

        boolean whiteTurn = true;
        int moveNum = 1;

        while (true) {
            if (moveNum > 100) {
                System.out.println("Game reached 100 moves. Stopping the program");
                    break;
            }

            if (board.isCheckmate(whiteTurn)) {
                if (whiteTurn) {
                    System.out.println("Checkmate! Black wins");
                }
                else {
                    System.out.println("Checkmate! White wins");
                }
                break;
            }

            if (board.isDraw()) {
                System.out.println("Draw!");
                break;
            }
            ChessAI ai;

            if (whiteTurn) {
                ai = whiteAI;
            }
            else {
                ai = blackAI;
            }
            Move move = ai.getBestMove(board, whiteTurn, 3);

            if (move == null) {
                System.out.println("ERROR: AI returned null");
                break;
            }
            ArrayList<Move> legalMoves = board.generateMoves(whiteTurn);
            
            boolean legal = false;
            for (Move legalMove : legalMoves) {

                if (legalMove.getStartRow() == move.getStartRow() && legalMove.getStartCol() == move.getStartCol() && legalMove.getEndRow() == move.getEndRow() && legalMove.getEndCol() == move.getEndCol()) {
                    legal = true;
                    break;
                }
            }

            if (!legal) {
                System.out.println("ERROR: AI played an illegal move");
                break;
            }
            System.out.println("Move " + moveNum + ": " + move.getStartRow() + "," + move.getStartCol() + " -> " + move.getEndRow() + "," + move.getEndCol());
            board.makeMove(move);

            if (!whiteTurn) {
                moveNum++;
            }

            whiteTurn = !whiteTurn;
        }
    }
}
