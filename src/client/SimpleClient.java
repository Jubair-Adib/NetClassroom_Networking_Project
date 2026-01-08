package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;
import utils.Config;

public class SimpleClient {

    public static void main(String[] args) {
        SimpleClient client = new SimpleClient();
        client.run();
    }

    public void run() {
        Socket socket = null;
        Scanner scanner = new Scanner(System.in);

        try {
            socket = new Socket("localhost", Config.PORT);
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            System.out.println("Connected to server. Type '/quit' to exit.");

            while (true) {
                System.out.print("> ");
                String line = scanner.nextLine();

                if ("/quit".equals(line)) {
                    break;
                }

                String message = line + "\n";
                outputStream.write(message.getBytes("UTF-8"));
                outputStream.flush();

                byte[] buffer = new byte[Config.BUFFER_SIZE];
                int bytesRead = inputStream.read(buffer);
                if (bytesRead > 0) {
                    String response = new String(buffer, 0, bytesRead, "UTF-8");
                    System.out.print(response);
                }
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println("Error closing socket: " + e.getMessage());
                }
            }
            scanner.close();
        }
    }
}
