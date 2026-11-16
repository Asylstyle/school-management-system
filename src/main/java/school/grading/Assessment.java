package school.grading;

import school.common.Validation;

public record Assessment(String title, double score, double weight) {
    public Assessment {
        title = Validation.text(title, "Assessment title");
        if (!Double.isFinite(score) || score < 0 || score > 100) {
            throw new IllegalArgumentException("Score must be between 0 and 100");
        }
        if (!Double.isFinite(weight) || weight <= 0) {
            throw new IllegalArgumentException("Weight must be a positive finite number");
        }
    }
}
