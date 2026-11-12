package school;

import java.util.LinkedHashMap;
import java.util.Map;
final class SchoolStore {
    record Key(String student, String course) {}
    final Map<String, Person> people = new LinkedHashMap<>();
    final Map<String, Course> courses = new LinkedHashMap<>();
    final Map<Key, Enrollment> enrollments = new LinkedHashMap<>();
}
