package school;

import java.time.Clock;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
public final class SchoolFacade {
    private final SchoolStore store = new SchoolStore();
    private final EventBus events;
    private final Clock clock;
    public SchoolFacade(EventBus events) { this(events, Clock.systemUTC()); }
    public SchoolFacade(EventBus events, Clock clock) {
        this.events = Objects.requireNonNull(events); this.clock = Objects.requireNonNull(clock);
    }
    public Person register(Access access, Role role, String id, String name) {
        access.require(Permission.MANAGE_PEOPLE);
        PersonCreator creator = switch (Objects.requireNonNull(role)) {
            case STUDENT -> new StudentCreator(); case TEACHER -> new TeacherCreator();
        };
        Person person = creator.register(id, name);
        if (store.people.containsKey(person.id())) throw new IllegalArgumentException("This person ID already exists");
        store.people.put(person.id(), person);
        emit(EventType.PERSON_REGISTERED, person.name() + " registered as " + person.role());
        return person;
    }
    public void addCourse(Access access, Course course) {
        access.require(Permission.MANAGE_COURSES);
        Objects.requireNonNull(course);
        if (!(store.people.get(course.teacherId()) instanceof Teacher))
            throw new IllegalArgumentException("Select a registered teacher");
        if (store.courses.containsKey(course.code())) throw new IllegalArgumentException("This course code already exists");
        store.courses.put(course.code(), course);
        emit(EventType.COURSE_CREATED, course.code() + " created: " + course.title());
    }
    public void enroll(Access access, String studentId, String courseCode) {
        access.require(Permission.ENROLL);
        Course course = course(courseCode);
        if (!(store.people.get(studentId) instanceof Student)) throw new IllegalArgumentException("Select a registered student");
        var key = new SchoolStore.Key(studentId, courseCode);
        if (store.enrollments.containsKey(key)) throw new IllegalArgumentException("Student is already enrolled");
        if (enrolledCount(courseCode) >= course.capacity()) throw new IllegalArgumentException("Course is full");
        store.enrollments.put(key, new Enrollment(studentId, courseCode));
        emit(EventType.STUDENT_ENROLLED, store.people.get(studentId).name() + " enrolled in " + courseCode);
    }
    public void recordGrade(Access access, String studentId, String courseCode, Assessment assessment) {
        access.require(Permission.RECORD_GRADES);
        Enrollment enrollment = enrollment(studentId, courseCode);
        Objects.requireNonNull(assessment);
        if (enrollment.assessments().stream().anyMatch(a -> a.title().equalsIgnoreCase(assessment.title())))
            throw new IllegalArgumentException("An assessment with this title already exists for this enrollment");
        enrollment.add(assessment);
        emit(EventType.GRADE_RECORDED, assessment.title() + ": " + assessment.score() + " for " + store.people.get(studentId).name() + " / " + courseCode);
    }
    public List<Person> people() { return List.copyOf(store.people.values()); }
    public List<Course> courses() { return List.copyOf(store.courses.values()); }
    public List<Enrollment> enrollments() { return List.copyOf(store.enrollments.values()); }
    public long enrolledCount(String code) { return store.enrollments.values().stream().filter(e -> e.courseCode().equals(code)).count(); }
    public Course course(String code) {
        Course c = store.courses.get(code);
        if (c == null) throw new IllegalArgumentException("Course not found");
        return c;
    }
    public Enrollment enrollment(String student, String course) {
        Enrollment e = store.enrollments.get(new SchoolStore.Key(student, course));
        if (e == null) throw new IllegalArgumentException("Student is not enrolled in this course");
        return e;
    }
    public Optional<GradeResult> result(Enrollment enrollment) {
        return course(enrollment.courseCode()).grading().calculate(enrollment.assessments());
    }
    public String personName(String id) {
        Person p = store.people.get(id);
        if (p == null) throw new IllegalArgumentException("Person not found");
        return p.name();
    }
    private void emit(EventType type, String message) { events.publish(new SchoolEvent(type, message, clock.instant())); }
}
