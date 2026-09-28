import java.io.RandomAccessFile;
import java.lang.invoke.VarHandle;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class SharedMemory {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: java SharedMemory <transfers> <filepath>");
            return;
        }
        int transfers = Integer.parseInt(args[0]);
        String filePath = args[1];

        ChessBoard chessBoard = new ChessBoard();
        byte[] board = new byte[64];

        chessBoard.copyBoardState(board);
        
        RandomAccessFile file = new RandomAccessFile(filePath, "rw");
        FileChannel channel = file.getChannel();

        MappedByteBuffer memory = channel.map(FileChannel.MapMode.READ_WRITE, 0, 65);

        memory.put(0, (byte) 0);
        VarHandle.fullFence();

        long startTime = System.nanoTime();
        
        for (int i = 0; i < transfers; i++) {
            
            memory.position(1);
            memory.put(board);

            VarHandle.fullFence();
            memory.put(0, (byte) 1);
            VarHandle.fullFence();

            while (memory.get(0) != (byte) 2) {
                VarHandle.fullFence(); 
                Thread.onSpinWait();
            }

            VarHandle.fullFence();
            memory.put(0, (byte) 0);
            VarHandle.fullFence();
        }
        
        long endTime = System.nanoTime();

        double milliseconds = (double) (endTime - startTime) / 1000000;
        double avg = milliseconds / transfers;
        double seconds = milliseconds / 1000;
        double throughput = transfers / seconds;

        System.out.println("{");
        System.out.println("  \"architecture\": \"SharedMemory\",");
        System.out.println("  \"transfers\": " + transfers + ",");
        System.out.println("  \"board_size\": " + board.length + ",");
        System.out.println("  \"total_time_ms\": " + milliseconds + ",");
        System.out.println("  \"average_latency_ms\": " + avg + ",");
        System.out.println("  \"throughput\": " + throughput);
        System.out.println("}");
        
        channel.close();
        file.close();
    }
}
