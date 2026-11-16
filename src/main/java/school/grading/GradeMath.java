package school.grading;

import java.util.List;

final class GradeMath {
    static final double PASS_SCORE = 60;

    private GradeMath() {
    }

    static double average(List<Assessment> items) {
        double maxWeight = 0;
        for (Assessment item : items) {
            maxWeight = Math.max(maxWeight, item.weight());
        }
        double total = 0;
        double weights = 0;
        for (Assessment item : items) {
            double normalized = item.weight() / maxWeight;
            total += item.score() * normalized;
            weights += normalized;
        }
        return round(total / weights);
    }

    static double round(double score) {
        return Math.round(score * 10) / 10.0;
    }
}
