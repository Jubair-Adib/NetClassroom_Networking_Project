package server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubmissionManager {

    // assignmentId -> List<SubmissionRecord>
    private static final Map<String, List<SubmissionRecord>> submissions = new HashMap<>();

    private SubmissionManager() {
        // utility class
    }

    public static synchronized void recordSubmission(String assignmentId, String submittedBy, List<String> affectedStudents) {
        if (assignmentId == null || submittedBy == null || affectedStudents == null || affectedStudents.isEmpty()) {
            return;
        }

        submissions.putIfAbsent(assignmentId, new ArrayList<>());
        List<SubmissionRecord> records = submissions.get(assignmentId);

        // Create a record for each affected student
        for (String studentId : affectedStudents) {
            boolean alreadySubmitted = records.stream()
                    .anyMatch(r -> r.getStudentId().equals(studentId));
        
            if (!alreadySubmitted) {
                records.add(new SubmissionRecord(assignmentId, studentId, submittedBy));
            }
        }
        
    }

    public static synchronized List<SubmissionRecord> getSubmissions(String assignmentId) {
        List<SubmissionRecord> records = submissions.get(assignmentId);
        return records != null ? Collections.unmodifiableList(new ArrayList<>(records)) : Collections.emptyList();
    }

    public static synchronized Map<String, List<SubmissionRecord>> getAllSubmissions() {
        Map<String, List<SubmissionRecord>> copy = new HashMap<>();
        for (Map.Entry<String, List<SubmissionRecord>> entry : submissions.entrySet()) {
            copy.put(entry.getKey(), Collections.unmodifiableList(new ArrayList<>(entry.getValue())));
        }
        return Collections.unmodifiableMap(copy);
    }
}

