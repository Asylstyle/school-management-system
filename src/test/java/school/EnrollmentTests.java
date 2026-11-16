package school;

import school.core.Course;
import school.core.Enrollment;
import school.core.EnrollmentService;
import school.core.GradingService;
import school.grading.Assessment;

final class EnrollmentTests {
    static void run() {
        Course course = Course.builder("C1", "Course").teacher("T1").capacity(1).build();
        EnrollmentService service = new EnrollmentService();
        GradingService grading = new GradingService();
        service.addCourse(course);
        Checks.fails(IllegalArgumentException.class, () -> service.addCourse(course));
        Checks.equal(true, service.hasCourse("c1"));
        Checks.equal(false, service.hasCourse("C2"));
        Checks.fails(IllegalArgumentException.class, () -> service.course("C2"));

        Enrollment enrollment = service.enroll("s1", course);
        Checks.equal("S1", enrollment.studentId());
        Checks.equal("C1", enrollment.courseCode());
        Checks.equal(enrollment, service.find("S1", "c1"));
        Checks.equal(true, service.isEnrolled("s1", "C1"));
        Checks.equal(1L, service.count("c1"));
        Checks.equal(1, service.ofStudent("s1").size());
        Checks.equal(0, service.ofStudent("S2").size());
        Checks.fails(IllegalArgumentException.class, () -> service.enroll("S1", course));
        Checks.fails(IllegalArgumentException.class, () -> service.enroll("S2", course));
        Checks.fails(IllegalArgumentException.class, () -> service.find("S2", "C1"));
        Checks.fails(IllegalArgumentException.class, () -> service.enroll(" ", course));

        var before = enrollment.assessments();
        Checks.equal(false, grading.passed(enrollment, course));
        grading.record(enrollment, new Assessment("Exam", 90, 1));
        Checks.equal(0, before.size());
        Checks.equal(1, enrollment.assessments().size());
        Checks.equal(true, grading.passed(enrollment, course));
        Checks.equal("A", grading.result(enrollment, course).orElseThrow().label());
        Checks.fails(UnsupportedOperationException.class, () -> enrollment.assessments().clear());
        Checks.fails(IllegalArgumentException.class, () -> grading.record(enrollment, new Assessment("exam", 80, 1)));
        Checks.fails(UnsupportedOperationException.class, () -> service.all().clear());
    }
}
