package school.people;

public final class Teacher extends Person {
    Teacher(String id, String name) {
        super(id, name);
    }

    @Override
    public Role role() {
        return Role.TEACHER;
    }
}
