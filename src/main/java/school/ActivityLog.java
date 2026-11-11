package school;

import java.util.ArrayList;
import java.util.List;
public final class ActivityLog implements SchoolListener {
    private final List<SchoolEvent> events = new ArrayList<>();
    @Override public void onEvent(SchoolEvent event) { events.add(event); }
    public List<SchoolEvent> events() { return List.copyOf(events); }
}
