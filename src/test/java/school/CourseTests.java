package school;

final class CourseTests {
    static void run() {
        var builder = Course.builder(" CS1 ", " Computing ").teacher("T1");
        Course first = builder.build();
        Course second = builder.capacity(10).grading(new PassFailGrading()).build();
        Checks.equal("CS1", first.code());
        Checks.equal(25, first.capacity());
        Checks.equal(10, second.capacity());
        Checks.equal("Weighted letters", first.grading().name());
        Checks.equal("Pass / Fail", second.grading().name());
        Checks.fails(IllegalArgumentException.class, () -> Course.builder("C", "Title").build());
        Checks.fails(IllegalArgumentException.class, () -> builder.capacity(0).build());
        Checks.fails(IllegalArgumentException.class, () -> Course.builder(" ", "Title").teacher("T1").build());
    }
}
