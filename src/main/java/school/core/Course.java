package school.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import school.common.Validation;
import school.grading.GradingStrategy;
import school.grading.WeightedGrading;

public final class Course {
    private final String code;
    private final String title;
    private final String teacherId;
    private final int capacity;
    private final GradingStrategy grading;
    private final String semester;
    private final int credits;
    private final List<String> prerequisites;

    private Course(Builder builder) {
        code = Validation.id(builder.code, "Course code");
        title = Validation.text(builder.title, "Course title");
        teacherId = Validation.id(builder.teacherId, "Teacher ID");
        capacity = Validation.positive(builder.capacity, "Capacity");
        grading = Objects.requireNonNull(builder.grading, "Grading policy is required");
        semester = Validation.text(builder.semester, "Semester");
        credits = Validation.positive(builder.credits, "Credits");
        prerequisites = List.copyOf(builder.prerequisites);
    }

    public static Builder builder(String code, String title, String teacherId) {
        return new Builder(code, title, teacherId);
    }

    public String code() {
        return code;
    }

    public String title() {
        return title;
    }

    public String teacherId() {
        return teacherId;
    }

    public int capacity() {
        return capacity;
    }

    public GradingStrategy grading() {
        return grading;
    }

    public String semester() {
        return semester;
    }

    public int credits() {
        return credits;
    }

    public List<String> prerequisites() {
        return prerequisites;
    }

    @Override
    public String toString() {
        return code + " - " + title;
    }

    public static final class Builder {
        private final String code;
        private final String title;
        private final String teacherId;
        private int capacity = 25;
        private GradingStrategy grading = new WeightedGrading();
        private String semester = "Fall";
        private int credits = 5;
        private final List<String> prerequisites = new ArrayList<>();

        private Builder(String code, String title, String teacherId) {
            this.code = code;
            this.title = title;
            this.teacherId = teacherId;
        }

        public Builder capacity(int value) {
            capacity = value;
            return this;
        }

        public Builder grading(GradingStrategy value) {
            grading = value;
            return this;
        }

        public Builder semester(String value) {
            semester = value;
            return this;
        }

        public Builder credits(int value) {
            credits = value;
            return this;
        }

        public Builder prerequisite(String courseCode) {
            prerequisites.add(Validation.id(courseCode, "Prerequisite code"));
            return this;
        }

        public Course build() {
            return new Course(this);
        }
    }
}
