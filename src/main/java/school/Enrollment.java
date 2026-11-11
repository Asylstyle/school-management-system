package school;

import java.util.ArrayList;
import java.util.List;
public final class Enrollment {
    private final String studentId, courseCode;
    private final List<Assessment> assessments = new ArrayList<>();
    public Enrollment(String studentId, String courseCode) {
        this.studentId = Validation.text(studentId, "Student ID");
        this.courseCode = Validation.text(courseCode, "Course code");
    }
    public String studentId() { return studentId; }
    public String courseCode() { return courseCode; }
    public List<Assessment> assessments() { return List.copyOf(assessments); }
    void add(Assessment assessment) { assessments.add(java.util.Objects.requireNonNull(assessment)); }
}
