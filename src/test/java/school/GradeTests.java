package school;

import java.util.List;
import school.grading.Assessment;
import school.grading.GradeResult;
import school.grading.GradingPolicies;
import school.grading.GradingStrategy;
import school.grading.PassFailGrading;
import school.grading.WeightedGrading;

final class GradeTests {
    static void run() {
        GradingStrategy letters = new WeightedGrading();
        GradingStrategy pass = new PassFailGrading();
        var grades = List.of(new Assessment("Project", 94, 60), new Assessment("Quiz", 82, 40));
        Checks.close(89.2, letters.calculate(grades).orElseThrow().score());
        Checks.equal("B", letters.calculate(grades).orElseThrow().label());
        Checks.equal("PASS", pass.calculate(grades).orElseThrow().label());
        Checks.equal(true, letters.calculate(List.of()).isEmpty());
        Checks.equal(true, pass.calculate(List.of()).isEmpty());

        double[] scores = {59.94, 59.96, 60, 70, 80, 89.94, 89.96, 90, 100};
        String[] expected = {"F", "D", "D", "C", "B", "B", "A", "A", "A"};
        for (int i = 0; i < scores.length; i++) {
            var list = List.of(new Assessment("Exam", scores[i], 1));
            Checks.equal(expected[i], letters.calculate(list).orElseThrow().label());
            Checks.equal(i == 0 ? "FAIL" : "PASS", pass.calculate(list).orElseThrow().label());
        }

        GradeResult rounded = letters.calculate(List.of(new Assessment("Exam", 89.96, 1))).orElseThrow();
        Checks.equal("90.0 / A", rounded.display());
        Checks.equal(true, rounded.passed());
        Checks.equal(false, new GradeResult(59.9, "F").passed());

        var large = List.of(new Assessment("One", 80, Double.MAX_VALUE), new Assessment("Two", 100, Double.MAX_VALUE));
        Checks.close(90, letters.calculate(large).orElseThrow().score());

        Checks.equal("Weighted letters", GradingPolicies.byName("letters").name());
        Checks.equal("Pass / Fail", GradingPolicies.byName("pass").name());
        Checks.equal("letters/pass", GradingPolicies.names());
        Checks.fails(IllegalArgumentException.class, () -> GradingPolicies.byName("stars"));
    }
}
