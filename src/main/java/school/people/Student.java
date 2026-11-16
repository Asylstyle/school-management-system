package school.people;

public final class Student extends Person {
    Student(String id, String name) {
        super(id, name);
    }

    @Override
    public Role role() {
        return Role.STUDENT;
    }
}
