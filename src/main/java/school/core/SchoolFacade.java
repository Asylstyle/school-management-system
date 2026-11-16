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

public final class SchoolFacade {
    private final PersonRegistry people = new PersonRegistry();
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
        emit(EventType.PERSON_REGISTERED, person.name() + " registered as " + person.role(), "", "");
        return person;
    }

    public void addCourse(Access access, Course course) {
        access.require(Permission.MANAGE_COURSES);
        Objects.requireNonNull(course);
        people.teacher(course.teacherId());
        for (String code : course.prerequisites()) {
            if (!enrollments.hasCourse(code)) {
                throw new IllegalArgumentException("Prerequisite course not found: " + code);
            }
        }
        enrollments.addCourse(course);
        emit(EventType.COURSE_CREATED, course.code() + " created: " + course.title(), "", course.teacherId());
    }

    public void enroll(Access access, String studentId, String courseCode) {
        access.require(Permission.ENROLL);
        Course course = enrollments.course(courseCode);
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
        Course course = enrollments.course(enrollment.courseCode());
        Person student = people.find(enrollment.studentId());
        String message = assessment.title() + ": " + assessment.score() + " for " + student.name() + " / " + course.code();
        emit(EventType.GRADE_RECORDED, message, student.id(), course.teacherId());
    }

    public StudentReport issueReport(Access access, String studentId) {
        access.require(Permission.VIEW);
        Person student = people.student(studentId);
        List<String> lines = new ArrayList<>();
        int credits = 0;
        for (Enrollment enrollment : enrollments.ofStudent(student.id())) {
            Course course = enrollments.course(enrollment.courseCode());
            Optional<GradeResult> result = grades.result(enrollment, course);
            String grade = result.map(GradeResult::display).orElse("Not graded");
            lines.add(course.code() + " " + course.title() + " | " + course.credits() + " credits | " + grade);
            if (grades.passed(enrollment, course)) {
                credits += course.credits();
            }
        }
        String message = "Report issued for " + student.name() + ", credits earned: " + credits;
        emit(EventType.REPORT_READY, message, student.id(), "");
        return new StudentReport(student.id(), student.name(), lines, credits);
    }

    public List<Person> people(Access access) {
        access.require(Permission.VIEW);
        return people.all();
    }

    public List<Course> courses(Access access) {
        access.require(Permission.VIEW);
        return enrollments.courses();
    }

    public List<Enrollment> enrollments(Access access) {
        access.require(Permission.VIEW);
        return enrollments.all();
    }

    public Course course(Access access, String code) {
        access.require(Permission.VIEW);
        return enrollments.course(code);
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
        return grades.result(enrollment, enrollments.course(enrollment.courseCode()));
    }

    private boolean hasPassed(String studentId, String courseCode) {
        if (!enrollments.isEnrolled(studentId, courseCode)) {
            return false;
        }
        Enrollment enrollment = enrollments.find(studentId, courseCode);
        return grades.passed(enrollment, enrollments.course(courseCode));
    }

    private void emit(EventType type, String message, String studentId, String teacherId) {
        events.publish(new SchoolEvent(type, message, clock.instant(), studentId, teacherId));
    }
}
