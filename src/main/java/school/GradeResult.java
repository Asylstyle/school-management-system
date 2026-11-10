package school;

import java.util.Locale;
public record GradeResult(double score, String label) {
    public String display() { return String.format(Locale.ROOT, "%.1f / %s", score, label); }
}
