package school;

public abstract class PersonCreator {
    public final Person register(String id, String name) {
        return create(Validation.text(id, "ID"), Validation.text(name, "Name"));
    }
    protected abstract Person create(String id, String name);
}
