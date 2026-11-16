package school.people;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import school.common.Validation;

public final class PersonRegistry {
    private final Map<Role, PersonCreator> creators = new EnumMap<>(Role.class);
    private final Map<String, Person> people = new LinkedHashMap<>();

    public PersonRegistry() {
        creators.put(Role.STUDENT, new StudentCreator());
        creators.put(Role.TEACHER, new TeacherCreator());
    }

    public Person register(Role role, String id, String name) {
        PersonCreator creator = creators.get(Objects.requireNonNull(role));
        Person person = creator.create(id, name);
        if (people.containsKey(person.id())) {
            throw new IllegalArgumentException("This person ID already exists");
        }
        people.put(person.id(), person);
        return person;
    }

    public Person find(String id) {
        Person person = people.get(Validation.id(id, "ID"));
        if (person == null) {
            throw new IllegalArgumentException("Person not found");
        }
        return person;
    }

    public Person student(String id) {
        Person person = people.get(Validation.id(id, "Student ID"));
        if (!(person instanceof Student)) {
            throw new IllegalArgumentException("Select a registered student");
        }
        return person;
    }

    public Person teacher(String id) {
        Person person = people.get(Validation.id(id, "Teacher ID"));
        if (!(person instanceof Teacher)) {
            throw new IllegalArgumentException("Select a registered teacher");
        }
        return person;
    }

    public List<Person> all() {
        return List.copyOf(people.values());
    }
}
