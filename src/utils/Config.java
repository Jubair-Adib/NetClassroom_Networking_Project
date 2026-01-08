package utils;

public class Config {

    // Port on which the TCP server will listen
    public static final int PORT = 8080;

    // Buffer size for reading data from TCP stream
    public static final int BUFFER_SIZE = 4096;

    // Socket read timeout (0 means block until data arrives)
    public static final int SOCKET_READ_TIMEOUT_MS = 0;
}
