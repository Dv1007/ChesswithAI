public interface Communication {
    void connect() throws Exception;
    void sendBoard(byte[] board) throws Exception;
    byte[] receiveResponse() throws Exception;
    void close() throws Exception;
}
