package school;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import school.access.AccessModes;
import school.core.SchoolFacade;
import school.events.ActivityLog;
import school.events.EventBus;
import school.events.Inbox;
import school.events.StudentNotifier;
import school.events.TeacherNotifier;

final class ConsoleTests {
    static void run() {
        EventBus bus = new EventBus();
        ActivityLog log = new ActivityLog();
        Inbox inbox = new Inbox();
        bus.subscribe(log);
        bus.subscribe(new StudentNotifier(inbox));
        bus.subscribe(new TeacherNotifier(inbox));
        SchoolFacade school = new SchoolFacade(bus);
        DemoData.load(school);

        String workflow = "3\nStudent\ns9\nNew Student\n6\ns9\ncs101\n8\nS9\nCS101\nExam\n90\n1\n"
                + "14\ns9\ncs101\n7\n10\nViewer\n3\n12\n0\n";
        String wrongNumber = "5\nC2\nTitle\nT001\nwrong\n0\n";
        String defaults = "5\nc3\nShort course\nt001\n\n\n\n\n\n4\n12\ns9\n13\ns9\n13\nT001\n10\nADMIN\n0\n";
        String wrongPolicy = "5\nC4\nTitle\nT001\n\nstars\n0\n";

        PrintStream original = System.out;
        var output = new ByteArrayOutputStream();
        try (var capture = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(capture);
            new ConsoleApp(school, log, inbox, new Scanner(workflow)).run();
            new ConsoleApp(school, log, inbox, new Scanner(wrongNumber)).run();
            new ConsoleApp(school, log, inbox, new Scanner(defaults)).run();
            new ConsoleApp(school, log, inbox, new Scanner(wrongPolicy)).run();
            new ConsoleApp(school, log, inbox, new Scanner("")).run();
        } finally {
            System.setOut(original);
        }

        var admin = AccessModes.admin();
        Checks.equal("A", school.result(admin, school.enrollment(admin, "S9", "CS101")).orElseThrow().label());
        String text = output.toString(StandardCharsets.UTF_8);
        Checks.equal(true, text.contains("Access: Viewer\n") || text.contains("Access: Viewer\r\n"));
        Checks.equal(true, text.contains("Permission required: MANAGE_PEOPLE"));
        Checks.equal(true, text.contains("Permission required: ISSUE_REPORTS"));
        Checks.equal(true, text.contains("Course completed: 90.0 / A"));
        Checks.equal(true, text.contains("  Exam: 90.0 (weight 1.0)"));
        Checks.equal(true, log.events().stream()
                .anyMatch(event -> event.message().equals("Viewer was denied MANAGE_PEOPLE")));
        Checks.equal(true, text.contains("Enter a whole number"));
        Checks.equal(true, text.contains("C3 | Short course | Dana Karimova | 0/25 seats | Weighted letters | Fall, 5 credits"));
        Checks.equal(true, text.contains("--- REPORT: New Student (S9) ---"));
        Checks.equal(true, text.contains("CS101 Computer Science | 5 credits | 90.0 / A | completed"));
        Checks.equal(true, text.contains("Credits earned: 5"));
        Checks.equal(true, text.contains("New grade: Exam: 90.0 for New Student / CS101"));
        Checks.equal(true, text.contains("Report issued for New Student, credits earned: 5"));
        Checks.equal(true, text.contains("New student: New Student enrolled in CS101"));
        Checks.equal(true, text.contains("Unknown grading policy. Choose: letters/pass"));
        Checks.equal(5, school.courses(admin).size());
    }
}
