package school.core;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import school.access.Access;
import school.access.Permission;
import school.events.EventBus;
import school.events.EventType;
import school.events.SchoolEvent;
import school.grading.Assessment;
import school.grading.GradeResult;
import school.people.Person;
import school.people.PersonRegistry;
import school.people.Role;

/**
 * One simple entry point for the console. Every method checks the permission,
 * gives the work to a service and publishes an event when something changed.
 */
public final class SchoolFacade {
    private final PersonRegistry people = new PersonRegistry();
    private final CourseCatalog catalog = new CourseCatalog();
    private final EnrollmentService enrollments = new EnrollmentService();
    private final GradingService grades = new GradingService();
    private final EventBus events;
    private final Clock clock;

    public SchoolFacade(EventBus events) {
        this(events, Clock.systemUTC());
    }

    public SchoolFacade(EventBus events, Clock clock) {
        this.events = Objects.requireNonNull(events);
        this.clock = Objects.requireNonNull(clock);
    }

    public Person register(Access access, Role role, String id, String name) {
        access.require(Permission.MANAGE_PEOPLE);
        Person person = people.register(role, id, name);
        emit(EventType.PERSON_REGISTERED, person.name() + " registered as " + person.role(), null, null);
        return person;
    }

    public void addCourse(Access access, Course course) {
        access.require(Permission.MANAGE_COURSES);
        Objects.requireNonNull(course);
        people.teacher(course.teacherId());
        for (String code : course.prerequisites()) {
            if (!catalog.contains(code)) {
                throw new IllegalArgumentException("Prerequisite course not found: " + code);
            }
        }
        catalog.add(course);
        emit(EventType.COURSE_CREATED, course.code() + " created: " + course.title(), null, course.teacherId());
    }

    public void enroll(Access access, String studentId, String courseCode) {
        access.require(Permission.ENROLL);
        Course course = catalog.find(courseCode);
        Person student = people.student(studentId);
        for (String code : course.prerequisites()) {
            if (!hasPassed(student.id(), code)) {
                throw new IllegalArgumentException("Prerequisite not passed: " + code);
            }
        }
        enrollments.enroll(student.id(), course);
        emit(EventType.STUDENT_ENROLLED, student.name() + " enrolled in " + course.code(), student.id(), course.teacherId());
    }

    public void recordGrade(Access access, String studentId, String courseCode, Assessment assessment) {
        access.require(Permission.RECORD_GRADES);
        Enrollment enrollment = enrollments.find(studentId, courseCode);
        grades.record(enrollment, assessment);
        Course course = catalog.find(enrollment.courseCode());
        Person student = people.find(enrollment.studentId());
        String message = assessment.title() + ": " + assessment.score() + " for " + student.name() + " / " + course.code();
        emit(EventType.GRADE_RECORDED, message, student.id(), course.teacherId());
    }

    public GradeResult completeCourse(Access access, String studentId, String courseCode) {
        access.require(Permission.RECORD_GRADES);
        Enrollment enrollment = enrollments.find(studentId, courseCode);
        Course course = catalog.find(enrollment.courseCode());
        GradeResult result = grades.complete(enrollment, course);
        Person student = people.find(enrollment.studentId());
        String message = student.name() + " completed " + course.code() + " with " + result.display();
        emit(EventType.COURSE_COMPLETED, message, student.id(), course.teacherId());
        return result;
    }

    public StudentReport issueReport(Access access, String studentId) {
        access.require(Permission.ISSUE_REPORTS);
        Person student = people.student(studentId);
        List<String> lines = new ArrayList<>();
        int credits = 0;
        for (Enrollment enrollment : enrollments.ofStudent(student.id())) {
            Course course = catalog.find(enrollment.courseCode());
            lines.add(reportLine(enrollment, course));
            if (grades.passed(enrollment, course)) {
                credits += course.credits();
            }
        }
        String message = "Report issued for " + student.name() + ", credits earned: " + credits;
        emit(EventType.REPORT_READY, message, student.id(), null);
        return new StudentReport(student.id(), student.name(), lines, credits);
    }

    /** Used by the audited access decorator to put refused actions into the activity log. */
    public void reportDeniedAccess(String message) {
        emit(EventType.ACCESS_DENIED, message, null, null);
    }

    public List<Person> people(Access access) {
        access.require(Permission.VIEW);
        return people.all();
    }

    public List<Course> courses(Access access) {
        access.require(Permission.VIEW);
        return catalog.all();
    }

    public List<Enrollment> enrollments(Access access) {
        access.require(Permission.VIEW);
        return enrollments.all();
    }

    public Course course(Access access, String code) {
        access.require(Permission.VIEW);
        return catalog.find(code);
    }

    public Enrollment enrollment(Access access, String studentId, String courseCode) {
        access.require(Permission.VIEW);
        return enrollments.find(studentId, courseCode);
    }

    public long enrolledCount(Access access, String courseCode) {
        access.require(Permission.VIEW);
        return enrollments.count(courseCode);
    }

    public String personName(Access access, String id) {
        access.require(Permission.VIEW);
        return people.find(id).name();
    }

    public Optional<GradeResult> result(Access access, Enrollment enrollment) {
        access.require(Permission.VIEW);
        return grades.result(enrollment, catalog.find(enrollment.courseCode()));
    }

    private String reportLine(Enrollment enrollment, Course course) {
        String grade = grades.result(enrollment, course).map(GradeResult::display).orElse("Not graded");
        String status = enrollment.completed() ? "completed" : "in progress";
        return course.code() + " " + course.title() + " | " + course.credits() + " credits | " + grade + " | " + status;
    }

    private boolean hasPassed(String studentId, String courseCode) {
        if (!enrollments.isEnrolled(studentId, courseCode)) {
            return false;
        }
        return grades.passed(enrollments.find(studentId, courseCode), catalog.find(courseCode));
    }

    private void emit(EventType type, String message, String studentId, String teacherId) {
        events.publish(new SchoolEvent(type, message, clock.instant(), studentId, teacherId));
    }
}
