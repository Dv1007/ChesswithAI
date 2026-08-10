import java.io.OutputStream;
import java.net.Socket;

public class ByteSend {
    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 5000;
        
        Socket socket = new Socket(host, port);
        System.out.println("Connected to Python");
            
        ChessBoard chessBoard = new ChessBoard();
        byte[] board = chessBoard.getBoardState();

        OutputStream output = socket.getOutputStream();
        output.write(board);
        System.out.println("Sent: " + board.length);
        socket.close();
    }
}
