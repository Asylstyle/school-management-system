package school.people;

/** Factory Method: each subclass decides which kind of person to create. */
public abstract class PersonCreator {
    public abstract Person create(String id, String name);
}
