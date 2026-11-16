package school.core;

import java.util.ArrayList;
import java.util.List;
import school.common.Validation;

public final class EnrollmentService {
    private final List<Enrollment> enrollments = new ArrayList<>();

    public Enrollment enroll(String studentId, Course course) {
        if (isEnrolled(studentId, course.code())) {
            throw new IllegalArgumentException("Student is already enrolled");
        }
        if (count(course.code()) >= course.capacity()) {
            throw new IllegalArgumentException("Course is full");
        }
        Enrollment enrollment = new Enrollment(studentId, course.code());
        enrollments.add(enrollment);
        return enrollment;
    }

    public boolean isEnrolled(String studentId, String courseCode) {
        return lookup(studentId, courseCode) != null;
    }

    public Enrollment find(String studentId, String courseCode) {
        Enrollment enrollment = lookup(studentId, courseCode);
        if (enrollment == null) {
            throw new IllegalArgumentException("Student is not enrolled in this course");
        }
        return enrollment;
    }

    public List<Enrollment> all() {
        return List.copyOf(enrollments);
    }

    public List<Enrollment> ofStudent(String studentId) {
        String student = Validation.id(studentId, "Student ID");
        List<Enrollment> result = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            if (enrollment.studentId().equals(student)) {
                result.add(enrollment);
            }
        }
        return List.copyOf(result);
    }

    public long count(String courseCode) {
        String course = Validation.id(courseCode, "Course code");
        long count = 0;
        for (Enrollment enrollment : enrollments) {
            if (enrollment.courseCode().equals(course)) {
                count++;
            }
        }
        return count;
    }

    private Enrollment lookup(String studentId, String courseCode) {
        String student = Validation.id(studentId, "Student ID");
        String course = Validation.id(courseCode, "Course code");
        for (Enrollment enrollment : enrollments) {
            if (enrollment.studentId().equals(student) && enrollment.courseCode().equals(course)) {
                return enrollment;
            }
        }
        return null;
    }
}
