package school;

public final class Student extends Person {
    public Student(String id, String name) { super(id, name); }
    @Override public Role role() { return Role.STUDENT; }
}
