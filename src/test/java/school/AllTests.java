package school;

public final class AllTests {
    public static void main(String[] args) throws Exception {
        ProfileTests.run();
        AccessTests.run();
        AssessmentTests.run();
        GradeTests.run();
        CourseTests.run();
        EnrollmentTests.run();
        ObserverTests.run();
        SchoolTests.run();
        ExportTests.run();
        ConsoleTests.run();
        System.out.println("PASS: " + Checks.count() + " checks across 10 test groups");
    }
}
