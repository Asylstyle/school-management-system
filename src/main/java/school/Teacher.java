package school;

public final class Teacher extends Person {
    public Teacher(String id, String name) { super(id, name); }
    @Override public Role role() { return Role.TEACHER; }
}
