package school.grading;

import java.util.List;
import java.util.Optional;

public final class PassFailGrading implements GradingStrategy {
    @Override
    public Optional<GradeResult> calculate(List<Assessment> assessments) {
        if (assessments.isEmpty()) {
            return Optional.empty();
        }
        double score = GradeMath.average(assessments);
        String label = score >= GradeMath.PASS_SCORE ? "PASS" : "FAIL";
        return Optional.of(new GradeResult(score, label));
    }

    @Override
    public String name() {
        return "Pass / Fail";
    }
}
