package school;

import java.util.List;
import java.util.Optional;
public final class WeightedGrading implements GradingStrategy {
    @Override public Optional<GradeResult> calculate(List<Assessment> items) {
        if (items.isEmpty()) return Optional.empty();
        double score = GradeMath.average(items);
        String letter = score >= 90 ? "A" : score >= 80 ? "B" : score >= 70 ? "C" : score >= 60 ? "D" : "F";
        return Optional.of(new GradeResult(score, letter));
    }
    @Override public String name() { return "Weighted letters"; }
}
