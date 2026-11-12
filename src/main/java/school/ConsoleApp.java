package school;

import java.nio.file.Path;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public final class ConsoleApp {
    private final SchoolFacade school;
    private final ActivityLog log;
    private final ConsoleInput input;
    private Access access = DemoData.administrator();

    ConsoleApp(SchoolFacade school, ActivityLog log, Scanner scanner) {
        this.school = school;
        this.log = log;
        this.input = new ConsoleInput(scanner);
    }

    public static void main(String[] args) {
        EventBus events = new EventBus();
        ActivityLog log = new ActivityLog();
        events.subscribe(log);
        SchoolFacade school = new SchoolFacade(events);
        DemoData.load(school);
        ConsoleApp app = new ConsoleApp(school, log, new Scanner(System.in));
        if (args.length > 0 && args[0].equals("--demo")) {
            app.overview();
            app.people();
            app.courses();
            app.gradebook();
            app.activity();
            return;
        }
        app.run();
    }

    void run() {
        System.out.println("SchoolDesk | School management");
        System.out.println("Sample data loaded. Changes last until exit; export the gradebook to keep results.");
        while (true) {
            System.out.println("\nAccess: " + access.label());
            System.out.println("1 Overview       2 People         3 Add person");
            System.out.println("4 Courses        5 Add course     6 Enroll student");
            System.out.println("7 Gradebook      8 Record grade   9 Activity");
            System.out.println("10 Access mode   11 Export CSV    0 Exit");
            try {
                switch (input.text("Choose")) {
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
                    case "0" -> { return; }
                    default -> System.out.println("Choose one of the listed options.");
                }
            } catch (ConsoleInput.EndOfInput e) {
                return;
            } catch (IllegalArgumentException | SecurityException e) {
                System.out.println("Cannot complete: " + e.getMessage());
            }
        }
    }

    private void overview() {
        System.out.println("\n--- SCHOOL OVERVIEW ---");
        System.out.println("Students: " + school.people().stream().filter(p -> p.role() == Role.STUDENT).count());
        System.out.println("Teachers: " + school.people().stream().filter(p -> p.role() == Role.TEACHER).count());
        System.out.println("Courses: " + school.courses().size());
        System.out.println("Enrollments: " + school.enrollments().size());
    }

    private void people() {
        System.out.println("\n--- PEOPLE ---");
        school.people().forEach(p -> System.out.printf("%-8s %-10s %s%n", p.id(), p.role(), p.name()));
    }

    private void addPerson() {
        access.require(Permission.MANAGE_PEOPLE);
        String type = input.text("Role (student/teacher)");
        Role role = switch (type.toLowerCase(java.util.Locale.ROOT)) {
            case "student" -> Role.STUDENT;
            case "teacher" -> Role.TEACHER;
            default -> throw new IllegalArgumentException("Role must be student or teacher");
        };
        school.register(access, role, input.text("ID"), input.text("Full name"));
        System.out.println("Person registered.");
    }

    private void courses() {
        System.out.println("\n--- COURSES ---");
        for (Course c : school.courses()) {
            System.out.printf("%s | %s | %s | %d/%d seats | %s%n", c.code(), c.title(),
                    school.personName(c.teacherId()), school.enrolledCount(c.code()), c.capacity(), c.grading().name());
        }
    }

    private void addCourse() {
        access.require(Permission.MANAGE_COURSES);
        String code = input.text("Code");
        String title = input.text("Title");
        String teacher = input.text("Teacher ID");
        int capacity = input.integer("Capacity");
        GradingStrategy grading = switch (input.text("Grading (letters/pass)")) {
            case "letters" -> new WeightedGrading();
            case "pass" -> new PassFailGrading();
            default -> throw new IllegalArgumentException("Choose letters or pass");
        };
        school.addCourse(access, Course.builder(code, title).teacher(teacher).capacity(capacity).grading(grading).build());
        System.out.println("Course created.");
    }

    private void enroll() {
        access.require(Permission.ENROLL);
        school.enroll(access, input.text("Student ID"), input.text("Course code"));
        System.out.println("Enrollment completed.");
    }

    private void gradebook() {
        System.out.println("\n--- GRADEBOOK ---");
        for (Enrollment e : school.enrollments()) {
            System.out.printf("%-18s %-8s %s%n", school.personName(e.studentId()), e.courseCode(),
                    school.result(e).map(GradeResult::display).orElse("Not graded"));
            for (Assessment a : e.assessments()) {
                System.out.printf(java.util.Locale.ROOT, "  %s: %.1f (weight %.1f)%n", a.title(), a.score(), a.weight());
            }
        }
    }

    private void recordGrade() {
        access.require(Permission.RECORD_GRADES);
        String student = input.text("Student ID");
        String course = input.text("Course code");
        Assessment assessment = new Assessment(input.text("Assessment title"), input.decimal("Score (0-100)"), input.decimal("Weight"));
        school.recordGrade(access, student, course, assessment);
        System.out.println("Grade recorded.");
    }

    private void activity() {
        System.out.println("\n--- ACTIVITY (UTC+05:00) ---");
        DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneOffset.ofHours(5));
        log.events().forEach(e -> System.out.println(format.format(e.at()) + " | " + e.message()));
    }

    private void changeAccess() {
        access = switch (input.text("Mode (viewer/registrar/grading/admin)")) {
            case "viewer" -> new ReadOnlyAccess();
            case "registrar" -> new RegistrarAccess(new ReadOnlyAccess());
            case "grading" -> new GradingAccess(new ReadOnlyAccess());
            case "admin" -> DemoData.administrator();
            default -> throw new IllegalArgumentException("Unknown access mode");
        };
    }

    private void export() {
        Path path = Path.of(input.text("CSV file path"));
        if (java.nio.file.Files.exists(path) && !input.text("File exists. Overwrite? (yes/no)").equals("yes")) return;
        try {
            CsvExport.write(school, path);
            System.out.println("Saved: " + path.toAbsolutePath());
        } catch (java.io.IOException e) {
            System.out.println("Export failed: " + e.getMessage());
        }
    }
}
