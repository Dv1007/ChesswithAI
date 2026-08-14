import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ByteSend {
    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 5000;
        
        try {
            Socket socket = new Socket(host, port);
            System.out.println("Connected to Python");   
            
            ChessBoard chessBoard = new ChessBoard();
            byte[] board = chessBoard.getBoardState();

            OutputStream output = socket.getOutputStream();
            InputStream input = socket.getInputStream();

            long startTime = System.nanoTime();
            int transfers = 100;
            for (int i = 0; i < transfers; i++) {
                System.out.println("Board length: " + board.length);
                output.write(board);
                output.flush();
                System.out.println("Sent: " + board.length);

                byte[] received =  new byte[2];
                for (int j = 0; j < received.length; j++) {
                    int val = input.read();
                    if (val == -1) {
                        throw new Exception("Connection has closed before receiving OK");
                    }
                    received[j] = (byte) val;
                }
                String response = new String(received);

                if (!response.equals("OK")) {
                    throw new Exception("Invalid response: " + response);
                }
            }
            long endTime = System.nanoTime();

            double milliseconds = (double) (endTime - startTime) / 1000000;
            double avg = milliseconds / transfers;
            double seconds = milliseconds / 1000;
            double throughput = transfers / seconds;
            
            System.out.println("Total Time: " + milliseconds + " ms");
            System.out.println("Average Time: " + avg + " ms");
            System.out.println("Throughput: " + throughput + " transfers/sec");
            
            socket.close();

        } catch (Exception e) {
        System.out.println("Could not connect to Python: " + e.getMessage());
        }
    }
}
