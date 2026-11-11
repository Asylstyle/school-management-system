package school;

import java.time.Instant;
public record SchoolEvent(EventType type, String message, Instant at) {
    public SchoolEvent {
        java.util.Objects.requireNonNull(type);
        java.util.Objects.requireNonNull(at);
        message = Validation.text(message, "Event message");
    }
}
