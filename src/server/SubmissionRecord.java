package server;

public class SubmissionRecord {

    private final String assignmentId;
    private final String studentId;
    private final String submittedBy;
    private final long timestamp;

    public SubmissionRecord(String assignmentId, String studentId, String submittedBy) {
        this.assignmentId = assignmentId;
        this.studentId = studentId;
        this.submittedBy = submittedBy;
        this.timestamp = System.currentTimeMillis();
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
