package school.grading;

import java.util.List;
import java.util.Optional;

public final class WeightedGrading implements GradingStrategy {
    @Override
    public Optional<GradeResult> calculate(List<Assessment> assessments) {
        if (assessments.isEmpty()) {
            return Optional.empty();
        }
        double score = GradeMath.average(assessments);
        return Optional.of(new GradeResult(score, letter(score)));
    }

    @Override
    public String name() {
        return "Weighted letters";
    }

    private String letter(double score) {
        if (score >= 90) {
            return "A";
        }
        if (score >= 80) {
            return "B";
        }
        if (score >= 70) {
            return "C";
        }
        if (score >= GradeMath.PASS_SCORE) {
            return "D";
        }
        return "F";
    }
}
