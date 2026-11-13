package school;

final class EnrollmentTests {
    static void run() {
        Enrollment enrollment = new Enrollment("S1", "C1");
        var before = enrollment.assessments();
        enrollment.add(new Assessment("Exam", 90, 1));
        Checks.equal(0, before.size());
        Checks.equal(1, enrollment.assessments().size());
        Checks.fails(UnsupportedOperationException.class, () -> enrollment.assessments().clear());
        Checks.fails(IllegalArgumentException.class, () -> new Enrollment("", "C1"));
    }
}
