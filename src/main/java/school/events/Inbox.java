package school.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import school.common.Validation;

public final class Inbox {
    private final Map<String, List<String>> messages = new HashMap<>();

    public void deliver(String personId, String text) {
        String id = Validation.id(personId, "Person ID");
        messages.computeIfAbsent(id, key -> new ArrayList<>()).add(text);
    }

    public List<String> of(String personId) {
        String id = Validation.id(personId, "Person ID");
        return List.copyOf(messages.getOrDefault(id, List.of()));
    }
}
