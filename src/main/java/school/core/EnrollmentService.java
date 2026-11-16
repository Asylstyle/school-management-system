package school.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import school.common.Validation;

public final class EnrollmentService {
    private final Map<String, Course> courses = new LinkedHashMap<>();
    private final List<Enrollment> enrollments = new ArrayList<>();

    public void addCourse(Course course) {
        if (courses.containsKey(course.code())) {
            throw new IllegalArgumentException("This course code already exists");
        }
        courses.put(course.code(), course);
    }

    public boolean hasCourse(String code) {
        return courses.containsKey(Validation.id(code, "Course code"));
    }

    public Course course(String code) {
        Course course = courses.get(Validation.id(code, "Course code"));
        if (course == null) {
            throw new IllegalArgumentException("Course not found");
        }
        return course;
    }

    public List<Course> courses() {
        return List.copyOf(courses.values());
    }

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
        return result;
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
