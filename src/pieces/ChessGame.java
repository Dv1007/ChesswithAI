public class ChessGame {
    public static void main(String[] args) {
        //board
        ChessBoard board = new ChessBoard();
      board.printBoard();
      boolean moveW = board.pawnMove(1, 4, 3, 4);
      board.printBoard();
      boolean moveB = board.pawnMove(6, 4, 5, 4);
      board.printBoard();
      boolean moveW1 = board.bishopMove(0, 5, 1, 4 );
      board.printBoard();
      boolean moveB1 = board.knightMove(7, 1, 5, 2);
      board.printBoard();
      boolean moveW2 = board.knightMove(0, 1, 2, 2);
      board.printBoard();
      boolean moveB2 = board.queenMove(7, 3, 5, 5);
      board.printBoard();
      boolean moveW3 = board.kingMove(0, 4, 0, 5);
      board.printBoard();
    }
}