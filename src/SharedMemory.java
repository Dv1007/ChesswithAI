import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class SharedMemory {
    public static void main(String[] args) throws Exception {
        int transfers = Integer.parseInt(args[0]);

        ChessBoard chessBoard = new ChessBoard();
        byte[] board = chessBoard.getBoardState();
        
        RandomAccessFile file = new RandomAccessFile("../shared_memory.bin", "rw");
        FileChannel channel = file.getChannel();

        MappedByteBuffer memory = channel.map(FileChannel.MapMode.READ_WRITE, 0, 65);

        memory.put(0, (byte) 0);

        long startTime = System.nanoTime();
        
        for (int i = 0; i < transfers; i++) {
            memory.position(1);
            memory.put(board);

            memory.put(0, (byte) 1);

            while (memory.get(0) != 2) {
                Thread.sleep(0, 100000);
            }
            memory.put(0, (byte) 0);
        }
        
        long endTime = System.nanoTime();

        double milliseconds = (double) (endTime - startTime) / 1000000;
        double avg = milliseconds / transfers;
        double seconds = milliseconds / 1000;
        double throughput = transfers / seconds;

        System.out.println("Architecture: Shared memory");
        System.out.println("Transfers: " + transfers);
        System.out.println("Board size: " + board.length);
        System.out.println("Total Time: " + milliseconds + " ms");
        System.out.println("Average Latency: " + avg + " ms");
        System.out.println("Throughput: " + throughput + " transfers/sec");

        channel.close();
        file.close();
    }
}
