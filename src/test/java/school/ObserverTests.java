package school;

import java.time.Instant;
import java.util.ArrayList;

final class ObserverTests {
    static void run() {
        var failures = new ArrayList<RuntimeException>();
        EventBus bus = new EventBus(failures::add);
        ActivityLog log = new ActivityLog();
        bus.subscribe(e -> { throw new IllegalStateException("Unavailable listener"); });
        bus.subscribe(log);
        bus.subscribe(log);
        SchoolEvent event = new SchoolEvent(EventType.COURSE_CREATED, "Created", Instant.EPOCH);
        bus.publish(event);
        Checks.equal(1, log.events().size());
        Checks.equal(1, failures.size());
        Checks.equal(event, log.events().get(0));
        bus.unsubscribe(log);
        bus.publish(event);
        Checks.equal(1, log.events().size());
        Checks.fails(UnsupportedOperationException.class, () -> log.events().clear());
    }
}
