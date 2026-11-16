package school.people;

public final class TeacherCreator extends PersonCreator {
    @Override
    protected Person create(String id, String name) {
        return new Teacher(id, name);
    }
}
