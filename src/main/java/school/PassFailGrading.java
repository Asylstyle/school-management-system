package school;

import java.util.List;
import java.util.Optional;
public final class PassFailGrading implements GradingStrategy {
    @Override public Optional<GradeResult> calculate(List<Assessment> items) {
        if (items.isEmpty()) return Optional.empty();
        double score = GradeMath.average(items);
        return Optional.of(new GradeResult(score, score >= 60 ? "PASS" : "FAIL"));
    }
    @Override public String name() { return "Pass / Fail"; }
}
