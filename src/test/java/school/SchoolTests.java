package school;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import school.access.Access;
import school.access.AccessDeniedException;
import school.access.AccessModes;
import school.access.Permission;
import school.core.Course;
import school.core.SchoolFacade;
import school.core.StudentReport;
import school.events.ActivityLog;
import school.events.EventBus;
import school.events.EventType;
import school.grading.Assessment;
import school.people.Role;

final class SchoolTests {
    static void run() {
        EventBus bus = new EventBus();
        ActivityLog log = new ActivityLog();
        bus.subscribe(log);
        SchoolFacade school = new SchoolFacade(bus, Clock.fixed(Instant.EPOCH, ZoneOffset.UTC));
        Access admin = AccessModes.admin();
        Access viewer = AccessModes.viewer();
        Access nobody = new Access() {
            @Override
            public boolean allows(Permission permission) {
                return false;
            }

            @Override
            public String label() {
                return "Nobody";
            }
        };

        school.register(admin, Role.TEACHER, "T", "Dana");
        school.register(admin, Role.STUDENT, "s", "Amina");
        school.register(admin, Role.STUDENT, "S2", "Dias");
        Checks.equal("S", school.people(viewer).get(1).id());
        Checks.equal("Amina", school.personName(viewer, "s"));
        Checks.fails(IllegalArgumentException.class, () -> school.register(admin, Role.STUDENT, " S ", "Duplicate"));
        Checks.fails(AccessDeniedException.class, () -> school.register(viewer, Role.STUDENT, "S3", "Denied"));

        Checks.fails(IllegalArgumentException.class,
                () -> school.addCourse(admin, Course.builder("Bad", "Bad", "S").build()));
        school.addCourse(admin, Course.builder("c", "Computing", "t").capacity(1).build());
        Checks.fails(IllegalArgumentException.class, () -> school.addCourse(admin, school.course(viewer, "C")));
        Checks.fails(AccessDeniedException.class, () -> school.addCourse(viewer, school.course(viewer, "C")));
        Checks.fails(IllegalArgumentException.class,
                () -> school.addCourse(admin, Course.builder("C9", "Next", "T").prerequisite("missing").build()));

        Checks.fails(IllegalArgumentException.class, () -> school.enroll(admin, "T", "C"));
        Checks.fails(IllegalArgumentException.class, () -> school.enroll(admin, "S", "missing"));
        Checks.fails(AccessDeniedException.class, () -> school.enroll(viewer, "S", "C"));
        school.enroll(admin, "s", "c");
        Checks.equal(true, school.result(viewer, school.enrollment(viewer, "S", "C")).isEmpty());
        Checks.fails(IllegalArgumentException.class, () -> school.enroll(admin, "S", "C"));
        Checks.fails(IllegalArgumentException.class, () -> school.enroll(admin, "S2", "C"));

        Checks.fails(AccessDeniedException.class,
                () -> school.recordGrade(viewer, "S", "C", new Assessment("Exam", 90, 1)));
        Checks.fails(IllegalArgumentException.class,
                () -> school.recordGrade(admin, "S2", "C", new Assessment("Exam", 90, 1)));
        school.recordGrade(admin, "s", "c", new Assessment("Exam", 90, 1));
        Checks.fails(IllegalArgumentException.class,
                () -> school.recordGrade(admin, "S", "C", new Assessment("exam", 80, 1)));
        Checks.equal("A", school.result(viewer, school.enrollment(viewer, "S", "C")).orElseThrow().label());
        Checks.equal(1L, school.enrolledCount(viewer, "C"));
        Checks.equal(6, log.events().size());
        Checks.equal(Instant.EPOCH, log.events().get(5).at());
        Checks.equal("S", log.events().get(5).studentId());
        Checks.equal("T", log.events().get(5).teacherId());
        Checks.equal(null, log.events().get(0).studentId());

        school.addCourse(admin, Course.builder("C2", "Advanced", "T").credits(6).prerequisite("c").build());
        Checks.fails(IllegalArgumentException.class, () -> school.enroll(admin, "S2", "C2"));
        Checks.fails(IllegalArgumentException.class, () -> school.enroll(admin, "S", "C2"));

        Checks.fails(AccessDeniedException.class, () -> school.completeCourse(viewer, "S", "C"));
        Checks.equal("90.0 / A", school.completeCourse(admin, "s", "c").display());
        Checks.equal(EventType.COURSE_COMPLETED, log.events().get(7).type());
        Checks.fails(IllegalArgumentException.class, () -> school.completeCourse(admin, "S", "C"));
        Checks.fails(IllegalArgumentException.class,
                () -> school.recordGrade(admin, "S", "C", new Assessment("Late work", 70, 1)));

        school.enroll(admin, "S", "C2");
        Checks.equal(1L, school.enrolledCount(viewer, "c2"));
        Checks.fails(IllegalArgumentException.class, () -> school.completeCourse(admin, "S", "C2"));

        Checks.fails(AccessDeniedException.class, () -> school.issueReport(viewer, "S"));
        StudentReport report = school.issueReport(admin, "s");
        Checks.equal("Amina", report.studentName());
        Checks.equal(2, report.lines().size());
        Checks.equal("C Computing | 5 credits | 90.0 / A | completed", report.lines().get(0));
        Checks.equal("C2 Advanced | 6 credits | Not graded | in progress", report.lines().get(1));
        Checks.equal(5, report.credits());
        Checks.equal(10, log.events().size());
        Checks.equal(EventType.REPORT_READY, log.events().get(9).type());
        Checks.fails(IllegalArgumentException.class, () -> school.issueReport(admin, "T"));

        school.reportDeniedAccess("Viewer was denied ENROLL");
        Checks.equal(EventType.ACCESS_DENIED, log.events().get(10).type());

        Checks.fails(AccessDeniedException.class, () -> school.people(nobody));
        Checks.fails(AccessDeniedException.class, () -> school.courses(nobody));
        Checks.fails(AccessDeniedException.class, () -> school.enrollments(nobody));
        Checks.fails(AccessDeniedException.class, () -> school.personName(nobody, "S"));
        Checks.fails(AccessDeniedException.class, () -> school.issueReport(nobody, "S"));
        Checks.equal(11, log.events().size());

        Checks.fails(UnsupportedOperationException.class, () -> school.people(viewer).clear());
        Checks.fails(UnsupportedOperationException.class, () -> school.courses(viewer).clear());
        Checks.fails(UnsupportedOperationException.class, () -> school.enrollments(viewer).clear());
    }
}
