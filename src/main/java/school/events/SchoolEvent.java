package school.events;

import java.time.Instant;
import java.util.Objects;
import school.common.Validation;

public record SchoolEvent(EventType type, String message, Instant at, String studentId, String teacherId) {
    public SchoolEvent {
        Objects.requireNonNull(type);
        Objects.requireNonNull(at);
        Objects.requireNonNull(studentId);
        Objects.requireNonNull(teacherId);
        message = Validation.text(message, "Event message");
    }
}
