package school.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import school.common.Validation;
import school.grading.Assessment;

public final class Enrollment {
    private final String studentId;
    private final String courseCode;
    private final List<Assessment> assessments = new ArrayList<>();

    Enrollment(String studentId, String courseCode) {
        this.studentId = Validation.id(studentId, "Student ID");
        this.courseCode = Validation.id(courseCode, "Course code");
    }

    public String studentId() {
        return studentId;
    }

    public String courseCode() {
        return courseCode;
    }

    public List<Assessment> assessments() {
        return List.copyOf(assessments);
    }

    void add(Assessment assessment) {
        assessments.add(Objects.requireNonNull(assessment));
    }
}
