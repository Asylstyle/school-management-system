package school.core;

import java.util.Objects;
import java.util.Optional;
import school.grading.Assessment;
import school.grading.GradeResult;

public final class GradingService {
    public void record(Enrollment enrollment, Assessment assessment) {
        Objects.requireNonNull(assessment);
        requireInProgress(enrollment);
        if (hasAssessment(enrollment, assessment.title())) {
            throw new IllegalArgumentException("An assessment with this title already exists for this enrollment");
        }
        enrollment.add(assessment);
    }

    public GradeResult complete(Enrollment enrollment, Course course) {
        requireInProgress(enrollment);
        GradeResult result = result(enrollment, course)
                .orElseThrow(() -> new IllegalArgumentException("Record at least one grade before completing the course"));
        enrollment.complete();
        return result;
    }

    public Optional<GradeResult> result(Enrollment enrollment, Course course) {
        return course.grading().calculate(enrollment.assessments());
    }

    /** A course counts as passed only after it is completed with a passing score. */
    public boolean passed(Enrollment enrollment, Course course) {
        return enrollment.completed() && result(enrollment, course).map(GradeResult::passed).orElse(false);
    }

    private void requireInProgress(Enrollment enrollment) {
        if (enrollment.completed()) {
            throw new IllegalArgumentException("This course is already completed");
        }
    }

    private boolean hasAssessment(Enrollment enrollment, String title) {
        for (Assessment existing : enrollment.assessments()) {
            if (existing.title().equalsIgnoreCase(title)) {
                return true;
            }
        }
        return false;
    }
}
