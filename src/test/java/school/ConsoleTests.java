package school;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

final class ConsoleTests {
    static void run() {
        EventBus bus = new EventBus();
        ActivityLog log = new ActivityLog();
        bus.subscribe(log);
        SchoolFacade school = new SchoolFacade(bus);
        DemoData.load(school);
        String commands = "3\nstudent\nS9\nNew Student\n6\nS9\nCS101\n8\nS9\nCS101\nExam\n90\n1\n10\nviewer\n3\n0\n";
        PrintStream original = System.out;
        var output = new ByteArrayOutputStream();
        try (var capture = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(capture);
            new ConsoleApp(school, log, new Scanner(commands)).run();
            new ConsoleApp(school, log, new Scanner("5\nC2\nTitle\nT001\nwrong\n0\n")).run();
            new ConsoleApp(school, log, new Scanner("")).run();
        } finally { System.setOut(original); }
        Checks.equal("A", school.result(school.enrollment("S9", "CS101")).orElseThrow().label());
        String text = output.toString(StandardCharsets.UTF_8);
        Checks.equal(true, text.contains("Permission required"));
        Checks.equal(true, text.contains("Enter a whole number"));
    }
}
