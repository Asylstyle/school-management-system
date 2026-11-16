package school;

import school.grading.Assessment;

final class AssessmentTests {
    static void run() {
        Checks.equal("Quiz", new Assessment(" Quiz ", 0, 1).title());
        new Assessment("Exam", 100, 1);
        for (double score : new double[]{-1, 101, Double.NaN, Double.POSITIVE_INFINITY}) {
            Checks.fails(IllegalArgumentException.class, () -> new Assessment("Quiz", score, 1));
        }
        for (double weight : new double[]{0, -1, Double.NaN, Double.POSITIVE_INFINITY}) {
            Checks.fails(IllegalArgumentException.class, () -> new Assessment("Quiz", 80, weight));
        }
        Checks.fails(IllegalArgumentException.class, () -> new Assessment(" ", 80, 1));
    }
}
