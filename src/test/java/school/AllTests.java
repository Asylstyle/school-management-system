package school;

import java.util.List;

public final class AllTests {
    private interface Group {
        void run() throws Exception;
    }

    public static void main(String[] args) throws Exception {
        List<Group> groups = List.of(
                ProfileTests::run,
                AccessTests::run,
                AssessmentTests::run,
                GradeTests::run,
                CourseTests::run,
                EnrollmentTests::run,
                ObserverTests::run,
                SchoolTests::run,
                ExportTests::run,
                ConsoleTests::run);
        for (Group group : groups) {
            group.run();
        }
        System.out.println("PASS: " + Checks.count() + " checks across " + groups.size() + " test groups");
    }
}
