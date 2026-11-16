package school.events;

@FunctionalInterface
public interface SchoolListener {
    void onEvent(SchoolEvent event);
}
