import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ByteSend {
    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 5000;
        
        try {
            Socket socket = new Socket("localhost", 5000);
            System.out.println("Connected to Python");   
            
            ChessBoard chessBoard = new ChessBoard();
            byte[] board = chessBoard.getBoardState();

            OutputStream output = socket.getOutputStream();
            InputStream input = socket.getInputStream();

            long startTime = System.nanoTime();
            for (int i = 0; i < 100; i++) {
                System.out.println("Board length: " + board.length);
                output.write(board);
                output.flush();
                System.out.println("Sent: " + board.length);

                byte[] received =  new byte[2];
                input.read(received);
                System.out.println(new String(received));    
            }
            long endTime = System.nanoTime();

            double milliseconds = (double) (endTime - startTime) / 1000000;
            double avg = milliseconds / 100;
            
            System.out.println("Total Time: " + milliseconds + " ms");
            System.out.println("Average Time: " + avg + " ms");
            
            socket.close();

        } catch (Exception e) {
        System.out.println("Could not connect to Python: " + e.getMessage());
        }
    }
}
