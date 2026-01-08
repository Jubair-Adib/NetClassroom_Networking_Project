package client;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import utils.Config;

public class ChatListenerClient {

    public static void main(String[] args) {
        ChatListenerClient client = new ChatListenerClient();
        client.run();
    }

    public void run() {
        Socket socket = null;
        try {
            socket = new Socket("localhost", Config.PORT);
            InputStream inputStream = socket.getInputStream();
            
            System.out.println("Listening for chat messages...");
            
            byte[] buffer = new byte[Config.BUFFER_SIZE];
            int bytesRead;
            
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                if (bytesRead > 0) {
                    String text = new String(buffer, 0, bytesRead, "UTF-8");
                    System.out.print(text);
                }
            }
        } catch (IOException e) {
            System.err.println("Connection closed: " + e.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println("Error closing socket: " + e.getMessage());
                }
            }
        }
    }
}
