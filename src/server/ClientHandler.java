package server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import protocol.HttpParser;
import protocol.HttpRequest;
import utils.Config;
import utils.Logger;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final int clientId;

    public ClientHandler(Socket socket, int clientId) {
        this.socket = socket;
        this.clientId = clientId;
    }

    @Override
    public void run() {

        OutputStream out = null;

        try {
            socket.setSoTimeout(Config.SOCKET_READ_TIMEOUT_MS);
            InputStream in = socket.getInputStream();
            out = socket.getOutputStream();

            ChatRoom.addClient(out);

            while (true) {
                HttpRequest request;

                try {
                    request = HttpParser.parse(in);
                } catch (IOException e) {
                    break;
                }

                String method = request.getMethod();
                String path = request.getPath().trim();
                String body = request.getBody();

                /* ================= JOIN ================= */
                if ("POST".equals(method) && "/join".equals(path)) {

                    String userId = request.getHeaders().get("X-User-Id");
                    String role = request.getHeaders().get("X-Role");

                    if (userId == null || role == null) {
                        respond(out, "400 Bad Request", "Bad Request");
                        continue;
                    }

                    SessionManager.addSession(new UserSession(userId, role, out));
                    broadcast("-- " + userId + " joined the class --");
                    respond(out, "200 OK", "JOINED");
                }
                /*
                 * ===================== PRIVATE MESSAGE (STUDENT ↔ STUDENT / TEACHER ↔ STUDENT)
                 * =====================
                 */
                else if ("POST".equals(request.getMethod()) && "/pm".equals(request.getPath())) {

                    // Sender must be logged in
                    UserSession sender = SessionManager.getSession(out);
                    if (sender == null) {
                        respond(out, "403 Forbidden", "Forbidden");
                        continue;
                    }

                    String toUser = request.getHeaders().get("X-To");
                    if (toUser == null) {
                        respond(out, "400 Bad Request", "Bad Request");
                        continue;
                    }

                    // Find receiver session (must exist)
                    UserSession receiver = SessionManager.getSessionByUserId(toUser);
                    if (receiver == null) {
                        respond(out, "404 Not Found", "User Not Found");
                        continue;
                    }

                    // Role-based permissions:
                    // - Students can message students and teachers
                    // - Teachers can message students only (no teacher→teacher PM)
                    boolean senderIsStudent = sender.isStudent();
                    boolean senderIsTeacher = sender.isTeacher();
                    boolean receiverIsStudent = receiver.isStudent();
                    boolean receiverIsTeacher = receiver.isTeacher();

                    if (senderIsTeacher && receiverIsTeacher) {
                        // Teacher → Teacher is not allowed
                        respond(out, "403 Forbidden", "Forbidden");
                        continue;
                    }

                    // Only student or teacher roles are allowed to send PM
                    if (!senderIsStudent && !senderIsTeacher) {
                        respond(out, "403 Forbidden", "Forbidden");
                        continue;
                    }

                    String message = request.getBody() != null ? request.getBody() : "";

                    String prefix;
                    if (senderIsTeacher && receiverIsStudent) {
                        prefix = "[PRIVATE from TEACHER " + sender.getUserId() + "]: ";
                    } else {
                        // student→student or student→teacher
                        prefix = "[PRIVATE from " + sender.getUserId() + "]: ";
                    }

                    String privateMsg = prefix + message;

                    byte[] bytes = privateMsg.getBytes("UTF-8");

                    String response = "HTTP/1.1 200 OK\r\n" +
                            "Content-Length: " + bytes.length + "\r\n\r\n" +
                            privateMsg;

                    // Send ONLY to the receiver
                    receiver.getOutputStream().write(response.getBytes("UTF-8"));
                    receiver.getOutputStream().flush();

                    // Acknowledge sender
                    respond(out, "200 OK", "SENT");
                }

                /* ================= CHAT ================= */
                else if ("POST".equals(method) && "/chat".equals(path)) {

                    UserSession s = SessionManager.getSession(out);
                    if (s == null) {
                        respond(out, "403 Forbidden", "Forbidden");
                        continue;
                    }
                
                    String msg = body != null ? body : "";
                    broadcast("MSG from " + s.getUserId() + ": " + msg);
                }
                
                
                /* ================= ANNOUNCE ================= */
                else if ("POST".equals(method) && "/announce".equals(path)) {

                    UserSession s = SessionManager.getSession(out);
                    if (s == null || !s.isTeacher()) {
                        respond(out, "403 Forbidden", "Forbidden");
                        continue;
                    }

                    broadcast("[ANNOUNCEMENT by " + s.getUserId() + "]: " + body);
                    respond(out, "200 OK", "OK");
                }

                /* ================= CREATE ASSIGNMENT ================= */
                else if ("POST".equals(method) && "/assignment".equals(path)) {

                    UserSession s = SessionManager.getSession(out);
                    if (s == null || !s.isTeacher()) {
                        respond(out, "403 Forbidden", "Forbidden");
                        continue;
                    }

                    String aid = request.getHeaders().get("X-Assignment-Id");
                    String modeHeader = request.getHeaders().get("X-Mode");

                    Assignment.Mode mode;
                    try {
                        mode = Assignment.Mode.valueOf(modeHeader);
                    } catch (Exception e) {
                        respond(out, "400 Bad Request", "Bad Request");
                        continue;
                    }

                    if (!AssignmentManager.createAssignment(aid, mode)) {
                        respond(out, "409 Conflict", "CONFLICT");
                        continue;
                    }

                    respond(out, "201 Created", "CREATED");
                }

                /* ================= LIST STUDENTS ================= */
                else if ("POST".equals(method) && "/students".equals(path)) {

                    UserSession s = SessionManager.getSession(out);
                    if (s == null || !s.isTeacher()) {
                        respond(out, "403 Forbidden", "Forbidden");
                        continue;
                    }

                    List<String> students = SessionManager.getAllStudentIds();
                    if (students.isEmpty()) {
                        respond(out, "200 OK", "No students in classroom.");
                    } else {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Students in classroom:\n");
                        for (String studentId : students) {
                            sb.append("- ").append(studentId).append("\n");
                        }
                        respond(out, "200 OK", sb.toString());
                    }
                }

                /* ================= CREATE GROUPS ================= */
                else if ("POST".equals(method) && "/groups".equals(path)) {

                    UserSession s = SessionManager.getSession(out);
                    if (s == null || !s.isTeacher()) {
                        respond(out, "403 Forbidden", "Forbidden");
                        continue;
                    }

                    String aid = request.getHeaders().get("X-Assignment-Id");
                    if (aid == null) {
                        respond(out, "400 Bad Request", "Missing X-Assignment-Id header");
                        continue;
                    }

                    Assignment a = AssignmentManager.getAssignment(aid);
                    if (a == null) {
                        respond(out, "404 Not Found", "Assignment not found");
                        continue;
                    }

                    if (a.getMode() != Assignment.Mode.GROUP) {
                        respond(out, "400 Bad Request", "Groups can only be created for GROUP assignments");
                        continue;
                    }

                    String bodyText = request.getBody();
                    if (bodyText == null || bodyText.trim().isEmpty()) {
                        respond(out, "400 Bad Request", "Empty group data");
                        continue;
                    }

                    // Parse group lines
                    List<String> groupLines = new java.util.ArrayList<>();
                    for (String line : bodyText.split("\n")) {
                        if (line != null && !line.trim().isEmpty()) {
                            groupLines.add(line.trim());
                        }
                    }

                    // Get valid student IDs
                    Set<String> validStudentIds = new HashSet<>(SessionManager.getAllStudentIds());

                    // Set groups (overwrites previous)
                    List<String> errors = AssignmentGroupManager.setGroups(aid, groupLines, validStudentIds);

                    if (errors.isEmpty()) {
                        respond(out, "200 OK", "Groups created successfully");
                    } else {
                        StringBuilder errorMsg = new StringBuilder("Errors:\n");
                        for (String error : errors) {
                            errorMsg.append("- ").append(error).append("\n");
                        }
                        respond(out, "400 Bad Request", errorMsg.toString());
                    }
                }

                /* ================= SUBMIT ASSIGNMENT ================= */
                else if ("POST".equals(method) && "/submit".equals(path)) {

                    UserSession s = SessionManager.getSession(out);
                    if (s == null || !s.isStudent()) {
                        respond(out, "403 Forbidden", "Forbidden");
                        continue;
                    }

                    String aid = request.getHeaders().get("X-Assignment-Id");
                    String studentId = request.getHeaders().get("X-Student-Id");
                    String filename = request.getHeaders().get("X-Filename");
                    byte[] data = request.getRawBody();

                    Assignment a = AssignmentManager.getAssignment(aid);
                    if (a == null || data == null) {
                        respond(out, "400 Bad Request", "Bad Request");
                        continue;
                    }

                    List<String> targets;
                    
                    if (a.getMode() == Assignment.Mode.GROUP) {
                        // For GROUP assignments, student must be in a group
                        List<String> groupMembers = AssignmentGroupManager.getGroupMembers(aid, studentId);
                        if (groupMembers == null) {
                            respond(out, "400 Bad Request", "Student is not in any group for this GROUP assignment");
                            continue;
                        }
                        targets = groupMembers;
                    } else {
                        // For SOLO assignments, only the submitting student
                        targets = List.of(studentId);
                    }

                    File dir = new File("uploads/" + aid);
                    dir.mkdirs();

                    for (String t : targets) {
                        File f = new File(dir, t + "_" + filename);
                        try (FileOutputStream fos = new FileOutputStream(f)) {
                            fos.write(data);
                        }
                    }

                    SubmissionManager.recordSubmission(aid, studentId, targets);
                    respond(out, "200 OK", "SUBMITTED");
                }

                /* ================= STATUS (TEACHER) ================= */
                else if ("POST".equals(method) && "/status".equals(path)) {

                    UserSession s = SessionManager.getSession(out);
                    if (s == null || !s.isTeacher()) {
                        respond(out, "403 Forbidden", "Forbidden");
                        continue;
                    }

                    String aid = request.getHeaders().get("X-Assignment-Id");
                    if (aid == null) {
                        respond(out, "400 Bad Request", "Bad Request");
                        continue;
                    }

                    Assignment a = AssignmentManager.getAssignment(aid);
                    if (a == null) {
                        respond(out, "404 Not Found", "No Assignment");
                        continue;
                    }

                    List<SubmissionRecord> records = SubmissionManager.getSubmissions(aid);
                    List<String> allStudents = SessionManager.getAllStudentIds();

                    // Create a map of studentId -> SubmissionRecord for quick lookup
                    Map<String, SubmissionRecord> submissionMap = new HashMap<>();
                    for (SubmissionRecord r : records) {
                        submissionMap.put(r.getStudentId(), r);
                    }

                    StringBuilder table = new StringBuilder();

                    table.append("Assignment: ").append(aid).append("\n");
                    table.append("Mode: ").append(a.getMode()).append("\n\n");

                    table.append(String.format(
                            "%-12s %-12s %-12s%n",
                            "Student", "Status", "SubmittedBy"));
                    table.append("---------------------------------------------\n");

                    if (allStudents.isEmpty()) {
                        table.append("No students in classroom.\n");
                    } else {
                        for (String studentId : allStudents) {
                            SubmissionRecord record = submissionMap.get(studentId);
                            if (record != null) {
                                table.append(String.format(
                                        "%-12s %-12s %-12s%n",
                                        studentId,
                                        "SUBMITTED",
                                        record.getSubmittedBy()));
                            } else {
                                table.append(String.format(
                                        "%-12s %-12s %-12s%n",
                                        studentId,
                                        "NOT SUBMITTED",
                                        "-"));
                            }
                        }
                    }

                    respond(out, "200 OK", table.toString());
                }

                else {
                    respond(out, "404 Not Found", "Not Found");
                }
            }

        } catch (Exception e) {
            Logger.error("Client error", e);
        } finally {
            ChatRoom.removeClient(out);
            try {
                socket.close();
            } catch (Exception ignored) {
            }
        }
    }

    private void broadcast(String msg) throws IOException {
        byte[] b = msg.getBytes(StandardCharsets.UTF_8);
        String res = "HTTP/1.1 200 OK\r\nContent-Length: " + b.length + "\r\n\r\n" + msg;
        ChatRoom.broadcast(res);
    }

    private void respond(OutputStream out, String status, String body) throws IOException {
        byte[] b = body.getBytes(StandardCharsets.UTF_8);
        String res = "HTTP/1.1 " + status + "\r\nContent-Length: " + b.length + "\r\n\r\n" + body;
        out.write(res.getBytes(StandardCharsets.UTF_8));
        out.flush();
    }
}
