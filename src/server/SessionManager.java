package server;

import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    // Map: OutputStream -> UserSession
    private static final Map<OutputStream, UserSession> sessions =
            new ConcurrentHashMap<>();

    private SessionManager() {
        // utility class
    }

    public static void addSession(UserSession session) {
        sessions.put(session.getOutputStream(), session);
    }

    public static void removeSession(OutputStream out) {
        sessions.remove(out);
    }

    public static UserSession getSession(OutputStream out) {
        return sessions.get(out);
    }

    public static Collection<UserSession> getAllSessions() {
        return sessions.values();
    }

    // Used for private student ↔ student chat
    public static UserSession getSessionByUserId(String userId) {
        for (UserSession s : sessions.values()) {
            if (s.getUserId().equals(userId)) {
                return s;
            }
        }
        return null;
    }

    // Get all students currently in the classroom
    public static java.util.List<String> getAllStudentIds() {
        java.util.List<String> studentIds = new java.util.ArrayList<>();
        for (UserSession s : sessions.values()) {
            if (s.isStudent()) {
                studentIds.add(s.getUserId());
            }
        }
        return studentIds;
    }
}
