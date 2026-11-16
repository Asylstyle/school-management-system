package school.events;

import java.time.Instant;
import java.util.Objects;
import school.common.Validation;

/**
 * Something that happened in the school.
 * {@code studentId} and {@code teacherId} are {@code null} when the event concerns no such person.
 */
public record SchoolEvent(EventType type, String message, Instant at, String studentId, String teacherId) {
    public SchoolEvent {
        Objects.requireNonNull(type);
        Objects.requireNonNull(at);
        message = Validation.text(message, "Event message");
    }
}
