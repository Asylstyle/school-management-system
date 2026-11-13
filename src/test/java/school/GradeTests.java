package school;

import java.util.List;

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
        String[] expected = {"F", "D", "C", "B", "A", "A"};
        double[] scores = {59.99, 60, 70, 80, 90, 100};
        for (int i = 0; i < scores.length; i++) {
            var list = List.of(new Assessment("Exam", scores[i], 1));
            Checks.equal(expected[i], letters.calculate(list).orElseThrow().label());
            Checks.equal(i == 0 ? "FAIL" : "PASS", pass.calculate(list).orElseThrow().label());
        }
        var large = List.of(new Assessment("One", 80, Double.MAX_VALUE), new Assessment("Two", 100, Double.MAX_VALUE));
        Checks.close(90, letters.calculate(large).orElseThrow().score());
    }
}
