package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AssignmentGroupManager {

    // assignmentId -> list of groups
    // each group is a list of studentIds
    private static final Map<String, List<List<String>>> groups =
            new HashMap<>();

    private AssignmentGroupManager() {}

    /**
     * Sets groups for an assignment, overwriting any previous groups.
     * Ensures each student is in at most one group per assignment.
     * 
     * @param assignmentId The assignment ID
     * @param groupLines List of group definitions (each line is comma-separated student IDs)
     * @param validStudentIds Set of valid student IDs currently in the classroom
     * @return List of error messages (empty if successful)
     */
    public static synchronized List<String> setGroups(String assignmentId,
                                                      List<String> groupLines,
                                                      Set<String> validStudentIds) {
        List<String> errors = new ArrayList<>();
        
        if (assignmentId == null || groupLines == null) {
            errors.add("Invalid assignment ID or group data");
            return errors;
        }

        // Clear existing groups for this assignment
        groups.put(assignmentId, new ArrayList<>());

        Set<String> assignedStudents = new HashSet<>();
        
        for (String line : groupLines) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }
            
            // Parse comma-separated student IDs
            String[] parts = line.trim().split(",");
            List<String> groupMembers = new ArrayList<>();
            
            for (String studentId : parts) {
                studentId = studentId.trim();
                if (studentId.isEmpty()) {
                    continue;
                }
                
                // Validate student exists in classroom
                if (!validStudentIds.contains(studentId)) {
                    errors.add("Student " + studentId + " is not in the classroom");
                    continue;
                }
                
                // Check if student already assigned to another group
                if (assignedStudents.contains(studentId)) {
                    errors.add("Student " + studentId + " is already in another group");
                    continue;
                }
                
                groupMembers.add(studentId);
                assignedStudents.add(studentId);
            }
            
            // Only add non-empty groups
            if (!groupMembers.isEmpty()) {
                groups.get(assignmentId).add(groupMembers);
            }
        }
        
        return errors;
    }

    /**
     * Gets all group members for a student in a specific assignment.
     * Returns null if the student is not in any group.
     */
    public static synchronized List<String> getGroupMembers(String assignmentId,
                                                            String studentId) {

        List<List<String>> assignmentGroups = groups.get(assignmentId);
        if (assignmentGroups == null) {
            return null;
        }

        for (List<String> group : assignmentGroups) {
            if (group.contains(studentId)) {
                return new ArrayList<>(group);
            }
        }
        return null;
    }

    /**
     * Gets all groups for an assignment (for debugging/status).
     */
    public static synchronized List<List<String>> getGroups(String assignmentId) {
        List<List<String>> assignmentGroups = groups.get(assignmentId);
        if (assignmentGroups == null) {
            return List.of();
        }
        // Return defensive copy
        List<List<String>> result = new ArrayList<>();
        for (List<String> group : assignmentGroups) {
            result.add(new ArrayList<>(group));
        }
        return result;
    }

    /**
     * Clears all groups for an assignment.
     */
    public static synchronized void clearGroups(String assignmentId) {
        groups.remove(assignmentId);
    }
}
