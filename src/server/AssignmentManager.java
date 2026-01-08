package server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AssignmentManager {

    // assignmentId -> Assignment
    private static final Map<String, Assignment> assignments = new HashMap<>();

    private AssignmentManager() {
        // utility class
    }

    public static synchronized boolean createAssignment(String assignmentId, Assignment.Mode mode) {
        if (assignmentId == null || mode == null) {
            return false;
        }
        if (assignments.containsKey(assignmentId)) {
            return false; // already exists
        }
        assignments.put(assignmentId, new Assignment(assignmentId, mode));
        return true;
    }

    public static synchronized Assignment getAssignment(String assignmentId) {
        return assignments.get(assignmentId);
    }

    public static synchronized Map<String, Assignment> getAllAssignments() {
        return Collections.unmodifiableMap(assignments);
    }
}
