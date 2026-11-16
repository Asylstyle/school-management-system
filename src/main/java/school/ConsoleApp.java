package school;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import school.access.Access;
import school.access.AccessDeniedException;
import school.access.AccessModes;
import school.access.AuditedAccess;
import school.access.Permission;
import school.core.Course;
import school.core.CsvExport;
import school.core.Enrollment;
import school.core.SchoolFacade;
import school.core.StudentReport;
import school.events.ActivityLog;
import school.events.EventBus;
import school.events.Inbox;
import school.events.SchoolEvent;
import school.events.StudentNotifier;
import school.events.TeacherNotifier;
import school.grading.Assessment;
import school.grading.GradeResult;
import school.grading.GradingPolicies;
import school.people.Person;
import school.people.Role;

public final class ConsoleApp {
    private final SchoolFacade school;
    private final ActivityLog log;
    private final Inbox inbox;
    private final ConsoleInput input;
    private Access access;

    ConsoleApp(SchoolFacade school, ActivityLog log, Inbox inbox, Scanner scanner) {
        this.school = school;
        this.log = log;
        this.inbox = inbox;
        this.input = new ConsoleInput(scanner);
        this.access = audited(AccessModes.admin());
    }

    public static void main(String[] args) {
        EventBus events = new EventBus();
        ActivityLog log = new ActivityLog();
        Inbox inbox = new Inbox();
        events.subscribe(log);
        events.subscribe(new StudentNotifier(inbox));
        events.subscribe(new TeacherNotifier(inbox));

        SchoolFacade school = new SchoolFacade(events);
        DemoData.load(school);

        ConsoleApp app = new ConsoleApp(school, log, inbox, new Scanner(System.in));
        if (args.length > 0 && args[0].equals("--demo")) {
            app.demo();
        } else {
            app.run();
        }
    }

    void run() {
        System.out.println("SchoolDesk | School management");
        System.out.println("Sample data loaded. Changes last until exit; export the gradebook to keep results.");
        boolean running = true;
        while (running) {
            printMenu();
            try {
                running = handle(input.text("Choose"));
            } catch (ConsoleInput.EndOfInput e) {
                running = false;
            } catch (IllegalArgumentException | AccessDeniedException e) {
                System.out.println("Cannot complete: " + e.getMessage());
            }
        }
    }

    private void demo() {
        overview();
        people();
        courses();
        gradebook();
        activity();
    }

    private void printMenu() {
        System.out.println("\nAccess: " + access.label());
        System.out.println("1 Overview        2 People          3 Add person");
        System.out.println("4 Courses         5 Add course      6 Enroll student");
        System.out.println("7 Gradebook       8 Record grade    9 Activity");
        System.out.println("10 Access mode    11 Export CSV     12 Issue report");
        System.out.println("13 Notifications  14 Complete course 0 Exit");
    }

    private boolean handle(String choice) {
        switch (choice) {
            case "1" -> overview();
            case "2" -> people();
            case "3" -> addPerson();
            case "4" -> courses();
            case "5" -> addCourse();
            case "6" -> enroll();
            case "7" -> gradebook();
            case "8" -> recordGrade();
            case "9" -> activity();
            case "10" -> changeAccess();
            case "11" -> export();
            case "12" -> studentReport();
            case "13" -> notifications();
            case "14" -> completeCourse();
            case "0" -> {
                return false;
            }
            default -> System.out.println("Choose one of the listed options.");
        }
        return true;
    }

    private void overview() {
        long students = 0;
        long teachers = 0;
        for (Person person : school.people(access)) {
            if (person.role() == Role.STUDENT) {
                students++;
            } else {
                teachers++;
            }
        }
        System.out.println("\n--- SCHOOL OVERVIEW ---");
        System.out.println("Students: " + students);
        System.out.println("Teachers: " + teachers);
        System.out.println("Courses: " + school.courses(access).size());
        System.out.println("Enrollments: " + school.enrollments(access).size());
    }

    private void people() {
        System.out.println("\n--- PEOPLE ---");
        for (Person person : school.people(access)) {
            System.out.printf("%-8s %-10s %s%n", person.id(), person.role(), person.name());
        }
    }

    private void addPerson() {
        access.require(Permission.MANAGE_PEOPLE);
        Role role = role(input.text("Role (student/teacher)"));
        String id = input.text("ID");
        String name = input.text("Full name");
        school.register(access, role, id, name);
        System.out.println("Person registered.");
    }

    private Role role(String name) {
        for (Role role : Role.values()) {
            if (role.name().equalsIgnoreCase(name)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Role must be student or teacher");
    }

    private void courses() {
        System.out.println("\n--- COURSES ---");
        for (Course course : school.courses(access)) {
            System.out.println(course.code() + " | " + course.title()
                    + " | " + school.personName(access, course.teacherId())
                    + " | " + school.enrolledCount(access, course.code()) + "/" + course.capacity() + " seats"
                    + " | " + course.grading().name()
                    + " | " + course.semester() + ", " + course.credits() + " credits");
            if (!course.prerequisites().isEmpty()) {
                System.out.println("  requires: " + String.join(", ", course.prerequisites()));
            }
        }
    }

    private void addCourse() {
        access.require(Permission.MANAGE_COURSES);
        String code = input.text("Code");
        String title = input.text("Title");
        String teacher = input.text("Teacher ID");
        Course.Builder builder = Course.builder(code, title, teacher);

        String capacity = input.text("Capacity (empty = default)");
        if (!capacity.isEmpty()) {
            builder.capacity(ConsoleInput.toInteger(capacity));
        }
        String grading = input.command("Grading (" + GradingPolicies.names() + ", empty = default)");
        if (!grading.isEmpty()) {
            builder.grading(GradingPolicies.byName(grading));
        }
        String semester = input.text("Semester (empty = default)");
        if (!semester.isEmpty()) {
            builder.semester(semester);
        }
        String credits = input.text("Credits (empty = default)");
        if (!credits.isEmpty()) {
            builder.credits(ConsoleInput.toInteger(credits));
        }
        String prerequisite = input.text("Prerequisite course (empty = none)");
        if (!prerequisite.isEmpty()) {
            builder.prerequisite(prerequisite);
        }

        school.addCourse(access, builder.build());
        System.out.println("Course created.");
    }

    private void enroll() {
        access.require(Permission.ENROLL);
        String student = input.text("Student ID");
        String course = input.text("Course code");
        school.enroll(access, student, course);
        System.out.println("Enrollment completed.");
    }

    private void gradebook() {
        System.out.println("\n--- GRADEBOOK ---");
        for (Enrollment enrollment : school.enrollments(access)) {
            String name = school.personName(access, enrollment.studentId());
            String result = school.result(access, enrollment).map(GradeResult::display).orElse("Not graded");
            String status = enrollment.completed() ? " (completed)" : "";
            System.out.printf("%-18s %-8s %s%s%n", name, enrollment.courseCode(), result, status);
            for (Assessment assessment : enrollment.assessments()) {
                System.out.printf(Locale.ROOT, "  %s: %.1f (weight %.1f)%n",
                        assessment.title(), assessment.score(), assessment.weight());
            }
        }
    }

    private void recordGrade() {
        access.require(Permission.RECORD_GRADES);
        String student = input.text("Student ID");
        String course = input.text("Course code");
        String title = input.text("Assessment title");
        double score = input.decimal("Score (0-100)");
        double weight = input.decimal("Weight");
        school.recordGrade(access, student, course, new Assessment(title, score, weight));
        System.out.println("Grade recorded.");
    }

    private void activity() {
        System.out.println("\n--- ACTIVITY (UTC+05:00) ---");
        DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneOffset.ofHours(5));
        for (SchoolEvent event : log.events()) {
            System.out.println(format.format(event.at()) + " | " + event.message());
        }
    }

    private void completeCourse() {
        access.require(Permission.RECORD_GRADES);
        String student = input.text("Student ID");
        String course = input.text("Course code");
        GradeResult result = school.completeCourse(access, student, course);
        System.out.println("Course completed: " + result.display());
    }

    private void changeAccess() {
        access = audited(AccessModes.byName(input.command("Mode (" + AccessModes.names() + ")")));
    }

    private Access audited(Access mode) {
        return new AuditedAccess(mode, school::reportDeniedAccess);
    }

    private void export() {
        access.require(Permission.VIEW);
        Path path = Path.of(input.text("CSV file path"));
        if (Files.exists(path) && !input.command("File exists. Overwrite? (yes/no)").equals("yes")) {
            return;
        }
        try {
            CsvExport.write(school, access, path);
            System.out.println("Saved: " + path.toAbsolutePath());
        } catch (IOException e) {
            System.out.println("Export failed: " + e.getMessage());
        }
    }

    private void studentReport() {
        access.require(Permission.ISSUE_REPORTS);
        StudentReport report = school.issueReport(access, input.text("Student ID"));
        System.out.println("\n--- REPORT: " + report.studentName() + " (" + report.studentId() + ") ---");
        for (String line : report.lines()) {
            System.out.println(line);
        }
        System.out.println("Credits earned: " + report.credits());
    }

    private void notifications() {
        String id = input.text("Person ID");
        String name = school.personName(access, id);
        List<String> messages = inbox.of(id);
        System.out.println("\n--- NOTIFICATIONS: " + name + " ---");
        if (messages.isEmpty()) {
            System.out.println("No notifications.");
        }
        for (String message : messages) {
            System.out.println(message);
        }
    }
}
