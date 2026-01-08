package server;

public class Assignment {

    public enum Mode {
        SOLO,
        GROUP
    }

    private final String assignmentId;
    private final Mode mode;

    public Assignment(String assignmentId, Mode mode) {
        this.assignmentId = assignmentId;
        this.mode = mode;
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public Mode getMode() {
        return mode;
    }
}
