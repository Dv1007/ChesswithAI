import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

 public class ChessFX extends Application {
    
    ChessBoard board = new ChessBoard();
    Button[][] buttons = new Button[8][8];
    int startRow = -1;
    int startCol = -1;
    boolean whiteTurn = true;

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
        GridPane grid = new GridPane();
        
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                String piece = board.getPiece(row, col);
                Button btn = new Button();
                btn.setPrefSize(60, 60);
                buttons[row][col] = btn;
                int r = row;
                int c = col;

                ImageView pieceImage = getPieceImage(piece);
                btn.setGraphic(pieceImage);

                btn.setOnDragDetected(new EventHandler <MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                    String p = board.getPiece(r, c);
                    if (p.equals("*")) {
                        return;
                    }

                    boolean isWhitePiece = (p.equals("P") || p.equals("R") || p.equals("N") || p.equals("B") || p.equals("Q") || p.equals("K"));
                    
                    if (whiteTurn && !isWhitePiece) {
                        return;
                    } 
                    if (!whiteTurn && isWhitePiece) {
                        return;
                    }

                    startRow = r;
                    startCol = c;
                    Dragboard db = btn.startDragAndDrop(TransferMode.MOVE);
                    db.setDragView(pieceImage.getImage());
                    ClipboardContent content = new ClipboardContent();
                    content.putString(board.getPiece(r, c));
                    db.setContent(content);
                    btn.setGraphic(null);
                    event.consume();
                }
            });

            btn.setOnDragOver(new EventHandler <DragEvent>() {
                @Override
                public void handle(DragEvent event) {
                if (event.getGestureSource() != btn && event.getDragboard().hasString())  {   
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            }
        });
        
        btn.setOnDragDropped( new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean test = false;
                
                if (db.hasString()) {
                    String movingPiece = db.getString();
                    boolean moved = false;
                    
                    if (movingPiece.equals("P") || movingPiece.equals("p"))  {
                        moved = board.pawnMove(startRow, startCol, r, c);
                    }
                    else if (movingPiece.equals("R") || movingPiece.equals("r"))  {
                        moved = board.rookMove(startRow, startCol, r, c);
                    }
                    else if (movingPiece.equals("B") || movingPiece.equals("b"))  {
                        moved = board.bishopMove(startRow, startCol, r, c);
                    }
                    else if (movingPiece.equals("N") || movingPiece.equals("n"))  {
                        moved = board.knightMove(startRow, startCol, r, c);
                    }
                    else if (movingPiece.equals("Q") || movingPiece.equals("q"))  {
                        moved = board.queenMove(startRow, startCol, r, c);
                    }
                    else if (movingPiece.equals("K") || movingPiece.equals("k"))  {
                        moved = board.kingMove(startRow, startCol, r, c);
                    }

                    if (moved) {
                        whiteTurn =!whiteTurn;
                        refreshBoard();
                        test = true;
                    }
                    if (!test) {
                        refreshBoard();
                    }
                }
                event.setDropCompleted(test);
                event.consume();
            }
        });

        grid.add(btn, col, 7 - row);
        }
        }
        
        Scene scene = new Scene(grid);
        primaryStage.setScene(scene);
        primaryStage.show();

        }

        public void refreshBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                String piece = board.getPiece(row, col);
                if (piece.equals("*")) {
                    buttons[row][col].setGraphic(null);
                } else {
                    buttons[row][col].setGraphic(getPieceImage(piece));
                }
            }
        }
    }
 }
