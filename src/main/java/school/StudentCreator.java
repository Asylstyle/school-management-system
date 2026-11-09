package school;

public final class StudentCreator extends PersonCreator {
    @Override protected Person create(String id, String name) { return new Student(id, name); }
}
