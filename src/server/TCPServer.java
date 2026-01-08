package server;

import java.net.ServerSocket;
import java.net.Socket;
import utils.Config;
import utils.Logger;

public class TCPServer {

    public static void main(String[] args) {
        TCPServer server = new TCPServer();
        server.start();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(Config.PORT)) {
            Logger.info("TCP Server started on port " + Config.PORT);
            
            int clientId = 1;
            
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    ClientHandler handler = new ClientHandler(socket, clientId);
                    Thread thread = new Thread(handler, "client-" + clientId);
                    thread.start();
                    clientId++;
                } catch (Exception e) {
                    Logger.error("Error accepting client connection", e);
                }
            }
        } catch (Exception e) {
            Logger.error("Error starting TCP server", e);
        }
    }
}

