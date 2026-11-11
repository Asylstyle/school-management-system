package school;

import java.util.Objects;
public final class Course {
    private final String code, title, teacherId;
    private final int capacity;
    private final GradingStrategy grading;
    private Course(Builder b) {
        code = Validation.text(b.code, "Course code");
        title = Validation.text(b.title, "Course title");
        teacherId = Validation.text(b.teacherId, "Teacher ID");
        capacity = Validation.positive(b.capacity, "Capacity");
        grading = Objects.requireNonNull(b.grading, "Grading policy is required");
    }
    public String code() { return code; }
    public String title() { return title; }
    public String teacherId() { return teacherId; }
    public int capacity() { return capacity; }
    public GradingStrategy grading() { return grading; }
    @Override public String toString() { return code + " - " + title; }
    public static Builder builder(String code, String title) { return new Builder(code, title); }
    public static final class Builder {
        private final String code, title;
        private String teacherId;
        private int capacity = 25;
        private GradingStrategy grading = new WeightedGrading();
        private Builder(String code, String title) { this.code = code; this.title = title; }
        public Builder teacher(String id) { teacherId = id; return this; }
        public Builder capacity(int value) { capacity = value; return this; }
        public Builder grading(GradingStrategy value) { grading = value; return this; }
        public Course build() { return new Course(this); }
    }
}
