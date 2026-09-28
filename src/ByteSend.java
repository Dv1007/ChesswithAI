import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ByteSend {
    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 5000;
        
        Socket socket = new Socket(host, port);

        socket.setTcpNoDelay(true);
        
        ChessBoard chessBoard = new ChessBoard();
        byte[] board = chessBoard.getBoardState();

        OutputStream output = socket.getOutputStream();
        InputStream input = socket.getInputStream();

        long startTime = System.nanoTime();
        int transfers = Integer.parseInt(args[0]);
        for (int i = 0; i < transfers; i++) {
            output.write(board);
            output.flush();

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
        
        System.out.println("{");
        System.out.println("  \"architecture\": \"TCP-IPC\",");
        System.out.println("  \"transfers\": " + transfers + ",");
        System.out.println("  \"board_size\": " + board.length + ",");
        System.out.println("  \"total_time_ms\": " + milliseconds + ",");
        System.out.println("  \"average_latency_ms\": " + avg + ",");
        System.out.println("  \"throughput\": " + throughput);
        System.out.println("}");

        socket.close();
    }
}
