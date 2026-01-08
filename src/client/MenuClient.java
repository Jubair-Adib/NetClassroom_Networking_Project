package client;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import utils.Config;

public class MenuClient {

    private String userId;
    private String role; // student | teacher

    public static void main(String[] args) {
        new MenuClient().start();
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to NetClassroom");
        System.out.println("-----------------------");

        System.out.print("Enter User ID: ");
        userId = scanner.nextLine().trim();

        while (true) {
            System.out.println("Select Role:");
            System.out.println("1. Student");
            System.out.println("2. Teacher");
            System.out.print("Choice: ");

            String c = scanner.nextLine().trim();
            if ("1".equals(c)) {
                role = "student";
                break;
            } else if ("2".equals(c)) {
                role = "teacher";
                break;
            } else {
                System.out.println("Invalid choice.\n");
            }
        }

        try (Socket socket = new Socket("localhost", Config.PORT)) {
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            /* ===================== LISTENER THREAD ===================== */
            Thread listener = new Thread(() -> {
                try {
                    while (true) {
                        StringBuilder headers = new StringBuilder();
                        int a = -1, b = -1, c = -1;

                        while (true) {
                            int d = in.read();
                            if (d == -1) return;

                            headers.append((char) d);

                            if (a == '\r' && b == '\n' && c == '\r' && d == '\n') break;
                            a = b; b = c; c = d;
                        }

                        int contentLength = 0;
                        for (String line : headers.toString().split("\r\n")) {
                            if (line.toLowerCase().startsWith("content-length:")) {
                                contentLength = Integer.parseInt(
                                        line.substring("content-length:".length()).trim());
                            }
                        }

                        byte[] body = new byte[contentLength];
                        int read = 0;
                        while (read < contentLength) {
                            read += in.read(body, read, contentLength - read);
                        }

                        String msg = new String(body, StandardCharsets.UTF_8);

                        System.out.println("\n=== Server ===");
                        System.out.println(msg);
                        System.out.println("===============");
                    }
                } catch (Exception ignored) {}
            });
            listener.start();

            System.out.println("\nLogged in as " + userId + " (" + role + ")");

            boolean running = true;
            while (running) {

                System.out.println("\nMenu:");
                if (role.equals("teacher")) {
                    System.out.println("1. Join Classroom");
                    System.out.println("2. Send Chat Message");
                    System.out.println("3. Make Announcement");
                    System.out.println("4. Create Assignment");
                    System.out.println("5. Create Groups (for GROUP assignment)");
                    System.out.println("6. View Submission Status");
                    System.out.println("7. Send Private Message");
                    System.out.println("8. Exit");
                } else {
                    System.out.println("1. Join Classroom");
                    System.out.println("2. Send Chat Message");
                    System.out.println("3. Submit Assignment");
                    System.out.println("4. Send Private Message");
                    System.out.println("5. Exit");
                }

                System.out.print("Choice: ");
                String choice = scanner.nextLine().trim();

                /* ================= JOIN ================= */
                if ("1".equals(choice)) {
                    send(out,
                            "POST /join HTTP/1.1\r\n" +
                            "Host: localhost\r\n" +
                            "X-User-Id: " + userId + "\r\n" +
                            "X-Role: " + role + "\r\n" +
                            "Content-Length: 0\r\n\r\n");
                }

                /* ================= CHAT ================= */
                else if ("2".equals(choice)) {
                    System.out.print("Enter message: ");
                    String msg = scanner.nextLine();
                    send(out,
                            "POST /chat HTTP/1.1\r\n" +
                            "Host: localhost\r\n" +
                            "Content-Length: " + msg.length() + "\r\n\r\n" +
                            msg);
                }

                /* ================= ANNOUNCE ================= */
                else if ("3".equals(choice) && role.equals("teacher")) {
                    System.out.print("Announcement: ");
                    String msg = scanner.nextLine();
                    send(out,
                            "POST /announce HTTP/1.1\r\n" +
                            "Host: localhost\r\n" +
                            "Content-Length: " + msg.length() + "\r\n\r\n" +
                            msg);
                }

                /* ================= CREATE ASSIGNMENT ================= */
                else if ("4".equals(choice) && role.equals("teacher")) {
                    System.out.print("Assignment ID: ");
                    String aid = scanner.nextLine();

                    System.out.print("Mode (1=SOLO, 2=GROUP): ");
                    String mode = scanner.nextLine().equals("2") ? "GROUP" : "SOLO";

                    send(out,
                            "POST /assignment HTTP/1.1\r\n" +
                            "Host: localhost\r\n" +
                            "X-Assignment-Id: " + aid + "\r\n" +
                            "X-Mode: " + mode + "\r\n" +
                            "Content-Length: 0\r\n\r\n");
                }

                /* ================= CREATE GROUPS ================= */
                else if ("5".equals(choice) && role.equals("teacher")) {

                    System.out.print("Assignment ID: ");
                    String aid = scanner.nextLine();

                    // 🔹 STEP 1: SHOW STUDENTS
                    send(out,
                            "POST /students HTTP/1.1\r\n" +
                            "Host: localhost\r\n" +
                            "Content-Length: 0\r\n\r\n");

                    System.out.println("\nEnter groups (comma-separated per line)");
                    System.out.println("Example:");
                    System.out.println("s1,s2");
                    System.out.println("s3,s4");
                    System.out.println("Empty line to finish");

                    StringBuilder body = new StringBuilder();
                    while (true) {
                        String line = scanner.nextLine();
                        if (line.isBlank()) break;
                        body.append(line).append("\n");
                    }

                    String bodyStr = body.toString();
                    byte[] bodyBytes = bodyStr.getBytes(StandardCharsets.UTF_8);
                    
                    send(out,
                            "POST /groups HTTP/1.1\r\n" +
                            "Host: localhost\r\n" +
                            "X-Assignment-Id: " + aid + "\r\n" +
                            "Content-Type: text/plain\r\n" +
                            "Content-Length: " + bodyBytes.length + "\r\n\r\n" +
                            bodyStr);
                }

                /* ================= STATUS ================= */
                else if ("6".equals(choice) && role.equals("teacher")) {
                    System.out.print("Assignment ID: ");
                    String aid = scanner.nextLine();
                    send(out,
                            "POST /status HTTP/1.1\r\n" +
                            "Host: localhost\r\n" +
                            "X-Assignment-Id: " + aid + "\r\n" +
                            "Content-Length: 0\r\n\r\n");
                }

                /* ================= SUBMIT ================= */
                else if ("3".equals(choice) && role.equals("student")) {
                    System.out.print("Assignment ID: ");
                    String aid = scanner.nextLine();

                    System.out.print("File path: ");
                    File f = new File(scanner.nextLine());
                    byte[] data = new byte[(int) f.length()];
                    new FileInputStream(f).read(data);

                    out.write((
                            "POST /submit HTTP/1.1\r\n" +
                            "Host: localhost\r\n" +
                            "X-Assignment-Id: " + aid + "\r\n" +
                            "X-Student-Id: " + userId + "\r\n" +
                            "X-Filename: " + f.getName() + "\r\n" +
                            "Content-Length: " + data.length + "\r\n\r\n"
                    ).getBytes());
                    out.write(data);
                    out.flush();
                }

                /* ================= PRIVATE MESSAGE (STUDENT) ================= */
                else if ("4".equals(choice) && role.equals("student")) {
                    System.out.print("To user ID: ");
                    String to = scanner.nextLine();

                    System.out.print("Message: ");
                    String msg = scanner.nextLine();

                    send(out,
                            "POST /pm HTTP/1.1\r\n" +
                            "Host: localhost\r\n" +
                            "X-To: " + to + "\r\n" +
                            "Content-Length: " + msg.length() + "\r\n\r\n" +
                            msg);
                }

                /* ================= PRIVATE MESSAGE (TEACHER → STUDENT) ================= */
                else if ("7".equals(choice) && role.equals("teacher")) {
                    System.out.print("To student ID: ");
                    String to = scanner.nextLine();

                    System.out.print("Message: ");
                    String msg = scanner.nextLine();

                    send(out,
                            "POST /pm HTTP/1.1\r\n" +
                            "Host: localhost\r\n" +
                            "X-To: " + to + "\r\n" +
                            "Content-Length: " + msg.length() + "\r\n\r\n" +
                            msg);
                }

                /* ================= EXIT ================= */
                else if (("5".equals(choice) && role.equals("student")) ||
                         ("8".equals(choice) && role.equals("teacher"))) {
                    running = false;
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void send(OutputStream out, String req) throws Exception {
        out.write(req.getBytes(StandardCharsets.UTF_8));
        out.flush();
    }
}
