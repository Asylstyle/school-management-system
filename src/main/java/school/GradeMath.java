package school;

import java.util.List;
final class GradeMath {
    private GradeMath() {}
    static double average(List<Assessment> items) {
        double maxWeight = items.stream().mapToDouble(Assessment::weight).max().orElseThrow();
        double total = 0, weights = 0;
        for (Assessment a : items) {
            double normalized = a.weight() / maxWeight;
            total += a.score() * normalized;
            weights += normalized;
        }
        return total / weights;
    }
}
