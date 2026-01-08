package server;

import java.util.*;

public class GroupManager {

    private static final Map<String, List<String>> groups = new HashMap<>();

    static {
        // Hardcoded groups for demo
        groups.put("group1", Arrays.asList("student1", "student2", "student3"));
        groups.put("group2", Arrays.asList("student4", "student5"));
    }

    public static List<String> getGroupMembers(String studentId) {
        for (List<String> members : groups.values()) {
            if (members.contains(studentId)) {
                return members;
            }
        }
        return Collections.singletonList(studentId);
    }
}
