package school;

public abstract class Person {
    private final String id;
    private final String name;
    protected Person(String id, String name) {
        this.id = Validation.text(id, "ID");
        this.name = Validation.text(name, "Name");
    }
    public final String id() { return id; }
    public final String name() { return name; }
    public abstract Role role();
    @Override public String toString() { return name + " (" + id + ")"; }
}
