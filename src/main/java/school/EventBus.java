package school;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
public final class EventBus {
    private final List<SchoolListener> listeners = new ArrayList<>();
    private final Consumer<RuntimeException> onFailure;
    public EventBus() { this(e -> System.err.println("Notification failed: " + e.getMessage())); }
    public EventBus(Consumer<RuntimeException> onFailure) { this.onFailure = Objects.requireNonNull(onFailure); }
    public void subscribe(SchoolListener listener) {
        Objects.requireNonNull(listener);
        if (!listeners.contains(listener)) listeners.add(listener);
    }
    public void unsubscribe(SchoolListener listener) { listeners.remove(listener); }
    public void publish(SchoolEvent event) {
        Objects.requireNonNull(event);
        for (SchoolListener listener : List.copyOf(listeners)) {
            try { listener.onEvent(event); }
            catch (RuntimeException e) {
                try { onFailure.accept(e); } catch (RuntimeException ignored) { /* Keep delivering committed events. */ }
            }
        }
    }
}
