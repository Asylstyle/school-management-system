package school;

import school.core.Course;
import school.core.CourseCatalog;
import school.core.Enrollment;
import school.core.EnrollmentService;
import school.core.GradingService;
import school.grading.Assessment;

final class EnrollmentTests {
    static void run() {
        Course course = Course.builder("C1", "Course", "T1").capacity(1).build();
        Course other = Course.builder("C2", "Other", "T1").build();
        CourseCatalog catalog = new CourseCatalog();
        catalog.add(course);
        catalog.add(other);
        Checks.fails(IllegalArgumentException.class, () -> catalog.add(course));
        Checks.equal(true, catalog.contains("c1"));
        Checks.equal(false, catalog.contains("C3"));
        Checks.equal(course, catalog.find("c1"));
        Checks.fails(IllegalArgumentException.class, () -> catalog.find("C3"));
        Checks.equal(2, catalog.all().size());

        EnrollmentService service = new EnrollmentService();
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
        Checks.fails(IllegalArgumentException.class, () -> service.enroll(" ", other));
        Checks.fails(UnsupportedOperationException.class, () -> service.all().clear());

        GradingService grading = new GradingService();
        var before = enrollment.assessments();
        grading.record(enrollment, new Assessment("Exam", 90, 1));
        Checks.equal(0, before.size());
        Checks.equal(1, enrollment.assessments().size());
        Checks.fails(UnsupportedOperationException.class, () -> enrollment.assessments().clear());
        Checks.fails(IllegalArgumentException.class, () -> grading.record(enrollment, new Assessment("exam", 80, 1)));
        Checks.equal("A", grading.result(enrollment, course).orElseThrow().label());

        Checks.equal(false, grading.passed(enrollment, course));
        Checks.equal("A", grading.complete(enrollment, course).label());
        Checks.equal(true, enrollment.completed());
        Checks.equal(true, grading.passed(enrollment, course));
        Checks.fails(IllegalArgumentException.class, () -> grading.complete(enrollment, course));
        Checks.fails(IllegalArgumentException.class, () -> grading.record(enrollment, new Assessment("Retake", 70, 1)));

        Enrollment empty = service.enroll("S1", other);
        Checks.fails(IllegalArgumentException.class, () -> grading.complete(empty, other));
        Checks.equal(false, empty.completed());
        grading.record(empty, new Assessment("Exam", 40, 1));
        grading.complete(empty, other);
        Checks.equal(false, grading.passed(empty, other));
    }
}
