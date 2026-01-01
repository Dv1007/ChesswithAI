public class ChessGame {
    public static void main(String[] args) {
        //board
      ChessBoard board = new ChessBoard();
      board.printBoard();
      boolean moveW = board.pawnMove(1,3, 2, 3);
      board.printBoard();
      boolean moveB = board.pawnMove(6, 3, 4, 3 );
      board.printBoard();
      boolean moveW1 = board.bishopMove(0, 2, 3, 5);
      board.printBoard();
      boolean moveB1 = board.pawnMove(6, 7, 5, 7);
      board.printBoard();
      boolean moveW2 = board.bishopMove(3, 5, 5, 7 );
      board.printBoard();
      boolean moveB2 = board.rookMove(7, 7, 5, 7);
      board.printBoard();
    }
}