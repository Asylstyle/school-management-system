package school.grading;

import java.util.Locale;

public record GradeResult(double score, String label) {
    public boolean passed() {
        return score >= GradeMath.PASS_SCORE;
    }

    public String display() {
        return String.format(Locale.ROOT, "%.1f / %s", score, label);
    }
}
