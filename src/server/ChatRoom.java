package server;

import java.io.OutputStream;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import utils.Logger;

public class ChatRoom {

    private static final Set<OutputStream> clients =
            ConcurrentHashMap.newKeySet();

    public static void addClient(OutputStream out) {
        clients.add(out);
        Logger.info("Client added to chat room. Total clients=" + clients.size());
    }

    public static void removeClient(OutputStream out) {
        clients.remove(out);
        Logger.info("Client removed from chat room. Total clients=" + clients.size());
    }

    public static void broadcast(String message) {
        byte[] data = message.getBytes();
        for (OutputStream out : clients) {
            try {
                out.write(data);
                out.flush();
            } catch (Exception e) {
                // Ignore broken clients
            }
        }
    }
}
