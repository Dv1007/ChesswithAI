import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class SharedMemory {
    public static void main(String[] args) throws Exception {
        ChessBoard chessBoard = new ChessBoard();
        byte[] board = chessBoard.getBoardState();

        File javaReady = new File("java_ready.flag");
        File pythonReady = new File("python_ready.flag");

        javaReady.delete();
        pythonReady.delete();
        
        RandomAccessFile file = new RandomAccessFile("shared_memory.bin", "rw");
        FileChannel channel = file.getChannel();

        MappedByteBuffer memory = channel.map(FileChannel.MapMode.READ_WRITE, 0, 64);

        long startTime = System.nanoTime();

        for (int i = 0; i < 100; i++) {
            memory.position(0);
            memory.put(board);
            memory.force();

            javaReady.createNewFile();

            while (!pythonReady.exists()) {
                Thread.sleep(10);
        }
        pythonReady.delete();
        javaReady.delete();
    }
    long endTime = System.nanoTime();

    double milliseconds = (double) (endTime - startTime) / 1000000;
    double avg = milliseconds / 100;

    System.out.println("Successfully completed 100 transfers");
    System.out.println("Board size: " + board.length + " bytes");
    System.out.println("Total Time: " + milliseconds + " ms");
    System.out.println("Average Time: " + avg + " ms");

    channel.close();
    file.close();
    }
}
