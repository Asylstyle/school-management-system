package school;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

final class SchoolTests {
    static void run() {
        EventBus bus = new EventBus();
        ActivityLog log = new ActivityLog();
        bus.subscribe(log);
        SchoolFacade school = new SchoolFacade(bus, Clock.fixed(Instant.EPOCH, ZoneOffset.UTC));
        Access admin = DemoData.administrator();
        Access viewer = new ReadOnlyAccess();
        school.register(admin, Role.TEACHER, "T", "Dana");
        school.register(admin, Role.STUDENT, "S", "Amina");
        school.register(admin, Role.STUDENT, "S2", "Dias");
        Checks.fails(IllegalArgumentException.class, () -> school.register(admin, Role.STUDENT, " S ", "Duplicate"));
        Checks.fails(SecurityException.class, () -> school.register(viewer, Role.STUDENT, "S3", "Denied"));
        Checks.fails(IllegalArgumentException.class, () -> school.addCourse(admin, Course.builder("Bad", "Bad").teacher("S").build()));
        school.addCourse(admin, Course.builder("C", "Computing").teacher("T").capacity(1).build());
        Checks.fails(IllegalArgumentException.class, () -> school.addCourse(admin, school.course("C")));
        Checks.fails(SecurityException.class, () -> school.addCourse(viewer, school.course("C")));
        Checks.fails(IllegalArgumentException.class, () -> school.enroll(admin, "T", "C"));
        Checks.fails(IllegalArgumentException.class, () -> school.enroll(admin, "S", "missing"));
        Checks.fails(SecurityException.class, () -> school.enroll(viewer, "S", "C"));
        school.enroll(admin, "S", "C");
        Checks.equal(true, school.result(school.enrollment("S", "C")).isEmpty());
        Checks.fails(IllegalArgumentException.class, () -> school.enroll(admin, "S", "C"));
        Checks.fails(IllegalArgumentException.class, () -> school.enroll(admin, "S2", "C"));
        Checks.fails(SecurityException.class, () -> school.recordGrade(viewer, "S", "C", new Assessment("Exam", 90, 1)));
        Checks.fails(IllegalArgumentException.class, () -> school.recordGrade(admin, "S2", "C", new Assessment("Exam", 90, 1)));
        school.recordGrade(admin, "S", "C", new Assessment("Exam", 90, 1));
        Checks.fails(IllegalArgumentException.class, () -> school.recordGrade(admin, "S", "C", new Assessment("exam", 80, 1)));
        Checks.equal("A", school.result(school.enrollment("S", "C")).orElseThrow().label());
        Checks.equal(1L, school.enrolledCount("C"));
        Checks.equal(6, log.events().size());
        Checks.equal(Instant.EPOCH, log.events().get(5).at());
        Checks.fails(UnsupportedOperationException.class, () -> school.people().clear());
        Checks.fails(UnsupportedOperationException.class, () -> school.courses().clear());
        Checks.fails(UnsupportedOperationException.class, () -> school.enrollments().clear());
    }
}
