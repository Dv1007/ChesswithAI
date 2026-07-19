import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.ChoiceDialog;
import java.util.Arrays;
import javafx.scene.control.Alert;

 public class ChessFX extends Application {
    ChessAI ai = new ChessAI();
    ChessBoard board = new ChessBoard();
    Button[][] buttons = new Button[8][8];
    int startRow = -1;
    int startCol = -1;
    double dragOffsetX = 0;
    double dragOffsetY = 0;

    public ImageView getPieceImage(String piece) {
        Image img;
        if (piece.equals("P")) {
            img = new Image("/pieces/wP.png", 50, 50, true, true);
        } 
        else if (piece.equals("R")) {
            img = new Image("/pieces/wR.png", 50, 50, true, true);
        }
        else if (piece.equals("B")) {
            img = new Image("/pieces/wB.png", 50, 50, true, true);
        }


        else if (piece.equals("N")) {
            img = new Image("/pieces/wN.png", 50, 50, true, true);
        }
        else if (piece.equals("Q")) {
            img = new Image("/pieces/wQ.png", 50, 50, true, true);
        }
        else if (piece.equals("K")) {
            img = new Image("/pieces/wK.png", 50, 50, true, true);
        }
        else if (piece.equals("p")) {
            img = new Image("/pieces/bP.png", 50, 50, true, true);
        }
        else if (piece.equals("r")) {
            img = new Image("/pieces/bR.png", 50, 50, true, true);
        }
        else if (piece.equals("b")) {
            img = new Image("/pieces/bB.png", 50, 50, true, true);
        }
        else if (piece.equals("n")) {
            img = new Image("/pieces/bN.png", 50, 50, true, true);
        }
        else if (piece.equals("q")) {
            img = new Image("/pieces/bQ.png", 50, 50, true, true);
        }
        else if (piece.equals("k")) {
            img = new Image("/pieces/bK.png", 50, 50, true, true);
        }
        else {
            return new ImageView();
        }
        return new ImageView(img);
    }
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {

    primaryStage.setTitle("Chess");

    Pane root = new Pane();
    GridPane grid = new GridPane();
    ImageView dragPiece = new ImageView();
    dragPiece.setMouseTransparent(true);
    dragPiece.setVisible(false);
    root.getChildren().addAll(grid, dragPiece);
    dragPiece.setViewOrder(-1);

    for (int row = 0; row < 8; row++) {
        for (int col = 0; col < 8; col++) {
            String piece = board.getPiece(row, col);

            Button btn = new Button();
            btn.setPrefSize(60, 60);
            btn.setMinSize(60, 60);
            btn.setMaxSize(60, 60);
            btn.setStyle("-fx-padding: 0;");
            styleSquare(btn, row, col);

            buttons[row][col] = btn;

            int r = row;
            int c = col;

            ImageView pieceImage = getPieceImage(piece);
            btn.setGraphic(pieceImage);

            btn.setOnMousePressed(e -> {
                if (board.getPiece(r, c).equals("*")) return;
                if (board.whiteTurn() && Character.isLowerCase(board.getPiece(r, c).charAt(0))) return;
                if (!board.whiteTurn() && Character.isUpperCase(board.getPiece(r, c).charAt(0))) return;

                startRow = r;
                startCol = c;

                String choosepiece = board.getPiece(r, c);
                dragPiece.setImage(getPieceImage(piece).getImage());
                dragPiece.setVisible(true);
                dragPiece.setTranslateX(e.getSceneX() - 25);
                dragPiece.setTranslateY(e.getSceneY() - 25);

                btn.setGraphic(null);
            });

            btn.setOnMouseDragged(e -> {
                if (startRow == -1) return;
                dragPiece.setTranslateX(e.getSceneX() - 25);
                dragPiece.setTranslateY(e.getSceneY() - 25);
            });

            btn.setOnMouseReleased(e -> {

                if (startRow == -1) return;

                int endCol = (int)(e.getSceneX() / 60);
                int endRow = 7 - (int)(e.getSceneY() / 60);

                if (endRow < 0 || endRow > 7 || endCol < 0 || endCol > 7) {
                    dragPiece.setVisible(false);
                    startRow = -1;
                    startCol = -1;
                    refreshBoard();
                    return;
                }

                String movingPiece = board.getPiece(startRow, startCol);
                boolean moved = false;

                Move playerMove = new Move(startRow, startCol, endRow, endCol);
                moved = board.makeMove(playerMove);

                if (moved) {
                    String endPiece = board.getPiece(endRow, endCol);
                    if (endPiece.equals("P") || endPiece.equals("p")) {
                        if (endRow == 7 || endRow == 0) {
                            ChoiceDialog<String> dialog = new ChoiceDialog<>("Queen",Arrays.asList("Queen", "Rook", "Bishop", "Knight"));
                            dialog.setTitle("Pawn Promotion");
                            dialog.setHeaderText("Choose a piece to promote");
                            dialog.setContentText("Piece:");
                            String result = dialog.showAndWait().orElse("Queen");
                            String promotePiece;
                            if (result.equals("Queen")) {
                                promotePiece = "Q";
                            }
                            else if (result.equals("Rook")) {
                                promotePiece = "R";
                            }
                            else if (result.equals("Bishop")) {
                                promotePiece = "B";
                            }
                            else {
                                promotePiece = "N";
                            }

                            if (endPiece.equals("p")) {
                                promotePiece = promotePiece.toLowerCase();
                            }
                            System.out.println(promotePiece);
                            board.pawnPromote(endRow, endCol, promotePiece);  
                        }
                    }
                    if (board.repeatDraw()) {
                        endGame("Draw by threefold repetition!");
                        return;
                    }
                    
                    if (!board.whiteTurn()) {
                        dragPiece.setVisible(false);
                        Move aiMove = ai.getBestMove(board, false, 3);
                        if (aiMove != null) {
                            board.makeMove(aiMove);
                            if (board.repeatDraw()) {
                                endGame("Draw by threefold repetition!");
                                return;
                            }
                            int aiEndRow = aiMove.getEndRow();
                            int aiEndCol = aiMove.getEndCol();
                            String promotedPiece = board.getPiece(aiEndRow, aiEndCol);

                            if (promotedPiece.equals("p") && aiEndRow == 0) {
                                board.pawnPromote(aiEndRow, aiEndCol, "Q");
                            }
                            refreshBoard();

                            if (board.isCheckmate(true)) {
                                endGame("Checkmate! AI wins");
                            }
                            else if (board.isStalemate(true)) {
                                endGame("Draw by stalemate!");
                            }
                        }
                        else {
                            if (board.isCheckmate(false)) {
                                endGame("Checkmate! You win");
                            }
                            else if (board.isStalemate(false)) {
                                endGame("Draw by stalemate!");
                            }
                        }
                    }
            }
                dragPiece.setVisible(false);
                startRow = -1;
                startCol = -1;

                dragPiece.setVisible(false);
                startRow = -1;
                startCol = -1;
                refreshBoard();
            });
            grid.add(btn, col, 7 - row);
        }
    }

    Scene scene = new Scene(root);
    primaryStage.setScene(scene);
    primaryStage.show();
}

    public void styleSquare(Button btn, int row, int col) {
    String color;

    if ((row + col) % 2 == 0) {
        color = "#b58863"; 
    }

    else {
        color = "#f0d9b5";
    }

    btn.setStyle(
    "-fx-padding: 0; " +
    "-fx-background-color: " + color + "; " +
    "-fx-background-insets: 0; " +
    "-fx-background-radius: 0;"
    );
}

    public void refreshBoard() {
    for (int row = 0; row < 8; row++) {
        for (int col = 0; col < 8; col++) {
            String piece = board.getPiece(row, col);
            if (piece.equals("*")) {
                buttons[row][col].setGraphic(null);
            } 
            else {
                buttons[row][col].setGraphic(getPieceImage(piece));
            }
        }
    }
}

    public void endGame(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
