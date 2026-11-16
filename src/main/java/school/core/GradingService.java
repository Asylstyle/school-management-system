package school.core;

import java.util.Objects;
import java.util.Optional;
import school.grading.Assessment;
import school.grading.GradeResult;

public final class GradingService {
    public void record(Enrollment enrollment, Assessment assessment) {
        Objects.requireNonNull(assessment);
        for (Assessment existing : enrollment.assessments()) {
            if (existing.title().equalsIgnoreCase(assessment.title())) {
                throw new IllegalArgumentException("An assessment with this title already exists for this enrollment");
            }
        }
        enrollment.add(assessment);
    }

    public Optional<GradeResult> result(Enrollment enrollment, Course course) {
        return course.grading().calculate(enrollment.assessments());
    }

    public boolean passed(Enrollment enrollment, Course course) {
        Optional<GradeResult> result = result(enrollment, course);
        return result.isPresent() && result.get().passed();
    }
}
