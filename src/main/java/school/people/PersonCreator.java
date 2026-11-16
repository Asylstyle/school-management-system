package school.people;

import school.common.Validation;

public abstract class PersonCreator {
    public final Person register(String id, String name) {
        String checkedId = Validation.id(id, "ID");
        String checkedName = Validation.text(name, "Name");
        return create(checkedId, checkedName);
    }

    protected abstract Person create(String id, String name);
}
