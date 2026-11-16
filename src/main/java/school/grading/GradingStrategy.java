package school.grading;

import java.util.List;
import java.util.Optional;

public interface GradingStrategy {
    Optional<GradeResult> calculate(List<Assessment> assessments);

    String name();
}
