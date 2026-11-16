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

        var sent = new ArrayList<String>();
        bus.subscribe(new StudentNotifier(sent::add));
        bus.subscribe(log);
        bus.publish(event);
        Checks.equal(0, sent.size());
        bus.publish(new SchoolEvent(EventType.GRADE_RECORDED, "Quiz: 82.0 for Amina Bekova / CS101", Instant.EPOCH));
        Checks.equal(1, sent.size());
        Checks.equal("Student notification | New grade Quiz: 82.0 for Amina Bekova / CS101", sent.get(0));
        Checks.equal(3, log.events().size());
        Checks.fails(NullPointerException.class, () -> new StudentNotifier(null));

        var inbox = new ArrayList<String>();
        EventBus events = new EventBus();
        ActivityLog activity = new ActivityLog();
        events.subscribe(activity);
        SchoolFacade school = new SchoolFacade(events);
        DemoData.load(school);
        events.subscribe(new StudentNotifier(inbox::add));
        int before = activity.events().size();
        school.recordGrade(DemoData.administrator(), "S002", "CS101", new Assessment("Quiz", 72, 40));
        Checks.equal(before + 1, activity.events().size());
        Checks.equal(1, inbox.size());
        Checks.fails(SecurityException.class,
                () -> school.recordGrade(new ReadOnlyAccess(), "S002", "CS101", new Assessment("Final exam", 80, 60)));
        Checks.equal(1, inbox.size());
    }
}
