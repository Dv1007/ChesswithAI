import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class SharedMemory {
    public static void main(String[] args) throws Exception {
        int transfers = Integer.parseInt(args[0]);

        ChessBoard chessBoard = new ChessBoard();
        byte[] board = chessBoard.getBoardState();

        File javaReady = new File("../java_ready.flag");
        File pythonReady = new File("../python_ready.flag");

        javaReady.delete();
        pythonReady.delete();
        
        RandomAccessFile file = new RandomAccessFile("../shared_memory.bin", "rw");
        FileChannel channel = file.getChannel();

        MappedByteBuffer memory = channel.map(FileChannel.MapMode.READ_WRITE, 0, 64);

        long startTime = System.nanoTime();
        
        for (int i = 0; i < transfers; i++) {
            memory.position(0);
            memory.put(board);

            javaReady.delete();
            javaReady.createNewFile();

            while (!pythonReady.exists()) {
                Thread.sleep(1);
            }
            pythonReady.delete();
            javaReady.delete();
        }
        System.out.println("Loop finished");
        
        long endTime = System.nanoTime();

        double milliseconds = (double) (endTime - startTime) / 1000000;
        double avg = milliseconds / transfers;

        System.out.println("Successfully completed " + transfers + " transfers");
        System.out.println("Board size: " + board.length + " bytes");
        System.out.println("Total Time: " + milliseconds + " ms");
        System.out.println("Average Time: " + avg + " ms");

        channel.close();
        file.close();
    }
}
