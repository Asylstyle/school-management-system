package school.core;

import java.util.List;

public record StudentReport(String studentId, String studentName, List<String> lines, int credits) {
    public StudentReport {
        lines = List.copyOf(lines);
    }
}
