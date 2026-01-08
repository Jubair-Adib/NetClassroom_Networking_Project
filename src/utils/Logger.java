package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private static final Object LOCK = new Object();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public static void info(String msg) {
        log("INFO", msg, null);
    }

    public static void error(String msg, Exception e) {
        log("ERROR", msg, e);
    }

    private static void log(String level, String msg, Exception e) {
        String time = LocalDateTime.now().format(FMT);
        String thread = Thread.currentThread().getName();

        synchronized (LOCK) {
            System.out.println(time + " [" + thread + "] " + level + " - " + msg);
            if (e != null) {
                System.out.println(time + " [" + thread + "] " + level + " - " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    }
}
