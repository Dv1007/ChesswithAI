import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
 
public class ChessFX extends Application {
    
    ChessBoard board = new ChessBoard();
    Button[][] buttons = new Button[8][8];
    
    int startRow = -1;
    int startCol = -1;

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
                btn.setGraphic(getPieceImage(piece));
                btn.setPrefSize(60, 60);
                buttons[row][col] = btn;
                
                int r = row;
                int c = col;
                
                btn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (startRow == -1) {
                            startRow = r;
                            startCol = c;
                        }
                        else {
                            String piece = board.getPiece(startRow, startCol);
                            boolean moved = false;

                            if (piece.equals("P") || piece.equals("p")) {
                            moved = board.pawnMove(startRow,startCol, r, c);
                            }
                            else if (piece.equals("R") || piece.equals("r")) {
                            moved = board.rookMove(startRow,startCol, r, c);
                            }
                            else if (piece.equals("B") || piece.equals("b")) {
                            moved = board.bishopMove(startRow,startCol, r, c);
                            }
                            else if (piece.equals("N") || piece.equals("n")) {
                            moved = board.knightMove(startRow,startCol, r, c);
                            }
                            else if (piece.equals("Q") || piece.equals("q")) {
                            moved = board.queenMove(startRow,startCol, r, c);
                            }
                            else if (piece.equals("K") || piece.equals("k")) {
                            moved = board.kingMove(startRow,startCol, r, c);
                            }

                            if (moved) {
                                for (int i = 0; i < 8; i++) {
                                    for (int j = 0; j < 8; j++) {
                                        buttons[i][j].setGraphic(getPieceImage(board.getPiece(i, j)));
                                    }
                                }
                            }
                            startRow = -1;
                            startCol = -1;
                        }
                    }
                });
                
                grid.add(btn, col, 7 - row);
            }
        }
        
        Scene scene = new Scene(grid);
        primaryStage.setScene(scene);
        primaryStage.show();

        }
    }
