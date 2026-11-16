package school;

import java.util.List;
import school.core.Course;
import school.grading.PassFailGrading;

final class CourseTests {
    static void run() {
        var builder = Course.builder(" cs1 ", " Computing ").teacher("t1");
        Course first = builder.build();
        Checks.equal("CS1", first.code());
        Checks.equal("Computing", first.title());
        Checks.equal("T1", first.teacherId());
        Checks.equal(25, first.capacity());
        Checks.equal("Weighted letters", first.grading().name());
        Checks.equal("Fall", first.semester());
        Checks.equal(5, first.credits());
        Checks.equal(List.of(), first.prerequisites());

        Course second = builder.capacity(10)
                .grading(new PassFailGrading())
                .semester("Spring")
                .credits(6)
                .prerequisite("cs0")
                .build();
        Checks.equal(10, second.capacity());
        Checks.equal("Pass / Fail", second.grading().name());
        Checks.equal("Spring", second.semester());
        Checks.equal(6, second.credits());
        Checks.equal(List.of("CS0"), second.prerequisites());
        Checks.equal(List.of(), first.prerequisites());
        Checks.fails(UnsupportedOperationException.class, () -> second.prerequisites().clear());

        Checks.fails(IllegalArgumentException.class, () -> Course.builder("C", "Title").build());
        Checks.fails(IllegalArgumentException.class, () -> builder.capacity(0).build());
        Checks.fails(IllegalArgumentException.class, () -> Course.builder(" ", "Title").teacher("T1").build());
        Checks.fails(IllegalArgumentException.class, () -> Course.builder("C", "Title").teacher("T1").credits(0).build());
        Checks.fails(IllegalArgumentException.class, () -> Course.builder("C", "Title").teacher("T1").semester(" ").build());
        Checks.fails(NullPointerException.class, () -> Course.builder("C", "Title").teacher("T1").grading(null).build());
    }
}
