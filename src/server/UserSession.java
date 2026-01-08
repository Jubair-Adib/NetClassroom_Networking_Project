package server;

import java.io.OutputStream;

public class UserSession {

    private final String userId;
    private final String role; // "student" or "teacher"
    private final OutputStream outputStream;

    public UserSession(String userId, String role, OutputStream outputStream) {
        this.userId = userId;
        this.role = role;
        this.outputStream = outputStream;
    }

    public String getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public boolean isTeacher() {
        return "teacher".equalsIgnoreCase(role);
    }

    public boolean isStudent() {
        return "student".equalsIgnoreCase(role);
    }
}
