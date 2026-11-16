package school.core;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import school.common.Validation;

public final class CourseCatalog {
    private final Map<String, Course> courses = new LinkedHashMap<>();

    public void add(Course course) {
        Objects.requireNonNull(course);
        if (courses.containsKey(course.code())) {
            throw new IllegalArgumentException("This course code already exists");
        }
        courses.put(course.code(), course);
    }

    public boolean contains(String code) {
        return courses.containsKey(Validation.id(code, "Course code"));
    }

    public Course find(String code) {
        Course course = courses.get(Validation.id(code, "Course code"));
        if (course == null) {
            throw new IllegalArgumentException("Course not found");
        }
        return course;
    }

    public List<Course> all() {
        return List.copyOf(courses.values());
    }
}
